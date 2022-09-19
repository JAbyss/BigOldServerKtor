package com.foggyskies

//import com.foggyskies.chat.extendfun.generateUUID
//import com.foggyskies.plugin.configureRouting
//import com.foggyskies.plugin.configureSecurity
//import com.foggyskies.plugin.configureSockets
//import com.foggyskies.plugin.mainModule
//import io.ktor.http.*
//import io.ktor.serialization.kotlinx.json.*
//import io.ktor.server.application.*
////import io.ktor.server.application.*
//import io.ktor.server.plugins.contentnegotiation.*
//import io.ktor.server.plugins.forwardedheaders.*
//import kotlinx.serialization.json.Json
//import org.koin.ktor.plugin.Koin
//import java.text.SimpleDateFormat
//import java.util.*
//import javax.mail.Message
//import javax.mail.MessagingException
//import javax.mail.PasswordAuthentication
//import javax.mail.Session
//import javax.mail.internet.InternetAddress
//import javax.mail.internet.MimeMessage

import com.foggyskies.server.databases.mongo.codes.testpacage.Logger.logs
import com.foggyskies.server.databases.mongo.main.models.UserMainEntity
import com.foggyskies.server.plugin.configureRouting
import com.foggyskies.server.plugin.configureSecurity
import com.foggyskies.server.plugin.configureSockets
import com.foggyskies.server.plugin.mainModule
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.ktor.plugin.Koin
import org.litote.kmongo.json
import java.text.SimpleDateFormat
import java.util.*


enum class DataBases {
    MAIN, MESSAGES, SUBSCRIBERS, CONTENT, NEW_MESSAGE, CODES
}

object ServerDate {
    private val formatFull = SimpleDateFormat("d MMM yyyy г. HH:mm:ss")
    private val formatMute = SimpleDateFormat("ddhhmm")

    val fullDate: String
        get() =
            formatFull.format(Date())

    val muteDate: String
        get() = formatMute.format(Date())
}

val client = HttpClient(CIO) {
    install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
        json(Json {
            prettyPrint = false
            allowStructuredMapKeys = true
            isLenient = true
            ignoreUnknownKeys = true
        }, contentType = ContentType.Application.Json)
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 20000
    }
}

object Testss {
    val user: UserMainEntity
        get() = UserMainEntity(
            username = UUID.randomUUID().toString(),
            e_mail = UUID.randomUUID().toString(),
            status = "",
            password = UUID.randomUUID().toString()
        )
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {

    install(Koin) {
        modules(mainModule)
    }
//    install(Forward)
//    install(ForwardedHeaderSupport)
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader("Auth")
        //        header(HttpHeaders.ContentType)
        //        header("Auth")
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = false
            allowStructuredMapKeys = true
            isLenient = true
            ignoreUnknownKeys = true
        }, contentType = ContentType.Application.Json)
    }
    //    install(ForwardedHeaders)
    //    install(XForwardedHeaders)
    configureSockets()
    configureRouting()
    configureSecurity()
//
//    CoroutineScope(IO).launch {
//        while (true) {
//            println(logs.json)
//            delay(1000)
//        }
//    }


//    val map = HashMap<String,suspend () -> Unit>()
//
//    val a: Long = 10L
//
//    map["wfkwa"] = suspend {
//        delay(9223372036854775807L)
//        map.remove("wfkwa")
//        println("Сделал дело")
//    }

//    suspend fun startTask(action: () -> Unit, duration: Long): Deferred<Unit> {
//
//        return async {
//            delay(duration)
//            action()
//        }
//    }
//
//    val map = ConcurrentHashMap<String, Deferred<Unit>>()
//
////    val a = ConcurrentHashMap
//
//    suspend fun startTaksMap(code: String, duration: Long) {
//        map[code] = startTask(action = {
////            println("$code task")
//            map.remove(code)?.cancel()
//        }, duration)
//    }
//
//    val a = 100_000
//
//    println(a)
//
//    launch(newSingleThreadContext("task-thread")) {
//
//        println(Thread.currentThread())
//        while (map.size < 200) {
//            val uuid = generateUUID(10)
//            val duration = kotlin.random.Random.nextLong(from = 1000, until = 8000)
//            startTaksMap(uuid, duration)
//            delay(10)
////                println(map.size)
//        }
//    }

}