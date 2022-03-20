package com.foggyskies.chat.routes

import com.foggyskies.ChatSession
import com.foggyskies.chat.room.MessageRoomController
import com.jetbrains.handson.chat.server.chat.data.model.Member
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.collections.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import org.koin.ktor.ext.inject
import java.util.concurrent.ConcurrentHashMap

var sessionsChat = ConcurrentList<String>()

fun Route.chatSessionRoutes() {
    route("/subscribes") {
        val roomController by inject<MessageRoomController>()
        fun createSocket(idChat: String, sessionsChat: ConcurrentList<String>) {
            if (!sessionsChat.contains(idChat)) {
                sessionsChat.add(idChat)
                val members = ConcurrentHashMap<String, Member>()
                webSocket("/$idChat{username}") {
                    roomController.chatId = idChat
                    val session = call.sessions.get<ChatSession>()
                    if (session == null) {
                        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
                        sessionsChat.remove(idChat)
                        return@webSocket
                    }
                    try {
                        roomController.onJoin(
                            username = session.username,
                            sessionId = session.sessionID,
                            socket = this,
                            members = members
                        )
                        incoming.consumeEach { frame ->
                            if (frame is Frame.Text) {
                                roomController.sendMessage(
                                    senderUsername = session.username,
                                    message = frame.readText(),
                                    members = members,
                                    idChat = idChat
                                )
                            }
                        }
                    } catch (e: Exception) {
                        println(e)
                    }
                    tryDisconnect(members = members, username = session.username)
//                    if (members.size == 0) {
//                        sessionsChat.remove(chatId)
//                        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
//                        flush()
//                        cancel()
//                        return@webSocket
//                    }
                }
            }
        }
        get("/createChatSession{idChat}") {
            val idChat =
                call.parameters["idChat"] ?: call.respond(HttpStatusCode.BadRequest, "Параметр idChat не получен.")
            if (idChat.toString().isNotEmpty()) {
                createSocket(idChat.toString(), sessionsChat)
//                val messages = roomController.getFiftyMessage(idChat.toString())
                call.respond(HttpStatusCode.Created)
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