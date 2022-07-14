package com.foggyskies.chat.routes

import com.foggyskies.chat.data.model.CreateChat
import com.foggyskies.chat.extendfun.isFalse
import com.foggyskies.chat.extendfun.isTrue
import com.foggyskies.chat.newroom.CreateChatRoutController
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.createChatRoutes() {
    val routController by inject<CreateChatRoutController>()

    post("/createChat") {
        val createChatDC = call.receive<CreateChat>()

        val token = call.request.headers["Auth"] ?: call.respondText(status = HttpStatusCode.BadRequest, text = "Токен не получен.")

        isTrue(token.toString().isNotEmpty()) {
            val isTokenExist = routController.checkOnExistToken(token.toString())
            isFalse(isTokenExist) {
                call.respondText(status = HttpStatusCode.NotFound, text = "Токен не был найден.")
            }
            val userFromToken = routController.getUserByUsername(createChatDC.username)

            val idChat = routController.checkOnExistChatByIdUsers(userFromToken.id, createChatDC.idUserSecond)

            if (idChat.isEmpty()) {
                val idChat = routController.createChat(
                    idUserFirst = userFromToken.id,
                    idUserSecond = createChatDC.idUserSecond
                )
                call.respondText(status = HttpStatusCode.Created, text = idChat)
            } else {
                call.respondText(status = HttpStatusCode.OK, text = idChat)
            }
        }
    }
}