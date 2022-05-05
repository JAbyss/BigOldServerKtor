package com.foggyskies.chat.routes

import com.foggyskies.chat.data.model.ChatSession
import com.foggyskies.chat.newroom.MessagesRoutController
import com.jetbrains.handson.chat.server.chat.data.model.Member
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.collections.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import java.util.*
import java.util.concurrent.ConcurrentHashMap

private var sessionsChat = ConcurrentList<String>()

fun Route.chatSessionRoutes() {
    route("/subscribes") {
        val routController by inject<MessagesRoutController>()
        fun createSocket(idChat: String, sessionsChat: ConcurrentList<String>) {
            if (!sessionsChat.contains(idChat)) {
                sessionsChat.add(idChat)
                val members = ConcurrentHashMap<String, Member>()
                webSocket("/$idChat{username}") {

                    routController.initChat(idChat)
                    val session = call.sessions.get<ChatSession>()
                    if (session == null) {
                        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
                        sessionsChat.remove(idChat)
                        return@webSocket
                    }
                    try {
                        routController.onJoin(
                            username = session.username,
                            sessionId = session.sessionID,
                            socket = this,
                            members = members
                        )
                        incoming.consumeEach { frame ->
                            if (frame is Frame.Text) {
                                val message = Json.decodeFromString<MessageDC>(frame.readText())
                                routController.sendMessage(
                                    senderUsername = session.username,
                                    message = message,
                                    members = members,
                                    idChat = idChat
                                )
                            }
                        }
                    } catch (e: Exception) {
                        println(e)
                    }
                    tryDisconnect(members = members, username = session.username)
                }
            }
        }
        get("/createChatSession{idChat}") {
            val idChat =
                call.parameters["idChat"] ?: call.respond(HttpStatusCode.BadRequest, "Параметр idChat не получен.")
            if (idChat.toString().isNotEmpty()) {
                createSocket(idChat.toString(), sessionsChat)
                call.respond(HttpStatusCode.Created)
            }
        }
        post("/addImageToMessage{idChat}") {
            val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")

            val image = call.receiveText() ?: call.respond(HttpStatusCode.BadRequest, "Image не получен.")

            val idChat =
                call.parameters["idChat"] ?: call.respond(HttpStatusCode.BadRequest, "IdChat не получен.")

            val isTokenExist = routController.checkOnExistToken(token.toString())
            if (isTokenExist) {
                val decodedString = Base64.getDecoder().decode(image.toString())
                val addressImage = routController.addImageToChat(idChat.toString(), decodedString)
                call.respond(HttpStatusCode.OK, addressImage)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Token не существует.")
            }
        }
    }
}

fun onJoin(
    username: String,
    sessionId: String,
    socket: WebSocketSession,
    members: ConcurrentHashMap<String, Member>
) {
    if (!members.containsKey(username)) {
        members[username] = Member(
            username = username,
            sessionId = sessionId,
            socket = socket
        )
    }
}

suspend fun tryDisconnect(username: String, members: ConcurrentHashMap<String, Member>) {
    members[username]?.socket?.close()
    if (members.containsKey(username)) {
        members.remove(username)
    }
}
//fixme
@Serializable
data class MessageDC(
    var listImages: List<String> = emptyList(),
    var message: String
)