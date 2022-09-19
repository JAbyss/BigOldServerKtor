//package com.foggyskies.server.routes.chat
//
//import com.foggyskies.server.data.model.CreateChat
//import com.foggyskies.server.extendfun.isFalse
//import com.foggyskies.server.extendfun.isTrue
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.request.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//import org.koin.ktor.ext.inject
//
//fun Route.createChatRoutes() {
//    val routController by inject<CreateChatRoutController>()
//
//    post("/createChat") {
//        val createChatDC = call.receive<CreateChat>()
//
//        val token = call.request.headers["Auth"] ?: call.respondText(status = HttpStatusCode.BadRequest, text = "Токен не получен.")
//
//        isTrue(token.toString().isNotEmpty()) {
//            val isTokenExist = routController.checkOnExistToken(token.toString())
//            isFalse(isTokenExist) {
//                call.respondText(status = HttpStatusCode.NotFound, text = "Токен не был найден.")
//            }
//            val userFromToken = routController.getUserByUsername(createChatDC.username)
//
//            val idChat = routController.checkOnExistChatByIdUsers(userFromToken.id, createChatDC.idUserSecond)
//
//            if (idChat.isEmpty()) {
//                val idChat = routController.createChat(
//                    idUserFirst = userFromToken.id,
//                    idUserSecond = createChatDC.idUserSecond
//                )
//                call.respondText(status = HttpStatusCode.Created, text = idChat)
//            } else {
//                call.respondText(status = HttpStatusCode.OK, text = idChat)
//            }
//        }
//    }
//}