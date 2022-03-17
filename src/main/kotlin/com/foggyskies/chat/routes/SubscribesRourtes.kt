package com.foggyskies.chat.routes

import com.foggyskies.ChatSession
import com.foggyskies.chat.room.MessageRoomController
import com.jetbrains.handson.chat.server.chat.data.model.Member
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.sessions.*
import io.ktor.util.collections.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import org.koin.ktor.ext.inject
import java.util.concurrent.ConcurrentHashMap

//@Serializable
//data class ChatMainEntity(
//    var idChat: Int,
//    var messages: List<ChatMessage>,
//    var
//)

//var chats = listOf(
//    ChatMainEntity(
//        idChat = "123",
//        messages = listOf(
//            ChatMessage(
//                author = "JAbyss",
//                date = "13:30",
//                message = "Hello"
//            ),
//            ChatMessage(
//                author = "JAbyss",
//                date = "1647268076148",
//                message = "Привет"
//            ),
//            ChatMessage(
//                author = "JAbyss",
//                date = "1647268076148",
//                message = "Как дела?"
//            ),
//            ChatMessage(
//                author = "Kalterfad",
//                date = "1647268076148",
//                message = "Привет"
//            ),
//            ChatMessage(
//                author = "Kalterfad",
//                date = "1647268076148",
//                message = "Нормально, как сам?"
//            )
//        ),
//    ),
//    ChatMainEntity(
//        idChat = "321",
//        messages = listOf(
//            ChatMessage(
//                author = "JAbyss",
//                date = "13:30",
//                message = "Hello12312323"
//            ),
//            ChatMessage(
//                author = "JAbyss",
//                date = "1647268076148",
//                message = "Привет21321312"
//            ),
//            ChatMessage(
//                author = "JAbyss",
//                date = "1647268076148",
//                message = "Как дела?23123231"
//            ),
//            ChatMessage(
//                author = "Aleha",
//                date = "1647268076148",
//                message = "Привет213213123"
//            ),
//            ChatMessage(
//                author = "Aleha",
//                date = "1647268076148",
//                message = "Нормально, как сам?1323213213"
//            )
//        ),
//    )
//)
//
//var users = listOf(
//    UserMainEntity(
//        idUser = "123456",
//        nameUser = "JAbyss",
//        chats = listOf(
//            "123", "321"
//        )
//    ),
//    UserMainEntity(
//        idUser = "654321",
//        nameUser = "Kalterfad",
//        chats = listOf(
//            "123", "321"
//        )
//    ),
//    UserMainEntity(
//        idUser = "2wd",
//        nameUser = "Aleha",
//        chats = listOf(
//            "321"
//        )
//    )
//)

var sessionsChat = ConcurrentList<String>()

fun Route.subscribeRoutes() {
    route("/subscribes") {
        val roomController by inject<MessageRoomController>()
        fun createSocket(idChat: String, sessionsChat: ConcurrentList<String>) {
            if (!sessionsChat.contains(idChat)) {
                sessionsChat.add(idChat)
                val members = ConcurrentHashMap<String, Member>()
                webSocket("/$idChat{username}") {
                    val chatId = idChat
                    val session = call.sessions.get<ChatSession>()
                    if (session == null) {
                        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
                        sessionsChat.remove(chatId)
                        return@webSocket
                    }
                    try {
                        onJoin(
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
                                    idChat = chatId
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
                val messages = roomController.getFiftyMessage(idChat.toString())
                call.respond(HttpStatusCode.Created, messages)
            }
        }
    }
}