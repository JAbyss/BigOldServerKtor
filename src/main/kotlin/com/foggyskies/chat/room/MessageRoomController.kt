//package com.foggyskies.chat.room
//
//import com.foggyskies.chat.data.MessageDataSource
//import com.foggyskies.chat.extendfun.forEachSuspend
//import com.jetbrains.handson.chat.server.chat.data.model.ChatMessage
//import com.jetbrains.handson.chat.server.chat.data.model.Member
//import io.ktor.http.cio.websocket.*
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.onEach
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//import java.text.SimpleDateFormat
//import java.util.*
//import java.util.concurrent.ConcurrentHashMap
//
//class MessageRoomController(
//    private val messageDataSource: MessageDataSource
//) {
//
//    var chatId = ""
//
//    private suspend fun insertOne(idChat: String, message: ChatMessage) {
//        messageDataSource.insertOne(idChat, message)
//    }
//
//    suspend fun getAllMessage(idChat: String): List<ChatMessage> {
//        return messageDataSource.getAllMessage(idChat)
//    }
//
//    private suspend fun getFiftyMessage(idChat: String): List<ChatMessage> {
//        return messageDataSource.getFiftyMessage(idChat)
//    }
//
//    suspend fun sendMessage(senderUsername: String, message: String, members: ConcurrentHashMap<String, Member>, idChat: String) {
//
//        members.values.forEach { member ->
//            val sdf = SimpleDateFormat("hh:mm")
//            val currentDate = sdf.format(Date())
//            val messageEntity = ChatMessage(
//                message = message,
//                author = senderUsername,
//                date = currentDate
//            )
//
//            insertOne(idChat, messageEntity)
//
//            val parsedMessage = Json.encodeToString(messageEntity)
//            member.socket.send(Frame.Text(parsedMessage))
//        }
//    }
//
//    suspend fun onJoin(
//        username: String,
//        sessionId: String,
//        socket: WebSocketSession,
//        members: ConcurrentHashMap<String, Member>
//    ) {
//        if (!members.containsKey(username)) {
//            members[username] = Member(
//                username = username,
//                sessionId = sessionId,
//                socket = socket
//            )
//            val messages = getFiftyMessage(chatId)
//            messages.forEachSuspend { _message ->
//                val json = Json.encodeToString(_message)
//                socket.send(json)
//            }
//
//        }
//    }
//}