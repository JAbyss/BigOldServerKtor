package com.foggyskies

import com.foggyskies.plugin.configureRouting
import com.foggyskies.plugin.configureSecurity
import com.foggyskies.plugin.configureSockets
import com.foggyskies.plugin.mainModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.Koin
import org.litote.kmongo.coroutine.CoroutineDatabase

//fun main() {
//
//    Json.encodeToString(FormattedItem<Char>(item = 'A', isVisible = false))
//
//    fun <T> List<T>.transformToFormattedItem(): MutableList<FormattedItem<T>> {
//        val newList = mutableListOf<FormattedItem<T>>()
//        this.forEach { item ->
//            val newItem = FormattedItem(
//                item = item,
//                isVisible = false
//            )
//            newList.add(newItem)
//        }
//        return newList
//    }
//
//    fun <T> MutableList<T>.processingList(work: OldListInfo<T>){
//            work.newItemsList.forEach {
//                this.add(it)
//            }
//    }
//
//    fun firstInit() {
//        newList = mutableListOf('A', 'B', 'C', 'D')
//        oldList = newList
//        transformedList = newList.transformToFormattedItem()
//        println("newList - $newList\n oldLsit - $oldList \n transformedList - $transformedList")
//    }
//
//    fun secondStage(){
//        newList = mutableListOf('A', 'V', 'V', 'D')
//        checkOldListByNewList(oldList, newList)
//    }
//
//
//
//    val oldList = mutableListOf<Char>('А', 'Г', 'Е', 'Д', 'F', 'L', 'Y', 'X')
//    val newList = mutableListOf<Char>('А', 'Е', 'Ж', 'М', 'X', 'V')
//
//    val result = checkOldListByNewList(oldList, newList)
//
//    println(result)
//
//}

enum class DataBases {
    MAIN, MESSAGES, SUBSCRIBERS, CONTENT, NEW_MESSAGE
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {

    install(Koin) {
        modules(mainModule)
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    configureSockets()
    configureRouting()
    configureSecurity()
}