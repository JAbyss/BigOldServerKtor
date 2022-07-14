package com.foggyskies.chat.routes

import com.foggyskies.chat.data.model.ChatSession
import com.foggyskies.chat.databases.main.models.IdUserReceiver
import com.foggyskies.chat.databases.main.models.MuteChatDC
import com.foggyskies.chat.databases.main.models.PageProfileDC
import com.foggyskies.chat.databases.main.models.UserNameID
import com.foggyskies.chat.extendfun.generateUUID
import com.foggyskies.chat.extendfun.isTrue
import com.foggyskies.chat.newroom.UserRoutController
import com.foggyskies.plugin.SystemRouting
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.collections.*
import io.ktor.websocket.*
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import org.koin.ktor.ext.inject
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


private var sessionsMainSockets = ConcurrentList<String>()

fun Route.usersRoutes() {

    val routController by inject<UserRoutController>()
    fun createMainSocket(idUser: String) {
        if (!sessionsMainSockets.contains(idUser)) {
            sessionsMainSockets.add(idUser)
            webSocket("/mainSocket/$idUser") {

                val session = call.sessions.get<ChatSession>()
                if (session == null) {
                    routController.setStatusUser(idUser, "Не в сети")
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
//                    sessionsMainSockets.remove(idUser)
                    return@webSocket
                }
                val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
                val isTokenExist = routController.checkOnExistToken(token.toString())

                val map_actions = mapOf(
                    "getFriends" to suspend { "getFriends|${Json.encodeToString(routController.getFriends(token.toString()))}" },
//                    "getChats" to suspend { "getFriends|${Json.encodeToString(routController.getChats(token.toString()))}" },
                    "getRequestsFriends" to suspend {
                        "getRequestsFriends|${
                            Json.encodeToString(
                                routController.getRequestsFriends(
                                    token.toString()
                                )
                            )
                        }"
                    },
                    "getChats" to suspend {
                        "getChats|${Json.encodeToString(routController.getChats(token.toString()))}"
                    },
                    "getPagesProfile" to suspend {
                        "getPagesProfile|${
                            Json.encodeToString(
                                routController.getAllPagesByIdUser(
                                    idUser
                                )
                            )
                        }"
                    },
                    "getNewMessages" to suspend {
                        "getNewMessages|${Json.encodeToString(routController.getAllNewMessages(idUser))}"
                    },
                )
                val map_action_unit = mapOf(
                    "logOut" to suspend { routController.logOut(token.toString()) },
                    "deleteAllSentNotifications" to suspend { routController.deleteAllSentNotifications(idUser) }
//            "acceptRequestFriend" to suspend { roomUserController.acceptRequestFriend() }
                )

                if (isTokenExist) {
                    routController.setStatusUser(idUser, "В сети")
                    val user = routController.getUserByToken(token.toString())

//                    try {
                    val watcherFriends = async {
                        routController.watchForFriend(idUser, this@webSocket)
                    }
                    val watcherRequestsFriends = async {
                        routController.watchForRequestsFriends(idUser, this@webSocket)
                    }
                    //fixme На время выключено
//                    val watcherInternalNotifications = async {
//                        routController.watchForInternalNotifications(idUser, this@webSocket)
//                    }
                    val watcherNewMessages = async {
                        routController.watchForNewMessages(idUser, this@webSocket)
                    }


                    incoming.consumeEach { frame ->
                        if (frame is Frame.Text) {
                            val incomingData = frame.readText()
                            val regex = "\\w+(?=\\|)".toRegex()
                            val action = regex.find(incomingData)?.value

                            if (map_actions.containsKey(action)) {
                                val friends = map_actions[action]?.invoke()
                                if (friends != null) {
                                    send(friends)
                                }
                            } else if (map_action_unit.containsKey(action)) {
                                map_action_unit[action]?.invoke()
                            } else if (action == "acceptRequestFriend") {
                                val idUserReceiver = incomingData.replace("$action|", "")
                                val userSender = UserNameID(
                                    id = user.idUser,
                                    username = user.username
                                )
                                routController.acceptRequestFriend(userSender, idUserReceiver)
                            } else if (action == "addFriend") {
                                val idReceiver = incomingData.replace("$action|", "")
                                routController.addRequestToFriend(
                                    UserNameID(
                                        id = user.idUser,
                                        username = user.username
                                    ), idReceiver
                                )
                            }
                            /**
                             * loadFile|{Путь к файлу}|{Название файла}
                             */
                            else if (action == "loadFile") {
                                val data = "(?<=\\|).+(?=\\|)".toRegex().find(incomingData)?.value!!.split("|")
                                val pathWithFile = data[0]
                                val nameOperation = data[1] + "-" + generateUUID(7)
                                routController.loadFile(pathWithFile, nameOperation, this)
                            }
                        }
                    }
//                    } catch (e: Exception) {
//                        println(e)
//                    } finally {
                    routController.setStatusUser(idUser, "Не в сети")
                    watcherFriends.cancel()
                    watcherRequestsFriends.cancel()
                    watcherNewMessages.cancel()
//                    watcherInternalNotifications.cancel()
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Неверный токен"))
//                    sessionsMainSockets.remove(idUser)
                    return@webSocket
//                    }
                } else {
                    routController.setStatusUser(idUser, "Не в сети")
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Неверный токен"))
//                    sessionsMainSockets.remove(idUser)
                    return@webSocket
                }
            }
        }
    }

    static("/") {
        files(".")
    }

    post("/muteChat") {
        val token = call.request.headers["Auth"] ?: call.respondText(
            status = HttpStatusCode.BadRequest,
            text = "Токен не получен."
        )
        val isTokenExist = routController.checkOnExistToken(token.toString())

        val mutedChat = call.receive<MuteChatDC>()

        if (isTokenExist) {
            routController.muteChat(mutedChat, token.toString())
            call.respond(HttpStatusCode.OK)
        } else {
            call.respondText(status = HttpStatusCode.BadRequest, text = "Token не существует.")
        }
    }

    post("/createPageProfile") {
        val token = call.request.headers["Auth"] ?: call.respondText(
            status = HttpStatusCode.BadRequest,
            text = "Токен не получен."
        )
        val isTokenExist = routController.checkOnExistToken(token.toString())

        val page = call.receive<PageProfileDC>()

        if (isTokenExist) {
            val objectId = ObjectId().toString()

            val pathString = "${SystemRouting.Images.BASE_DIR}/${SystemRouting.Images.profiles_avatars}"
            val path = Paths.get(pathString)
            val decodedString = Base64.getDecoder().decode(page.image)
            val file = File(pathString)
            if (!Files.exists(path)) {
                file.mkdirs()
            }
            val readyPath = "$pathString/image_$objectId.jpg"
            File(readyPath).writeBytes(decodedString)

            val user = routController.getUserByToken(token.toString())

            routController.addOnePage(
                user.idUser,
                page.copy(id = ObjectId().toString(), image = readyPath)
            )

            call.respondText(status = HttpStatusCode.OK, text = "Страница создана")
        } else {
            call.respondText(status = HttpStatusCode.BadRequest, text = "Token не существует.")
        }
    }
    post("/createMainSocket") {

        val token = call.request.headers["Auth"] ?: call.respondText(
            status = HttpStatusCode.BadRequest,
            text = "Токен не получен."
        )
        val isTokenExist = routController.checkOnExistToken(token.toString())

        if (isTokenExist) {
            val user = routController.getUserByToken(token.toString())
            createMainSocket(user.idUser)
            call.respondText(user.idUser, status = HttpStatusCode.OK)
        } else {
            call.respondText("Token не существует.", status = HttpStatusCode.BadRequest)
        }
    }

    webSocket("/user") {
        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
        val isTokenExist = routController.checkOnExistToken(token.toString())

        if (isTokenExist) {
            val user = routController.getUserByToken(token.toString())

            val session = call.sessions.get<ChatSession>()
            if (session == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
                return@webSocket
            }
            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        if (frame.readText().length >= 3) {
                            val users = routController.searchUsers(idUser = user.idUser, frame.readText())
                            if (users.isNotEmpty()) {
                                val parsedString = Json.encodeToString(users)
                                send(parsedString)
                            } else {
                                send("[]")
                            }
                        } else {
                            send("[]")
                        }
                    }
                }
            } catch (e: Exception) {
                println(e)
            }
        } else {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Неверный токен"))
            return@webSocket
        }
    }

    get("/chats") {

        val token = call.request.headers["Auth"] ?: call.respondText(
            status = HttpStatusCode.BadRequest,
            text = "Токен не получен."
        )

        isTrue(token.toString().isNotEmpty()) {
            val isTokenExist = routController.checkOnExistToken(token.toString())

            if (isTokenExist) {
                val chats = routController.getChats(token.toString())
                call.respond(HttpStatusCode.OK, chats)
            } else {
                call.respondText(status = HttpStatusCode.NotFound, text = "Токен не был найден.")
            }
        }
    }

    post("/addFriend") {
        val idUserReceiver: IdUserReceiver = (call.receive<IdUserReceiver>()
            ?: call.respond(HttpStatusCode.BadRequest, "IdUser не получен.")) as IdUserReceiver

        val token = call.request.headers["Auth"] ?: call.respondText(
            status = HttpStatusCode.BadRequest,
            text = "Токен не получен."
        )

        isTrue(token.toString().isNotEmpty()) {
            val isTokenExist = routController.checkOnExistToken(token.toString())

            if (isTokenExist) {

                val user = routController.getUserByToken(token.toString())

                val userNameId = UserNameID(
                    id = user.idUser,
                    username = user.username
                )

                routController.addRequestToFriend(userNameId, idUserReceiver.id)
                call.respond(HttpStatusCode.OK)
            }
        }

    }

    get("/friends") {
        val token = call.request.headers["Auth"] ?: call.respondText(
            status = HttpStatusCode.BadRequest,
            text = "Токен не получен."
        )

        isTrue(token.toString().isNotEmpty()) {
            val isTokenExist = routController.checkOnExistToken(token.toString())

            if (isTokenExist) {
                val listFriends = routController.getFriends(token.toString())
                call.respond(HttpStatusCode.OK, listFriends)
            } else {
                call.respondText(status = HttpStatusCode.BadRequest, text = "Токен неверный.")
            }
        }
    }

    get("/avatar") {
        val token = call.request.headers["Auth"] ?: call.respondText(
            status = HttpStatusCode.BadRequest,
            text = "Токен не получен."
        )

        val isTokenExist = routController.checkOnExistToken(token.toString())

        if (isTokenExist) {
            val idUser = routController.getUserByToken(token.toString()).idUser
            val avatar = routController.getAvatarByIdUser(idUser)
            call.respondText(status = HttpStatusCode.OK, text = avatar)
        } else {
            call.respondText(status = HttpStatusCode.BadRequest, text = "Токен не существует.")
        }
    }

    post("/changeAvatar") {
        val token = call.request.headers["Auth"] ?: call.respondText(
            status = HttpStatusCode.BadRequest,
            text = "Токен не получен."
        )

//        val image = call.parameters["image"] ?: call.respond(HttpStatusCode.BadRequest, "Image не получен.")

        val image = call.receiveText()

        val isTokenExist = routController.checkOnExistToken(token.toString())

        if (isTokenExist) {
            val idUser = routController.getUserByToken(token.toString()).idUser

            val avatarOld = routController.getAvatarByIdUser(idUser)

            if (avatarOld.isNotEmpty()) {
                routController.deleteAvatarByIdUser(avatarOld)
            }

            val decodedString = Base64.getDecoder().decode(image)

            val pathString = "${SystemRouting.Images.BASE_DIR}/${SystemRouting.Images.AVATARS}"
            val path = Paths.get(pathString)
            val file = File(pathString)
            if (!Files.exists(path)) {
                file.mkdirs()
            }
//            val countFiles = File("images/avatars/").list().size + 1
            val name = ObjectId().toString()
            val readyString = "$pathString/avatar_${name}.jpg"
            File(readyString).writeBytes(decodedString)
            val avatar = routController.changeAvatarByUserId(idUser, readyString)
            call.respondText(status = HttpStatusCode.OK, text =  avatar)
        } else {
            call.respondText(status = HttpStatusCode.BadRequest, text = "Токен не существует.")
        }
    }
    get("/getPagesProfileByIdUser{idUser}") {
        val token = call.request.headers["Auth"] ?: call.respondText(
            status = HttpStatusCode.BadRequest,
            text = "Токен не получен."
        )

        val idOtherUser = call.parameters["idUser"] ?: call.respondText(
            status = HttpStatusCode.BadRequest,
            text = "IdUser не получен."
        )

        val isTokenExist = routController.checkOnExistToken(token.toString())

        if (isTokenExist) {
            val listPages = routController.getAllPagesByIdUser(idOtherUser.toString())
            call.respond(HttpStatusCode.OK, listPages)
        } else {
            call.respondText(status = HttpStatusCode.BadRequest, text = "Токен не существует.")
        }
    }

    post("/changeAvatarProfile") {
        val token = call.request.headers["Auth"] ?: call.respondText(
            status = HttpStatusCode.BadRequest,
            text = "Токен не получен."
        )
        val idPage = call.request.headers["idPage"] ?: ""

        val image = call.receiveText()

        val isTokenExist = routController.checkOnExistToken(token.toString())

        if (isTokenExist) {

            val avatarOld = routController.getAvatarPageProfile(idPage)

            if (avatarOld.isNotEmpty()) {
                routController.deleteAvatarByIdUser(avatarOld)
            }

            val pathString = "${SystemRouting.Images.BASE_DIR}/${SystemRouting.Images.profiles_avatars}"
            val path = Paths.get(pathString)
            val file = File(pathString)
            if (!Files.exists(path)) {
                file.mkdirs()
            }
            val decodedString = Base64.getDecoder().decode(image)
            val name = ObjectId().toString()
            val readyString = "$pathString/image_${name}.jpg"
            File(readyString).writeBytes(decodedString)
            val avatar = routController.changeAvatarByIdPage(idPage, readyString)
            call.respond(HttpStatusCode.OK, avatar)
        } else {
            call.respondText(status = HttpStatusCode.BadRequest, text = "Токен не существует.")
        }
    }
}

//fun Boolean.toInt(): Int {
//    return if (this)
//        1 else
//        0
//}

//fun main() {
//
//    val (A, B) = Pair(-2, 2)
//
//    val f = (A + B == 2)
//    val s = A != B
//    val th = A < 0
//
//    val ad = 0 > 0 && true
//
////    val z = 1 in 0..0
//    println((if (f) 1 else 0) !in 0..(if (s) 1 else 0))
//    println("first $f, second $s, third $th")
//    println("itog $ad")
//
//}