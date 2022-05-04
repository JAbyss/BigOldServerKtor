package com.foggyskies.chat.newroom

import com.foggyskies.ImpAndDB
import com.foggyskies.chat.data.bettamodels.Notification
import com.foggyskies.chat.data.model.ChatMainEntity
import com.foggyskies.chat.data.model.ChatUserEntity
import com.foggyskies.chat.databases.main.AllCollectionImpl
import com.foggyskies.chat.databases.message.MessagesDBImpl
import com.foggyskies.chat.databases.newmessage.NewMessagesDBImpl
import com.foggyskies.chat.extendfun.forEachSuspend
import com.foggyskies.chat.routes.MessageDC
import com.jetbrains.handson.chat.server.chat.data.model.ChatMessage
import com.jetbrains.handson.chat.server.chat.data.model.Member
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.suspendCoroutine

class MessagesRoutController(
    private val main: ImpAndDB<AllCollectionImpl>,
    private val message: ImpAndDB<MessagesDBImpl>,
    private val new_message: ImpAndDB<NewMessagesDBImpl>,
//    private val allCollectionImpl: AllCollectionImpl,
//    private val messagesDBImpl: MessagesDBImpl
//    private val db: CoroutineDatabase
) : CheckTokenExist(main.db) {

    lateinit var chatEntity: ChatMainEntity

    suspend fun initChat(idChat: String) {
        chatEntity = main.impl.getChatById(idChat)
    }

    private suspend fun insertOne(idChat: String, message: ChatMessage) {
        this.message.impl.insertOne(idChat, message)
    }

    suspend fun getAllMessages(idChat: String): List<ChatMessage> {
        return message.impl.getAllMessages(idChat)
    }

    private suspend fun getFiftyMessage(idChat: String): List<ChatMessage> {
        return message.impl.getFiftyMessage(idChat)
    }

    suspend fun sendMessage(
        senderUsername: String,
        message: MessageDC,
        members: ConcurrentHashMap<String, Member>,
        idChat: String
    ) {
        members.values.forEach { member ->
            val sdf = SimpleDateFormat("d MMM yyyy г. hh:mm:ss")
            val currentDate = sdf.format(Date())
            val messageEntity = ChatMessage(
                listImages = message.listImages,
                message = message.message,
                author = senderUsername,
                date = currentDate
            )

            val idReceiver =
                if (chatEntity.firstCompanion?.nameUser != senderUsername) chatEntity.firstCompanion!! else chatEntity.secondCompanion!!

            if (members.keys.size == 1 && idReceiver.nameUser != senderUsername) {
                if (main.impl.getStatusByIdUser(idReceiver.idUser) == "Не в сети")
                    createNotification(senderUsername, idReceiver, message.message)
                else {
                    createInternalNotification(senderUsername, idReceiver, message.message)
                    insertNewMessage(chatEntity.idChat, idReceiver.idUser, messageEntity)
                }
            } else {
                insertOne(idChat, messageEntity)
            }

            val parsedMessage = Json.encodeToString(messageEntity)
            member.socket.send(parsedMessage)
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
            val user = main.impl.getUserByUsername(username)
            getNewMessages(chatEntity.idChat, user.idUser, callBack = { listNewMessages ->
                listNewMessages.forEachSuspend { _message ->
                    val json = Json.encodeToString(_message)
                    socket.send(json)
                }
            })
        }
    }

    private suspend fun insertNewMessage(idChat: String, idUser: String, message: ChatMessage) {
        new_message.impl.createCollection(idUser)
        if (new_message.impl.checkOnExistDocument(idChat, idUser))
            new_message.impl.insertOneMessage(idChat, idUser, message)
        else {
            new_message.impl.createDocument(idChat, idUser)
            new_message.impl.insertOneMessage(idChat, idUser, message)
        }
    }

    private suspend fun getNewMessages(idChat: String, idUser: String, callBack: suspend (List<ChatMessage>) -> Unit) {
        val new_messages = new_message.impl.getAllNewMessages(idChat, idUser)
        callBack(new_messages)
        coroutineScope {
            new_messages.forEach { newMess ->
                message.impl.insertOne(idChat, newMess)
            }
            new_message.impl.clearOneChat(idChat, idUser)
        }
//        new_message.impl.clearOneChat(idChat, idUser)
    }

    private suspend fun createNotification(senderUsername: String, receiver: ChatUserEntity, message: String) {
        val isExistDocument = main.impl.checkOnExistNotificationDocument(receiver.idUser)

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
            main.impl.createNotificationDocument(receiver.idUser, notification)
        else
            main.impl.addNotification(receiver.idUser, notification)
    }

    private suspend fun createInternalNotification(senderUsername: String, receiver: ChatUserEntity, message: String) {
        val isExistDocument = main.impl.checkOnExistInternalNotificationDocument(receiver.idUser)

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
            main.impl.createInternalNotificationDocument(receiver.idUser, notification)
        else
            main.impl.addInternalNotification(receiver.idUser, notification)
    }

    fun addImageToChat(idChat: String, image: ByteArray): String {
        val originString = "images/chats/$idChat"
        val path = Paths.get(originString)
        val file = File(originString)
        if (!Files.exists(path)) {
            file.mkdirs()
        }
        val idImage = ObjectId().toString()
        val readyPath = "$originString/image_${idImage}.png"
        File(readyPath).writeBytes(image)
        return readyPath
    }
}

suspend fun main() {
//    coroutineScope {
//        async {
//
//
//        }
//    }
    testFun(callBack = {
        println(it)
    })
    println("All Ended")
}

suspend fun testFun(callBack: (String) -> Unit) {
    coroutineScope {
        val b = "Eee"
        callBack(b)
        async {

            var a = 0
            while (a < 50) {
                a++
                println(a)
                delay(100)
            }
        }
    }

}