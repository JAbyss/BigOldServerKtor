package com.foggyskies.server.plugin

import com.foggyskies.server.extendfun.generateUUID
import com.foggyskies.server.plugin.NewTaskManager.SmallListSort.quickSortLomuto
import kotlinx.coroutines.*
import java.util.LinkedList
import java.util.SortedMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.properties.Delegates

object TaskManager {

    data class Task(
        val code: String,
        val duration: Long,
        val before_action: (suspend () -> Unit)? = null,
        val after_action: suspend () -> Unit
    )

    private val tasks = ConcurrentHashMap<String, Deferred<Unit>>()

    private fun stop(code: String) {
        tasks.remove(code)?.cancel()
    }

    private suspend fun startTask(task: Task) =
        coroutineScope {
            async {
                task.before_action?.let { it() }
                delay(task.duration)
                task.after_action()
                stop(task.code)
            }
        }

    @OptIn(DelicateCoroutinesApi::class)
    fun addTask(task: Task) {
        CoroutineScope(newSingleThreadContext("task-thread")).launch {
            tasks[task.code] = startTask(task)
        }
    }

    fun cancelTask(nameTask: String) {
        tasks.remove(nameTask)?.cancel()
    }
}

object NewTaskManager {

    private val Scope = CoroutineScope(newSingleThreadContext("task-thread"))
    private const val CHECK_DELAY = 100L
    private var lastExecutionTask = 0L
    private val listQueue = mutableListOf<Task>()
    private val nowTime
        get() = System.currentTimeMillis()


    class Task(
    ) {
        val id = generateUUID(10)

        var time by Delegates.notNull<Long>()
        lateinit var action: () -> Unit

        constructor(time: Long, action: () -> Unit) : this() {
            this.time = time + nowTime
            this.action = action
            return
        }
    }

    fun addTaskToQueue(task: Task) {
        listQueue.add(task)
    }

    init {
        Scope.launch {
            while (true) {
                if (listQueue.isEmpty()) {
                    delay(1.s)
                } else {
                    val sortedList = listQueue.quickSortLomuto()
                    val listForDelete = mutableListOf<Task>()
                    sortedList.forEach { task ->
                        val currentTime = nowTime
                        if (task.time < currentTime) {
                            task.action()
                            lastExecutionTask = nowTime
                            listForDelete.add(task)
                        } else
                            return@forEach
                    }

                    listForDelete.forEach {
                        listQueue.remove(it)
                    }

                    listForDelete.clear()
                    delay(if (lastExecutionTask + 10.m >= System.currentTimeMillis()) 1.s else CHECK_DELAY)
                }
            }
        }
    }

    object SmallListSort {

        fun sortByJ(array: MutableList<Task>, start: Int, end: Int): Int {
            var left = start
            var current = start
            while (current < end) {
                if (array[current].time <= array[end].time) {
                    val temp = array[left]
                    array[left] = array[current]
                    array[current] = temp
                    left++
                }
                current++
            }
            val temp = array[left]
            array[left] = array[end]
            array[end] = temp
            return left
        }

        fun quickSortLomuto(
            arr: MutableList<Task>,
            start: Int,
            end: Int
        ): MutableList<Task> {
            if (start >= end) return arr
            val rightStart = sortByJ(arr, start, end)
            quickSortLomuto(arr, start, rightStart - 1)
            quickSortLomuto(arr, rightStart + 1, end)
            return arr
        }

        fun MutableList<Task>.quickSortLomuto(): MutableList<Task> {
            return quickSortLomuto(this, 0, this.size - 1)
        }
    }
}