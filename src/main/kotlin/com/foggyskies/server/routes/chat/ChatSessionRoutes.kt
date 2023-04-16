package com.foggyskies.server.routes.chat

import com.foggyskies.server.routes.chat.requests.*
import com.foggyskies.server.routes.chat.sockets.chatSession
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

data class ChatUserAndSocket(
    val idUser: String,
    val socket: WebSocketSession
)

val roomsChats = ConcurrentHashMap<String, MutableList<ChatUserAndSocket>>()

fun Route.chatSessionRoutes() {

    createChat(true)

    route("/subscribes") {

        createChatSession(true)
        deleteMessage(true)
        editMessage(true)

        getMessages(true)
        getNewMessages(true)

        fileLoad(true)
        sendMessageWithContent(true)
        //        val routController by inject<MessagesRoutController>()
        chatSession()

//        webSocket("/chatSessions") {
//
//            val token = call.request.headers["Auth"] ?: return@webSocket call.respond(
//                HttpStatusCode.BadRequest,
//                "Токен не получен."
//            )
//            val idChat = call.request.headers["idChat"] ?: return@webSocket call.respond(HttpStatusCode.BadRequest)
//            val user = getUserByToken(token)
//
//            if (roomsChats[idChat] == null)
//                roomsChats[idChat] = mutableListOf(ChatUserAndSocket(user.idUser, this))
//            else
//                roomsChats[idChat]?.addIfNotExist(ChatUserAndSocket(user.idUser, this))
//
////                val chatEntity = routController.getChat(idChat)
//
////                    try {
////                routController.onJoin(
////                    idUser = idUser,
////                    username = session.username,
////                    sessionId = session.sessionID,
////                    socket = this,
////                    members = members,
////                    chatEntity = chatEntity
////                )
////                post("/fileUpload") {
////
////                    try {
////
////                        val a = call.receive<BodyFile>()
////
////                        routController.loadFile(
////                            a.idChat,
////                            a.typeFile,
////                            a.contentFile,
////                            a.nameFile,
////                            a.status,
////                            a.idUser,
////                            session,
////                            members,
////                            chatEntity
////                        )
////                    } catch (e: Exception) {
////                        println(e)
////                    }
////                    call.respond(HttpStatusCode.OK)
////                }
////            suspend fun sendAllinChat(idChat: String, message: Frame){
////
////            }
////            try {
////                            for (frame in incoming){
//            incoming.consumeEach { frame ->
//                val message = (frame as Frame.Text).readText()
//                println(
//                    "Frame " + message +
//                            "\nidChat " + idChat +
//                            "\nidUser" + user.idUser
//                )
////                println(
////                    "\n\n" +
////                            "Hash $roomsChats"
////                )
//                roomsChats[idChat]?.forEach {
//                    it.socket.send(message)
//                }
////                    sendAllinChat(idChat, frame)
////                        if (frame is Frame.Text) {
////                            val rawMessage = frame.readText()
////                            val regexCommand = "^nextMessages".toRegex()
////
////
////                            if (regexCommand.find(rawMessage) != null) {
////                                val lastMessageId = "(?<=\\|).+".toRegex().find(rawMessage)?.value!!
////                                routController.sendNextMessages(this, chatEntity, lastMessageId)
////                                println("Послал старые сообщения")
////                            } else {
////                                val message = Json.decodeFromString<MessageDC>(rawMessage)
////                                routController.sendMessage(
////                                    idUser = idUser,
////                                    senderUsername = session.username,
////                                    message = message,
////                                    members = members,
////                                    chatEntity = chatEntity
////                                )
////                            }
////                        }
//            }
////            } catch (e: Exception) {
////                println(e)
////            }
//            println("Я закрываюсь")
////                    } catch (e: Exception) {
////                        println(e)
////                    }
////                routController.tryDisconnect(members = members, username = session.username)
//        }
    }
//        get("/createChatSession{idChat}") {
//            val idChat =
//                call.parameters["idChat"] ?: call.respond(HttpStatusCode.BadRequest, "Параметр idChat не получен.")
//            if (idChat.toString().isNotEmpty()) {
//                createSocket(idChat.toString(), sessionsChat)
//                call.respond(HttpStatusCode.Created)
//            }
//        }

//        post("/fileUpload") {
////
//                            try {
//
//                                val a = call.receive<BodyFile>()
//
////                                routController.loadFile(
////                                    a.idChat,
////                                    a.typeFile,
////                                    a.contentFile,
////                                    a.nameFile,
////                                    a.status,
////                                    a.idUser,
//////                                    session,
//////                                    members,
//////                                    chatEntity
////                                )
//                            }catch (e: Exception){
//                                println(e)
//                            }
//                            call.respond(HttpStatusCode.OK)
//                        }
    //TODO Под вопросом
    /*post("/addImageToMessage{idChat}") {
        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")

        val image = call.receiveText() ?: call.respond(HttpStatusCode.BadRequest, "Image не получен.")

        val idChat =
            call.parameters["idChat"] ?: call.respond(HttpStatusCode.BadRequest, "IdChat не получен.")

        val isTokenExist = routController.checkOnExistToken(token.toString())
        if (isTokenExist) {
            val decodedString = Base64.getDecoder().decode(image.toString())
            val addressImage = routController.addImageToChat(idChat.toString(), decodedString)
            call.respondText(status = HttpStatusCode.OK, text = addressImage)
        } else {
            call.respondText(status = HttpStatusCode.BadRequest, text = "Token не существует.")
        }
    }*/
//        post("/deleteMessage") {
//            val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
//
//            val deleteChatEntity = call.receive<DeleteMessageEntity>()
//
//            val isTokenExist = routController.checkOnExistToken(token.toString())
//            if (isTokenExist) {
//                val code = routController.deleteMessage(deleteChatEntity)
//                call.respondText(status = HttpStatusCode.OK, text = code.toString())
//            } else
//                call.respondText(status = HttpStatusCode.BadRequest, text = "Токен не существует.")
//        }

//        post("/editMessage") {
//            val token = call.request.headers["Auth"] ?: call.respondText(
//                status = HttpStatusCode.BadRequest,
//                text = "Токен не получен."
//            )
//
//            val editMessageEntity = call.receive<EditMessageEntity>()
//
//            val isTokenExist = routController.checkOnExistToken(token.toString())
//            if (isTokenExist) {
//                val code = routController.editMessage(editMessageEntity)
//                call.respondText(status = HttpStatusCode.OK, text = code.toString())
//            } else
//                call.respondText(status = HttpStatusCode.BadRequest, text = "Токен не существует.")
//        }
//        fun createPostForLoadFile(idLoad: String){
//            val a = post("/$idLoad") {
//                call.respond(HttpStatusCode.OK, "Я ответил")
//                this.cancel()
//            }
//        }


    webSocket("/testLoad") {


//            val token =
//                call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
//            val chatEntity = routController.getChat(idChat)

//            val session = call.sessions.get<ChatSession>()
//            if (session == null) {
//                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
////                sessionsChat.remove(idChat)
//                return@webSocket
//            }

//            val idUser = routController.getIdUserByToken(token.toString())

        try {
//                routController.onJoin(
//                    idUser = idUser,
//                    username = session.username,
//                    sessionId = session.sessionID,
//                    socket = this,
//                    members = members,
//                    chatEntity = chatEntity
//                )
            incoming.consumeEach { frame ->
                println("Просто сокет получил")

                if (frame is Frame.Text) {
                    println("Получил байты")
                    val data = Base64.getDecoder().decode(frame.readText())

                    File("images/test/newFile1.zip").appendBytes(data)
                }
            }
        } catch (e: Exception) {
            println(e)
        }
//            routController.tryDisconnect(members = members, username = session.username)
    }

}

@kotlinx.serialization.Serializable
data class BodyFile(
    val idUpload: String,
//    val idChat: String,
    val nameFile: String,
    val contentFile: String,
    val status: String,
//    val idUser: String,
    val typeFile: String,
    val typeLoad: TypeLoadFile,
    val infoData: String
)

@kotlinx.serialization.Serializable
data class DeleteMessageEntity(
    val idMessage: String,
    val idUser: String,
    val idChat: String
)

@kotlinx.serialization.Serializable
data class EditMessageEntity(
    val idMessage: String,
    val idUser: String,
    val idChat: String,
    val newMessage: String
)

//fun main() {
//    val file = File("C:\\All project\\ServerPetAppKtor\\images\\chats\\62913f54cc47483b1695182a\\18c02871-c16d-4323-b5af-6a3667ac5ed6.mp4")
//    println(file.length())
//}

//TODO ПЕРЕДАЧА ФАЙЛОВ ТУТ
//suspend fun main() {
//    val a = " kfajkfjakfwakjf"
//    val file = File("Диплом Ахмеров Р.А Готово 90 проц.docx")
//
////    file.appendBytes(a.toByteArray())
//    println(file.length())
//    val formatMute = SimpleDateFormat("hh:mm:ss:SS")
//
//    val fullDate: String = formatMute.format(Date())
//    println("DAte start " + formatMute.format(Date()))
//    val fileFilm = File("C:\\Users\\rusl2\\Downloads\\Eternals.2021.1080p.WEB-DL.DD5.1.H.264-EniaHD.mkv")
////    println("Bytes " + fileFilm.readBytes())
////    fileFilm.readBytes()
//
//
//    val forthGigaFilm = File("film.mkv")
//
//    CoroutineScope(Dispatchers.Default).launch {
//        println(" AWwdaw " + forthGigaFilm.length())
//        forthGigaFilm.length()
////        var segments = forthGigaFilm.length()
////        while (segments > 50000){
////            segments /= 2
////        }
//        var arr = ByteArray(4096)
//        var allReaded = 0L
//        val maxSize = forthGigaFilm.length()
//        forthGigaFilm.inputStream().use { input ->
//            do {
//                val size =
//                    if (maxSize - allReaded < arr.size) {
//                    println("Check ${maxSize - allReaded}")
//                    arr = ByteArray((maxSize - allReaded).toInt())
////                    println(a.size)
//                    input.read(arr)
//                } else
//                    input.read(arr)
//                if (size <= 0) {
//                    println(allReaded)
//                    break
//                } else {
//                    allReaded += size
//                    File("фвцвацфа.mkv").appendBytes(arr)
//                }
//            } while (true)
//        }
//    }.join()
//
//
//    println("DAte finish " + formatMute.format(Date()))
//    println("Leight " + fileFilm.length())
//
//}

//data class FileLoadDC(
//    val nameFile: String,
//    val
//)
//fun main() {
//    val a = 1000L
//    println(a.toUByte())
//    println(a / 8)
//}