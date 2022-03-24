package com.foggyskies

import com.foggyskies.chat.data.*
import com.foggyskies.chat.datanew.AllCollectionImpl
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
    single {
        AllCollectionImpl(get())
    }
    single {
        UserRoutController(get(),get())
    }
    single {
        CreateChatRoutController(get(), get())
    }
    single {
        NotifyRoutController(get(),get())
    }
    single {
        MessagesRoutController(get(), get())
    }
    single {
        AuthRoutController(get(),get())
    }
//    single<AuthDataSource> {
//        AuthDataSourceImpl(get())
//    }
//    single {
//        AuthRoomController(get())
//    }
}