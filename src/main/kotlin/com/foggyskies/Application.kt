package com.foggyskies

import com.foggyskies.chat.data.*
import com.foggyskies.chat.datanew.AllCollectionImpl
import com.foggyskies.chat.newroom.UserRoutController
import com.foggyskies.chat.room.AuthRoomController
import com.foggyskies.chat.room.CreateChatRoomController
import com.foggyskies.chat.room.MessageRoomController
import com.foggyskies.chat.room.UserRoomController
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
    single<AuthDataSource> {
        AuthDataSourceImpl(get())
    }
    single<UsersDataSource> {
        UsersDataSourceImpl(get())
    }
    single<CreateChatDataSource> {
        CreateChatDataSourceImpl(get())
    }
    single<TokenDataSource> {
        TokenDataSourceImpl(get())
    }
    single<MessageDataSource> {
        MessageDataSourceImpl(get())
    }
    single {
        AuthRoomController(get())
    }
    single {
        UserRoomController(get())
    }
    single {
        CreateChatRoomController(get(), get())
    }
    single {
        MessageRoomController(get())
    }
}