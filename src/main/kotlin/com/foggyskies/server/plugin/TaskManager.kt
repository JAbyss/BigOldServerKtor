package com.foggyskies.server.plugin

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

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

    fun cancelTask(nameTask: String){
        tasks.remove(nameTask)?.cancel()
    }
}