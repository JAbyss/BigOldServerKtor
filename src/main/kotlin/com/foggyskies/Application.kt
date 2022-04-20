package com.foggyskies

import com.foggyskies.chat.databases.content.ContentImpl
import com.foggyskies.chat.databases.main.AllCollectionImpl
import com.foggyskies.chat.databases.message.MessagesDBImpl
import com.foggyskies.chat.databases.subscribers.SubscribersImpl
import com.foggyskies.chat.newroom.*
import com.foggyskies.plugin.configureRouting
import com.foggyskies.plugin.configureSecurity
import com.foggyskies.plugin.configureSockets
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.litote.kmongo.coroutine.CoroutineDatabase
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
            ignoreUnknownKeys = true
        })
    }
    configureSockets()
    configureRouting()
    configureSecurity()
}

enum class DataBases {
    MAIN, MESSAGES, SUBSCRIBERS, CONTENT
}

//enum class ImplDB{
//    CONTENT
//}

data class ImpAndDB<I>(
    val db: CoroutineDatabase,
    val impl: I
)

val mainModule = module {
    single(named(DataBases.MAIN)) {
        KMongo.createClient()
            .coroutine
            .getDatabase("petapp_db")
    }
    single(named(DataBases.MESSAGES)) {
        KMongo.createClient()
            .coroutine
            .getDatabase("messages_petapp_db")
    }
    single(named(DataBases.SUBSCRIBERS)) {
        KMongo.createClient()
            .coroutine
            .getDatabase("subscribers_petapp_db")
    }
    single(named(DataBases.CONTENT)) {
        KMongo.createClient()
            .coroutine
            .getDatabase("content_users_petapp_db")
    }
//  ----------------------------------------------------------------------------

    single(named<ContentImpl>()) {
        ImpAndDB<ContentImpl>(db = get(named(DataBases.CONTENT)), impl = get())
    }
    single(named<MessagesDBImpl>()) {
        ImpAndDB<MessagesDBImpl>(db = get(named(DataBases.MESSAGES)), impl = get())
    }
    single(named<AllCollectionImpl>()) {
        ImpAndDB<AllCollectionImpl>(db = get(named(DataBases.MAIN)), impl = get())
    }
    single(named<SubscribersImpl>()) {
        ImpAndDB<SubscribersImpl>(db = get(named(DataBases.SUBSCRIBERS)), impl = get())
    }
//  ----------------------------------------------------------------------------
    single {
        ContentImpl(get(named(DataBases.CONTENT)))
    }
    single {
        SubscribersImpl(get(named(DataBases.SUBSCRIBERS)))
    }
    single {
        MessagesDBImpl(get(named(DataBases.MESSAGES)))
    }
    single {
        AllCollectionImpl(get(named(DataBases.MAIN)))
    }
    single {
        UserRoutController(
            content = get(named<ContentImpl>()),
            main = get(named<AllCollectionImpl>()),
            message = get(named<MessagesDBImpl>()),
            subscribers = get(named<SubscribersImpl>())
        )
    }
    single {
        CreateChatRoutController(
            allCollectionImpl = get(),
            messagesDB = get(named(DataBases.MESSAGES))
        )
    }
    single {
        NotifyRoutController(
            allCollectionImpl = get(),
            db = get(named(DataBases.MAIN))
        )
    }
    single {
        MessagesRoutController(
            allCollectionImpl = get(),
            messagesDBImpl = get()
        )
    }
    single {
        AuthRoutController(
            allCollectionImpl = get(),
            db = get(named(DataBases.MAIN))
        )
    }
    single {
        ContentRoutController(
            content = get(named<ContentImpl>()),
            main = get(named<AllCollectionImpl>())
        )
    }
}