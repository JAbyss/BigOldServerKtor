package com.foggyskies

import com.foggyskies.chat.data.*
import com.foggyskies.chat.data.bettamodels.Notification
import com.foggyskies.chat.datanew.AllCollectionImpl
import com.foggyskies.chat.newroom.*
import com.foggyskies.plugin.configureRouting
import com.foggyskies.plugin.configureSecurity
import com.foggyskies.plugin.configureSockets
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

@kotlinx.serialization.Serializable
data class Message(
    var user: String,
    var message: String
)

@kotlinx.serialization.Serializable
data class TestCollection(
    @BsonId
    val id: String = ObjectId().toString(),
    var name: String
)

data class OldListInfo<T>(
    var equalItemsList: MutableList<T>,
    var newItemsList: MutableList<T>,
    var depricatedItemsList: MutableList<T>
)

data class FormattedItem<T>(
    var item: T,
    var isVisible: Boolean
)

var newList = mutableListOf<Char>()
var oldList = mutableListOf<Char>()
var transformedList = mutableListOf<FormattedItem<Char>>()

fun <T> checkOldListByNewList(oldList: MutableList<T>, newList: MutableList<T>): OldListInfo<T> {
    val equalItemsList = mutableListOf<T>()
    val newItemsList = mutableListOf<T>()
    val depricatedItemsList = mutableListOf<T>()

    newList.forEach { item ->
        if (oldList.contains(item)) {
            equalItemsList.add(item)
        } else {
            newItemsList.add(item)
        }
    }
    if (oldList != equalItemsList) {
        depricatedItemsList.addAll(oldList - newList)
    }
    val result = OldListInfo(
        newItemsList = newItemsList,
        equalItemsList = equalItemsList,
        depricatedItemsList = depricatedItemsList
    )

    return result
}

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

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

data class ChatSession(
    val username: String,
    val sessionID: String
)

@Suppress("unused")
fun Application.module() {

    install(Koin) {
        modules(mainModule)
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    configureSockets()
    configureRouting()
    configureSecurity()
}

val mainModule = module {
    single {
        KMongo.createClient()
            .coroutine
            .getDatabase("petapp_db")
    }
//    factory {
//        KMongo.createClient()
//            .coroutine
//            .getDatabase("petapp_db")
//    }
    single {
//        val a =
        AllCollectionImpl(get())
    }
    single {
        UserRoutController(get(), get())
    }
    single {
        CreateChatRoutController(get(), get())
    }
    single {
        NotifyRoutController(get(), get())
    }
    single {
        MessagesRoutController(get(), get())
    }
    single {
        AuthRoutController(get(), get())
    }
//    single<AuthDataSource> {
//        AuthDataSourceImpl(get())
//    }
//    single {
//        AuthRoomController(get())
//    }
}