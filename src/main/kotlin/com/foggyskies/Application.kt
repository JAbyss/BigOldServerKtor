package com.foggyskies

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
import kotlinx.serialization.json.Json
import org.koin.ktor.plugin.Koin
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
    }
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = false
            allowStructuredMapKeys = true
            isLenient = true
            ignoreUnknownKeys = true
        }, contentType = ContentType.Application.Json)
    }
    configureSockets()
    configureRouting()
    configureSecurity()
}