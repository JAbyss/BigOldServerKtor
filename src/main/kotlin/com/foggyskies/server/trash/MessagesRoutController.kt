//package com.foggyskies.server.routes.chat
//
//
//import com.foggyskies.PasswordCoder
//import com.foggyskies.ServerDate
//import com.foggyskies.server.data.bettamodels.Notification
//import com.foggyskies.server.data.model.ChatSession
//import com.foggyskies.server.data.model.ChatUserEntity
//import com.foggyskies.server.data.model.ImpAndDB
//import com.foggyskies.server.data.model.Member
//import com.foggyskies.server.databases.mongo.main.MainDBImpl
//import com.foggyskies.server.databases.mongo.main.models.ChatMainEntity
////import com.foggyskies.server.databases.main.models.ChatMainEntity_
//import com.foggyskies.server.databases.message.MessagesDBImpl
//import com.foggyskies.server.databases.message.models.ChatMessageCollection
//import com.foggyskies.server.databases.message.models.ChatMessageDC
//import com.foggyskies.server.databases.message.models.FileDC
//import com.foggyskies.server.databases.message.models.MessageDC
//import com.foggyskies.server.databases.mongo.newmessage.NewMessagesDBImpl
//import com.foggyskies.server.extendfun.forEachSuspend
//import com.foggyskies.server.extendfun.getSizeFile
//import com.foggyskies.server.newroom.CheckTokenExist
//import io.ktor.websocket.*
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.coroutineScope
//import kotlinx.coroutines.withContext
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//import org.bson.types.ObjectId
//import java.io.File
//import java.nio.file.Files
//import java.nio.file.Paths
//import java.util.*
//import java.util.concurrent.ConcurrentHashMap
//
//class MessagesRoutController(
//    private val main: ImpAndDB<MainDBImpl>,
//    private val message: ImpAndDB<MessagesDBImpl>,
//    private val new_message: ImpAndDB<NewMessagesDBImpl>
//) : CheckTokenExist(main.db) {
//
//    suspend fun getChat(idChat: String): ChatMainEntity = main.impl.getChatById(idChat)
//
//    private suspend fun insertOne(idChat: String, message: ChatMessageCollection) {
//        this.message.impl.insertOne(idChat, message)
//    }
//
//    suspend fun getAllMessages(idChat: String): List<ChatMessageCollection> {
//        return message.impl.getAllMessages(idChat)
//    }
//
//    private suspend fun getFiftyMessage(chatEntity: ChatMainEntity): List<ChatMessageDC> {
//        val listMessages = message.impl.getFiftyMessage(chatEntity.idChat)
//        return listMessages.map {
//            ChatMessageDC(
//                id = it.id,
//                idUser = it.idUser,
//                author = "" ,
////                if (it.idUser == chatEntity.firstCompanion?.idUser!!)
////                    chatEntity.firstCompanion?.nameUser!!
////                else
////                    chatEntity.secondCompanion?.nameUser!!,
//                date = it.date,
//                message = it.message,
//                listImages = it.listImages,
//                listFiles = it.listFiles
//            )
//        }
//    }
//
//    private suspend fun getNextMessage(chatEntity: ChatMainEntity, lastMessageId: String): List<ChatMessageDC> {
//        val listMessages = message.impl.getNextMessages(chatEntity.idChat, lastMessageId)
//        return listMessages.map {
//            ChatMessageDC(
//                id = it.id,
//                idUser = it.idUser,
//                author = "",
////                if (it.idUser == chatEntity.firstCompanion?.idUser!!)
////                    chatEntity.firstCompanion?.nameUser!!
////                else
////                    chatEntity.secondCompanion?.nameUser!!,
//                date = it.date,
//                message = it.message,
//                listImages = it.listImages,
//                listFiles = it.listFiles
//            )
//        }
//    }
//
//    suspend fun sendNextMessages(socket: WebSocketSession, chatEntity: ChatMainEntity, lastMessageId: String) {
//        val messages = getNextMessage(chatEntity, lastMessageId)
////        messages.forEach { _message ->
////            val json = Json.encodeToString(_message)
////            socket.send("nextMessages|$json")
////        }
//        val json = Json.encodeToString(messages)
//        socket.send("nextMessages|$json")
//    }
//
//    suspend fun sendMessage(
//        idUser: String,
//        senderUsername: String,
//        message: MessageDC,
//        members: ConcurrentHashMap<String, Member>,
//        chatEntity: ChatMainEntity
//    ) {
//
//        val messageEntity = ChatMessageCollection(
//            listFiles = message.listFiles,
//            listImages = message.listImages,
//            message = message.message,
//            idUser = idUser,
//            date = ServerDate.fullDate
//        )
//
//        val idReceiver = ""
////            if (chatEntity.firstCompanion?.nameUser != senderUsername) chatEntity.firstCompanion!! else chatEntity.secondCompanion!!
//
////        if (members.keys.size == 1 && idReceiver.nameUser != senderUsername) {
////            if (main.impl.getStatusByIdUser(idReceiver.idUser) == "Не в сети") {
////                createNotification(senderUsername, idReceiver, message.message, chatEntity)
////                insertNewMessage(chatEntity.idChat, idReceiver.idUser, messageEntity)
////            } else {
//////                val chat = main.impl.getChatById(idChat)
////                val (receiverCompanion, nameField) =
////                    if (chatEntity.firstCompanion?.idUser != idUser)
////                        Pair(chatEntity.firstCompanion!!, ChatMainEntity_.FirstCompanion)
////                    else
////                        Pair(chatEntity.secondCompanion!!, ChatMainEntity_.SecondCompanion)
////
////                if (receiverCompanion.notifiable.isEmpty())
////                    insertNewMessage(chatEntity.idChat, idReceiver.idUser, messageEntity)
//////                createInternalNotification(senderUsername, idReceiver, message.message)
////                else {
////                    val timeMute = idReceiver.notifiable.toInt()
////                    if (ServerDate.muteDate.toInt() > timeMute) {
////                        // Размут
////                        main.impl.muteChat(chatEntity.idChat, idReceiver.idUser, nameField = nameField)
////                        insertNewMessage(chatEntity.idChat, idReceiver.idUser, messageEntity)
////                    }
////                }
////            }
////        } else {
////            insertOne(chatEntity.idChat, messageEntity)
////        }
//        println("Просто Пошло")
////        members.values.forEach { member -> member.socket.send("fwafwaf") }
////        members[senderUsername]?.socket?.send(Frame.Text("Hello"))
//        members.values.forEach { member ->
//            val parsedMessage = Json.encodeToString(
//                ChatMessageDC(
//                    id = messageEntity.id,
//                    idUser = messageEntity.idUser,
//                    message = messageEntity.message,
//                    listImages = messageEntity.listImages,
//                    listFiles = messageEntity.listFiles,
//                    date = messageEntity.date,
//                    author = senderUsername
//                )
//            )
//            println("NewMessage Пошло")
//            member.socket.send(parsedMessage)
//        }
//    }
//
//    suspend fun getIdUserByToken(token: String): String {
//        return main.impl.getTokenByToken(token).idUser
//    }
//
//    suspend fun onJoin(
//        idUser: String,
//        username: String,
//        sessionId: String,
//        socket: WebSocketSession,
//        members: ConcurrentHashMap<String, Member>,
//        chatEntity: ChatMainEntity
//    ) {
//        if (!members.containsKey(username)) {
//            members[username] = Member(
//                idUser = idUser,
//                username = username,
//                sessionId = sessionId,
//                socket = socket
//            )
//            val messages = getFiftyMessage(chatEntity)
//            messages.forEachSuspend { _message ->
//                val json = Json.encodeToString(_message)
//                socket.send(json)
//            }
//            val myNewMessage =
//                if (chatEntity.firstCompanion?.idUser == idUser) chatEntity.secondCompanion else chatEntity.firstCompanion
//            val user = main.impl.getUserByUsername(username)
//            getNewMessagesCompanion(
//                chatEntity.idChat,
//                myNewMessage?.idUser!!,
//                chatEntity,
//                callBack = { listNewMessagesCompanion ->
//                    listNewMessagesCompanion.forEachSuspend { _message ->
//                        val json = Json.encodeToString(_message)
//                        socket.send(json)
//                    }
//                })
//            getNewMessages(chatEntity.idChat, idUser, chatEntity, callBack = { listNewMessages ->
//                listNewMessages.forEachSuspend { _message ->
//                    val json = Json.encodeToString(_message)
////                    socket.send("newMessage|$json")
//                    socket.send(json)
//                }
//            })
//        }
//    }
//
//    suspend fun tryDisconnect(username: String, members: ConcurrentHashMap<String, Member>) {
//        members[username]?.socket?.close()
//        if (members.containsKey(username)) {
//            members.remove(username)
//        }
//    }
//
//    private suspend fun insertNewMessage(idChat: String, idUser: String, message: ChatMessageCollection) {
//        new_message.impl.createCollection(idUser)
//        this.message.impl.createCollection(idChat)
//        if (new_message.impl.checkOnExistDocument(idChat, idUser))
//            new_message.impl.insertOneMessage(idChat, idUser, message)
//        else {
//            new_message.impl.createDocument(idChat, idUser)
//            new_message.impl.insertOneMessage(idChat, idUser, message)
//        }
//    }
//
//    private suspend fun getNewMessagesCompanion(
//        idChat: String,
//        idUser: String,
//        chatEntity: ChatMainEntity,
//        callBack: suspend (List<ChatMessageDC>) -> Unit
//    ) {
////        val usersChat = main.impl.getChatById(idChat)
////        val companionUser =
////            if (usersChat.firstCompanion?.idUser != idUser) usersChat.firstCompanion!! else usersChat.secondCompanion!!
//        val new_messages = new_message.impl.getNewMessagesByIdChat(idChat, idUser)
//
////        callBack(new_messages.map {
//////            it.toCMDC().copy(
//////                author = if (it.idUser == chatEntity.firstCompanion?.idUser!!)
//////                    chatEntity.firstCompanion?.nameUser!!
//////                else
//////                    chatEntity.secondCompanion?.nameUser!!,
//////            )
////        })
//    }
//
//    private suspend fun getNewMessages(
//        idChat: String,
//        idUser: String,
//        chatEntity: ChatMainEntity,
//        callBack: suspend (List<ChatMessageDC>) -> Unit
//    ) {
//        val new_messages = new_message.impl.getNewMessagesByIdChat(idChat, idUser)
//
////        callBack(new_messages.map {
////            it.toCMDC().copy(
////                author = if (it.idUser == chatEntity.firstCompanion?.idUser!!)
////                    chatEntity.firstCompanion?.nameUser!!
////                else
////                    chatEntity.secondCompanion?.nameUser!!,
////            )
////        })
//        coroutineScope {
//            new_messages.forEach { newMess ->
//                message.impl.insertOne(idChat, newMess)
//            }
//            new_message.impl.clearOneChat(idChat, idUser)
//        }
//    }
//
//    private suspend fun createNotification(
//        senderUsername: String,
//        receiver: ChatUserEntity,
//        message: String,
//        chatEntity: ChatMainEntity
//    ) {
//        val isExistDocument = main.impl.checkOnExistNotificationDocument(receiver.idUser)
//
//        val senderUser = ""
////            if (chatEntity.firstCompanion?.nameUser == senderUsername) chatEntity.firstCompanion!! else chatEntity.secondCompanion!!
//
//        val notification = Notification(
//            id = chatEntity.idChat,
//            idUser = "senderUser.idUser",
//            title = senderUsername,
//            description = message,
//            image = "",
//            status = "Отправлено"
//        )
//
//        if (!isExistDocument)
//            main.impl.createNotificationDocument(receiver.idUser, notification)
//        else
//            main.impl.addNotification(receiver.idUser, notification)
//    }
//
//    private suspend fun createInternalNotification(
//        senderUsername: String,
//        receiver: ChatUserEntity,
//        message: String,
//        chatEntity: ChatMainEntity
//    ) {
//        val isExistDocument = main.impl.checkOnExistInternalNotificationDocument(receiver.idUser)
//
//        val senderUser = ""
////            if (chatEntity.firstCompanion?.nameUser == senderUsername) chatEntity.firstCompanion!! else chatEntity.secondCompanion!!
//
//        val notification = Notification(
//            id = chatEntity.idChat,
//            idUser = "senderUser.idUser",
//            title = senderUsername,
//            description = message,
//            image = "",
//            status = "Отправлено"
//        )
//
//        if (!isExistDocument)
//            main.impl.createInternalNotificationDocument(receiver.idUser, notification)
//        else
//            main.impl.addInternalNotification(receiver.idUser, notification)
//    }
//
//    fun addImageToChat(idChat: String, image: ByteArray): String {
//        val originString = "images/chats/$idChat"
//        val path = Paths.get(originString)
//        val file = File(originString)
//        if (!Files.exists(path)) {
//            file.mkdirs()
//        }
//        val idImage = ObjectId().toString()
//        val readyPath = "$originString/image_${idImage}.jpg"
//        File(readyPath).writeBytes(image)
//        return readyPath
//    }
//
//    suspend fun deleteMessage(deleteMessageDC: DeleteMessageEntity): Int {
//        return if (message.impl.deleteMessage(deleteMessageDC.idChat, deleteMessageDC.idMessage) == 0)
//            new_message.impl.deleteNewMessage(deleteMessageDC.idUser, deleteMessageDC.idChat, deleteMessageDC.idMessage)
//        else
//            1
//    }
//
//    suspend fun editMessage(editMessageEntity: EditMessageEntity): Boolean {
//        return if (!message.impl.editMessage(editMessageEntity.idChat, editMessageEntity.idMessage, editMessageEntity.newMessage))
//            new_message.impl.editMessage(editMessageEntity)
//        else
//            true
//    }
//
//    suspend fun checkOnExistFile() {
//
//    }
//
//    suspend fun loadFile(
//        idChat: String,
//        typeFile: String,
//        contentFile: String,
//        nameFile: String,
//        status: String,
//        idUser: String,
//        session: ChatSession,
//        members: ConcurrentHashMap<String, Member>,
//        chatEntity: ChatMainEntity
//    ) {
//
//        try {
//            if (!fileInLoads.containsKey(nameFile)) {
//                val name = UUID.randomUUID().toString()
//                fileInLoads[nameFile] = name
//            }
////                    "UUID.randomUUID().toString()"
//            val file = File("images/chats/$idChat/${fileInLoads[nameFile]}.$typeFile")
//            withContext(Dispatchers.IO) {
//                file.createNewFile()
//                val decodedString = Base64.getDecoder().decode(contentFile)
//                file.appendBytes(decodedString)
//            }
////            println("Пришло сюда file: ${file.absolutePath}")
//            if (status == "finish") {
//                println("ФИНИШ")
//
//                sendMessage(
//                    idUser,
//                    senderUsername = session.username,
//                    message = MessageDC(
//                        listFiles = listOf(
//                            FileDC(
//                                name = nameFile,
//                                size = getSizeFile(file.length()),
//                                type = typeFile,
//                                path = "images/chats/$idChat/${fileInLoads[nameFile]}.$typeFile"
//                            )
//                        ),
//                        message = ""
//                    ),
//                    members = members,
//                    chatEntity = chatEntity
//                )
////            message.impl.insertOne(
////                idChat, ChatMessageCollection(
////                    idUser = idUser,
////                    date = ServerDate.fullDate,
////                    message = "",
////                    listFiles = listOf(
////                        FileDC(
////                            name = "$nameFile.$typeFile",
////                            path = "images/chats/$idChat/${fileInLoads[nameFile]}.$typeFile"
////                        )
////                    )
////                )
////            )
//                fileInLoads.remove(nameFile)
//            }
//        }catch (e: Exception){
//            println(e)
//        }
//    }
//}
//
//val fileInLoads = ConcurrentHashMap<String, String>()
//
//fun main() {
//    println(PasswordCoder.encodeStringFS("123456"))
//}
//
////suspend fun main() {
//////    coroutineScope {
//////        async {
//////
//////
//////        }
//////    }
////    testFun(callBack = {
////        println(it)
////    })
////    println("All Ended")
////}
////
////suspend fun testFun(callBack: (String) -> Unit) {
////    coroutineScope {
////        val b = "Eee"
////        callBack(b)
////        async {
////
////            var a = 0
////            while (a < 50) {
////                a++
////                println(a)
////                delay(100)
////            }
////        }
////    }
////
////}
//
//
////class CheckInt {
////
////    constructor(
////        a: () -> Unit,
////    ) {
////        if (true)
////            a()
////    }
////
////}
////
////interface RightMenusss{
////
////    fun getFriends(){
////
////    }
////
////}
////interface iter {
////    fun getUsers(){
////        println("aaa")
////    }
////}
////interface checkInter{
////    fun check(action: () -> Unit){
////        if (false)
////            action()
////    }
////}
////object RightMenusActions: checkInter {
////
////    fun getFriends(action: () -> Unit = {
////        println("aaaa")
////    }) = check(action)
////
////}
////
////fun main() {
////
////    val sdf = SimpleDateFormat("ddhhmm")
////    val currentDate = sdf.format(Date())
////    val a = 260116
////    val b = currentDate.toInt()
////    if (b > a){
////        println("Все размутан")
////    } else
////        println("еще в муте")
////
//////        if (currentDate.takeLast(4))
//////    println(currentDate.takeLast(2))
//////    val mins = if (currentDate.takeLast(2).toInt() < 30)
//////        "15"
//////    else
//////        "49"
//////    val formattedString = currentDate.replace("[0-9]{2}\$".toRegex(), mins)
//////    println(currentDate)
//////    println(formattedString)
////////    println(PasswordCoder.encodeStringFS(currentDate.hashCode().toString()))
//////    val code = PasswordCoder.encodeStringFS(formattedString)
//////    println(code)
//////    println(PasswordCoder.decodeStringFS(code))
////
//////   RightMenusActions.getFriends()
////}