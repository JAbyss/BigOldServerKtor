package com.foggyskies.chat.routes

import com.foggyskies.chat.newroom.NotifyRoutController
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

fun Route.notificationRoutes() {
    val routeController by inject<NotifyRoutController>()
    webSocket("/notify") {

//        val notification = routeController.getNotification("63cf0a3e88c4a08c2842c08")

        async {
            routeController.watchForNotification("", this@webSocket)
        }

//        val json = Json.encodeToString(notification)
//
//        send(json)

        incoming.consumeEach { frame ->
            if (frame is Frame.Text) {
                val incomingData = frame.readText()
            }
        }
    }
}