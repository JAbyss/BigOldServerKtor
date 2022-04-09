package com.foggyskies.chat.routes

import com.foggyskies.ChatSession
import com.foggyskies.chat.newroom.NotifyRoutController
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.collections.*
import io.ktor.websocket.*
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.consumeEach
import org.koin.ktor.ext.inject

private val listNotify = ConcurrentList<String>()

fun Route.notificationRoutes() {
    val routeController by inject<NotifyRoutController>()
    fun createNotifySession(token: String) {
        if (!listNotify.contains(token)) {
            listNotify.add(token)
            webSocket("/notify/$token") {
//            val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")

                val session = call.sessions.get<ChatSession>()
                if (session == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
                    listNotify.remove(token)
                    return@webSocket
                }

                val user = routeController.getUserByToken(token)
//        val notification = routeController.getNotification("63cf0a3e88c4a08c2842c08")

                async {
                    routeController.watchForNotification(user.idUser, this@webSocket)
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
    }
    get("/createNotificationSession{token}") {
        val token =
            call.parameters["token"] ?: call.respond(HttpStatusCode.BadRequest, "Параметр token не получен.")
        if (token.toString().isNotEmpty()) {
            createNotifySession(token.toString())
//                val messages = roomController.getFiftyMessage(idChat.toString())
            call.respond(HttpStatusCode.Created)
        }
    }
}