package com.foggyskies.server.routes.user

import com.foggyskies.server.data.model.ChatSession
import com.foggyskies.server.routes.user.requests.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject


private var sessionsMainSockets = mutableListOf<String>()

fun Route.usersRoutes() {

//    val routController by inject<UserRoutController>()

    muteChat(true)
    createPageProfile(true)
//    createMainSocket(routController, isCheckToken = true)
    getChats(true)
    addFiend(true)
    acceptRequestFriend(true)
    getFriends(true)
    getAvatar(true)
    changeAvatar(true)
    getPagesProfileByIdUser(true)
    changeAvatarProfile(true)

    getRequestsFriends(true)
    getNewMessages(true)

    logOut(true)

    deletePageProfile(true)

    searchUser(true)
    getPagesProfile(true)
//    post("/createPageProfile") {
//        val token = call.request.headers["Auth"] ?: call.respondText(
//            status = HttpStatusCode.BadRequest,
//            text = "Токен не получен."
//        )
//        val isTokenExist = routController.checkOnExistToken(token.toString())
//
//        val page = call.receive<PageProfileDC>()
//
//        if (isTokenExist) {
//            val objectId = ObjectId().toString()
//
//            val pathString = "${SystemRouting.Images.BASE_DIR}/${SystemRouting.Images.profiles_avatars}"
//            val path = Paths.get(pathString)
//            val decodedString = Base64.getDecoder().decode(page.image)
//            val file = File(pathString)
//            if (!Files.exists(path)) {
//                file.mkdirs()
//            }
//            val readyPath = "$pathString/image_$objectId.jpg"
//            File(readyPath).writeBytes(decodedString)
//
//            val user = routController.getUserByToken(token.toString())
//
//            routController.addOnePage(
//                user.idUser,
//                page.copy(id = ObjectId().toString(), image = readyPath)
//            )
//
//            call.respondText(status = HttpStatusCode.OK, text = "Страница создана")
//        } else {
//            call.respondText(status = HttpStatusCode.BadRequest, text = "Token не существует.")
//        }
//    }

//    post("/createMainSocket") {
//
//        val token = call.request.headers["Auth"] ?: call.respondText(
//            status = HttpStatusCode.BadRequest,
//            text = "Токен не получен."
//        )
//        val isTokenExist = routController.checkOnExistToken(token.toString())
//
//        if (isTokenExist) {
//            val user = routController.getUserByToken(token.toString())
//            createMainSocket(user.idUser)
//            call.respondText(user.idUser, status = HttpStatusCode.OK)
//        } else {
//            call.respondText("Token не существует.", status = HttpStatusCode.BadRequest)
//        }
//    }

//    webSocket("/user") {
//        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
//        val isTokenExist = routController.checkOnExistToken(token.toString())
//
//        if (isTokenExist) {
//            val user = routController.getUserByToken(token.toString())
//
//            val session = call.sessions.get<ChatSession>()
//            if (session == null) {
//                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
//                return@webSocket
//            }
//            try {
//                incoming.consumeEach { frame ->
//                    if (frame is Frame.Text) {
//                        if (frame.readText().length >= 3) {
//                            val users = routController.searchUsers(idUser = user.idUser, frame.readText())
//                            if (users.isNotEmpty()) {
//                                val parsedString = Json.encodeToString(users)
//                                send(parsedString)
//                            } else {
//                                send("[]")
//                            }
//                        } else {
//                            send("[]")
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                println(e)
//            }
//        } else {
//            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Неверный токен"))
//            return@webSocket
//        }
//    }
//    get("/chats") {
//
//        val token = call.request.headers["Auth"] ?: call.respondText(
//            status = HttpStatusCode.BadRequest,
//            text = "Токен не получен."
//        )
//
//        isTrue(token.toString().isNotEmpty()) {
//            val isTokenExist = routController.checkOnExistToken(token.toString())
//
//            if (isTokenExist) {
//                val chats = routController.getChats(token.toString())
//                call.respond(HttpStatusCode.OK, chats)
//            } else {
//                call.respondText(status = HttpStatusCode.NotFound, text = "Токен не был найден.")
//            }
//        }
//    }
//    post("/addFriend") {
//        val idUserReceiver: IdUserReceiver = (call.receive<IdUserReceiver>()
//            ?: call.respond(HttpStatusCode.BadRequest, "IdUser не получен.")) as IdUserReceiver
//
//        val token = call.request.headers["Auth"] ?: call.respondText(
//            status = HttpStatusCode.BadRequest,
//            text = "Токен не получен."
//        )
//
//        isTrue(token.toString().isNotEmpty()) {
//            val isTokenExist = routController.checkOnExistToken(token.toString())
//
//            if (isTokenExist) {
//
//                val user = routController.getUserByToken(token.toString())
//
//                val userNameId = UserNameID(
//                    id = user.idUser,
//                    username = user.username
//                )
//
//                routController.addRequestToFriend(userNameId, idUserReceiver.id)
//                call.respond(HttpStatusCode.OK)
//            }
//        }
//
//    }

//    get("/friends") {
//        val token = call.request.headers["Auth"] ?: call.respondText(
//            status = HttpStatusCode.BadRequest,
//            text = "Токен не получен."
//        )
//
//        isTrue(token.toString().isNotEmpty()) {
//            val isTokenExist = routController.checkOnExistToken(token.toString())
//
//            if (isTokenExist) {
//                val listFriends = routController.getFriends(token.toString())
//                call.respond(HttpStatusCode.OK, listFriends)
//            } else {
//                call.respondText(status = HttpStatusCode.BadRequest, text = "Токен неверный.")
//            }
//        }
//    }
//    get("/avatar") {
//        val token = call.request.headers["Auth"] ?: call.respondText(
//            status = HttpStatusCode.BadRequest,
//            text = "Токен не получен."
//        )
//
//        val isTokenExist = routController.checkOnExistToken(token.toString())
//
//        if (isTokenExist) {
//            val idUser = routController.getUserByToken(token.toString()).idUser
//            val avatar = routController.getAvatarByIdUser(idUser)
//            call.respondText(status = HttpStatusCode.OK, text = avatar)
//        } else {
//            call.respondText(status = HttpStatusCode.BadRequest, text = "Токен не существует.")
//        }
//    }

//    post("/changeAvatar") {
//        val token = call.request.headers["Auth"] ?: call.respondText(
//            status = HttpStatusCode.BadRequest,
//            text = "Токен не получен."
//        )
//
////        val image = call.parameters["image"] ?: call.respond(HttpStatusCode.BadRequest, "Image не получен.")
//
//        val image = call.receiveText()
//
//        val isTokenExist = routController.checkOnExistToken(token.toString())
//
//        if (isTokenExist) {
//            val idUser = routController.getUserByToken(token.toString()).idUser
//
//            val avatarOld = routController.getAvatarByIdUser(idUser)
//
//            if (avatarOld.isNotEmpty()) {
//                routController.deleteAvatarByIdUser(avatarOld)
//            }
//
//            val decodedString = Base64.getDecoder().decode(image)
//
//            val pathString = "${SystemRouting.Images.BASE_DIR}/${SystemRouting.Images.AVATARS}"
//            val path = Paths.get(pathString)
//            val file = File(pathString)
//            if (!Files.exists(path)) {
//                file.mkdirs()
//            }
////            val countFiles = File("images/avatars/").list().size + 1
//            val name = ObjectId().toString()
//            val readyString = "$pathString/avatar_${name}.jpg"
//            File(readyString).writeBytes(decodedString)
//            val avatar = routController.changeAvatarByUserId(idUser, readyString)
//            call.respondText(status = HttpStatusCode.OK, text =  avatar)
//        } else {
//            call.respondText(status = HttpStatusCode.BadRequest, text = "Токен не существует.")
//        }
//    }

//    get("/getPagesProfileByIdUser{idUser}") {
//        val token = call.request.headers["Auth"] ?: call.respondText(
//            status = HttpStatusCode.BadRequest,
//            text = "Токен не получен."
//        )
//
//        val idOtherUser = call.parameters["idUser"] ?: call.respondText(
//            status = HttpStatusCode.BadRequest,
//            text = "IdUser не получен."
//        )
//
//        val isTokenExist = routController.checkOnExistToken(token.toString())
//
//        if (isTokenExist) {
//            val listPages = routController.getAllPagesByIdUser(idOtherUser.toString())
//            call.respond(HttpStatusCode.OK, listPages)
//        } else {
//            call.respondText(status = HttpStatusCode.BadRequest, text = "Токен не существует.")
//        }
//    }


//    post("/changeAvatarProfile") {
//        val token = call.request.headers["Auth"] ?: call.respondText(
//            status = HttpStatusCode.BadRequest,
//            text = "Токен не получен."
//        )
//        val idPage = call.request.headers["idPage"] ?: ""
//
//        val image = call.receiveText()
//
//        val isTokenExist = routController.checkOnExistToken(token.toString())
//
//        if (isTokenExist) {
//
//            val avatarOld = routController.getAvatarPageProfile(idPage)
//
//            if (avatarOld.isNotEmpty()) {
//                routController.deleteAvatarByIdUser(avatarOld)
//            }
//
//            val pathString = "${SystemRouting.Images.BASE_DIR}/${SystemRouting.Images.profiles_avatars}"
//            val path = Paths.get(pathString)
//            val file = File(pathString)
//            if (!Files.exists(path)) {
//                file.mkdirs()
//            }
//            val decodedString = Base64.getDecoder().decode(image)
//            val name = ObjectId().toString()
//            val readyString = "$pathString/image_${name}.jpg"
//            File(readyString).writeBytes(decodedString)
//            val avatar = routController.changeAvatarByIdPage(idPage, readyString)
//            call.respond(HttpStatusCode.OK, avatar)
//        } else {
//            call.respondText(status = HttpStatusCode.BadRequest, text = "Токен не существует.")
//        }
//    }
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