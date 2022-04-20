package com.foggyskies.chat.newroom

import com.foggyskies.chat.data.bettamodels.Notification
import com.foggyskies.chat.data.model.ChatMainEntity
import com.foggyskies.chat.data.model.ChatUserEntity
import com.foggyskies.chat.databases.main.AllCollectionImpl
import com.foggyskies.chat.databases.message.MessagesDBImpl
import com.foggyskies.chat.extendfun.forEachSuspend
import com.jetbrains.handson.chat.server.chat.data.model.ChatMessage
import com.jetbrains.handson.chat.server.chat.data.model.Member
import io.ktor.http.cio.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class MessagesRoutController(
    private val allCollectionImpl: AllCollectionImpl,
    private val messagesDBImpl: MessagesDBImpl
//    private val db: CoroutineDatabase
) {

    lateinit var chatEntity: ChatMainEntity

    suspend fun initChat(idChat: String) {
        chatEntity = allCollectionImpl.getChatById(idChat)
    }

    private suspend fun insertOne(idChat: String, message: ChatMessage) {
        messagesDBImpl.insertOne(idChat, message)
    }

    suspend fun getAllMessages(idChat: String): List<ChatMessage> {
        return messagesDBImpl.getAllMessages(idChat)
    }

    private suspend fun getFiftyMessage(idChat: String): List<ChatMessage> {
        return messagesDBImpl.getFiftyMessage(idChat)
    }

    suspend fun sendMessage(
        senderUsername: String,
        message: String,
        members: ConcurrentHashMap<String, Member>,
        idChat: String
    ) {

        members.values.forEach { member ->
            val sdf = SimpleDateFormat("hh:mm")
            val currentDate = sdf.format(Date())
            val messageEntity = ChatMessage(
                message = message,
                author = senderUsername,
                date = currentDate
            )

            insertOne(idChat, messageEntity)
            val idReceiver =
                if (chatEntity.firstCompanion?.nameUser != senderUsername) chatEntity.firstCompanion!! else chatEntity.secondCompanion!!

            if (members.keys.size == 1 && idReceiver.nameUser != senderUsername) {
                if (allCollectionImpl.getStatusByIdUser(idReceiver.idUser) == "Не в сети")
                    createNotification(senderUsername, idReceiver, message)
                else
                    createInternalNotification(senderUsername, idReceiver, message)
            }

            val parsedMessage = Json.encodeToString(messageEntity)
            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun onJoin(
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
            val messages = getFiftyMessage(chatEntity.idChat)
            messages.forEachSuspend { _message ->
                val json = Json.encodeToString(_message)
                socket.send(json)
            }

        }
    }

    private suspend fun createNotification(senderUsername: String, receiver: ChatUserEntity, message: String) {
        val isExistDocument = allCollectionImpl.checkOnExistNotificationDocument(receiver.idUser)

        val senderUser =
            if (chatEntity.firstCompanion?.nameUser == senderUsername) chatEntity.firstCompanion!! else chatEntity.secondCompanion!!

        val notification = Notification(
            id = chatEntity.idChat,
            idUser = senderUser.idUser,
            title = senderUsername,
            description = message,
            image = "",
            status = "Отправлено"
        )

        if (!isExistDocument)
            allCollectionImpl.createNotificationDocument(receiver.idUser, notification)
        else
            allCollectionImpl.addNotification(receiver.idUser, notification)
    }

    private suspend fun createInternalNotification(senderUsername: String, receiver: ChatUserEntity, message: String) {
        val isExistDocument = allCollectionImpl.checkOnExistInternalNotificationDocument(receiver.idUser)

        val senderUser =
            if (chatEntity.firstCompanion?.nameUser == senderUsername) chatEntity.firstCompanion!! else chatEntity.secondCompanion!!

        val notification = Notification(
            id = chatEntity.idChat,
            idUser = senderUser.idUser,
            title = senderUsername,
            description = message,
            image = "",
            status = "Отправлено"
        )

        if (!isExistDocument)
            allCollectionImpl.createInternalNotificationDocument(receiver.idUser, notification)
        else
            allCollectionImpl.addInternalNotification(receiver.idUser, notification)
    }

}