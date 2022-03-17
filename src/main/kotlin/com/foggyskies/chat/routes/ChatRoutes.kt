package com.foggyskies.chat.routes

import com.jetbrains.handson.chat.server.chat.data.model.Member
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.concurrent.ConcurrentHashMap

@Serializable
data class UserMainEntity(
    @BsonId
    var idUser: String = ObjectId().toString(),
    var username: String,
    var e_mail: String,
    var image: String = "",
    var status: String = "",
    var chats: List<String> = emptyList(),
    val password: String
)


@Serializable
data class ChatUserEntity(
    var idUser: String,
    var nameUser: String
)

@Serializable
data class ChatMainEntity(
    @BsonId
    var idChat: String = ObjectId().toString(),
    var users: List<ChatUserEntity>
)

fun Route.chatSocket() {
//
//        webSocket("/chat{username}") {
//            val members = ConcurrentHashMap<String, Member>()
//
//            val session = call.sessions.get<ChatSession>()
//
//            if (session == null) {
//                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
//                return@webSocket
//            }
//            try {
//                onJoin(
//                    username = session.username,
//                    sessionId = session.sessionID,
//                    socket = this,
//                    members = members
//                )
//                incoming.consumeEach { frame ->
//                    if (frame is Frame.Text) {
//                        sendMessage(
//                            senderUsername = session.username,
//                            message = frame.readText(),
//                            members = members
//                        )
//                    }
//                }
//            } catch (e: Exception) {
//                println(e.localizedMessage)
//            } finally {
//                tryDisconnect(members = members, username = session.username)
//            }
//    }
//    webSocket("/chat_2") {
//        send("You are connected!")
//        for (frame in incoming) {
//            frame as? Frame.Text ?: continue
//            val receivedText = frame.readText()
//            println("You said: $receivedText")
//            send("You said: $receivedText")
//        }
//    }
}

//fun Route.createSocket() {
//
//    webSocket("/createroom{username}{idChat}") {
////        val membersSocket = ConcurrentHashMap<String, Member>()
////        val session = call.sessions.get<ChatSession>()
//        val idChat = call.parameters["idChat"]
//        webSocket("/$idChat") secSock@{
//            val members = ConcurrentHashMap<String, Member>()
//            val session = call.sessions.get<ChatSession>()
//            if (session == null) {
//                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
//                return@secSock
//            }
//            onJoin(
//                username = session.username,
//                sessionId = session.sessionID,
//                socket = this,
//                members = members
//            )
//            incoming.consumeEach { frame ->
//                if (frame is Frame.Text) {
//                    sendMessage(
//                        senderUsername = session.username,
//                        message = frame.readText(),
//                        members = members
//                    )
//                }
//            }
//        }
////        if (session == null) {
////            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
////            return@webSocket
////        }
////        onJoinTest(
////            username = session.username,
////            sessionId = session.sessionID,
////            socket = this,
////            membersSocket = membersSocket
////        )
////        var listSessions = listOf("123", "321")
//    }
//}

suspend fun tryDisconnect(username: String, members: ConcurrentHashMap<String, Member>) {
    members[username]?.socket?.close()
    if (members.containsKey(username)) {
        members.remove(username)
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