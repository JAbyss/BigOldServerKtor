package com.foggyskies.chat.routes

import com.foggyskies.chat.data.model.ChatSession
import com.foggyskies.chat.databases.message.models.MessageDC
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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import java.io.File
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


                    val token =
                        call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
                    val chatEntity = routController.getChat(idChat)

                    val session = call.sessions.get<ChatSession>()
                    if (session == null) {
                        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
                        sessionsChat.remove(idChat)
                        return@webSocket
                    }

                    val idUser = routController.getIdUserByToken(token.toString())

                    try {
                        post("/fileUpload") {

                            val a = call.receive<BodyFile>()

                            routController.loadFile(
                                a.idChat.toString(),
                                a.typeFile.toString(),
                                a.contentFile.toString(),
                                a.nameFile.toString(),
                                a.status.toString(),
                                a.idUser.toString(),
                                session,
                                members,
                                chatEntity
                            )
                            call.respond(HttpStatusCode.OK)
                        }
                        routController.onJoin(
                            idUser = idUser,
                            username = session.username,
                            sessionId = session.sessionID,
                            socket = this,
                            members = members,
                            chatEntity = chatEntity
                        )
                        incoming.consumeEach { frame ->
                            if (frame is Frame.Text) {
                                val rawMessage = frame.readText()
                                val regexCommand = "^nextMessages".toRegex()


                                if (regexCommand.find(rawMessage) != null) {
                                    val lastMessageId = "(?<=\\|).+".toRegex().find(rawMessage)?.value!!
                                    routController.sendNextMessages(this, chatEntity, lastMessageId)
                                    println("Послал старые сообщения")
                                } else {
                                    val message = Json.decodeFromString<MessageDC>(rawMessage)
                                    routController.sendMessage(
                                        idUser = idUser,
                                        senderUsername = session.username,
                                        message = message,
                                        members = members,
                                        chatEntity = chatEntity
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        println(e)
                    }
                    routController.tryDisconnect(members = members, username = session.username)
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
        post("/deleteMessage") {
            val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")

            val deleteChatEntity = call.receive<DeleteMessageEntity>()

            val isTokenExist = routController.checkOnExistToken(token.toString())
            if (isTokenExist) {
                val code = routController.deleteMessage(deleteChatEntity)
                call.respond(HttpStatusCode.OK, code)
            } else
                call.respond(HttpStatusCode.BadRequest, "Токен не существует.")
        }

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
}

@kotlinx.serialization.Serializable
data class BodyFile(
    val idChat: String,
    val nameFile: String,
    val contentFile: String,
    val status: String,
    val idUser: String,
    val typeFile: String
)

@kotlinx.serialization.Serializable
data class DeleteMessageEntity(
    val idMessage: String,
    val idUser: String,
    val idChat: String
)
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