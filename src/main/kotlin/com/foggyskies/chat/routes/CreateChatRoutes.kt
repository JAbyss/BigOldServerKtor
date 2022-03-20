package com.foggyskies.chat.routes

import com.foggyskies.chat.data.model.CreateChat
import com.foggyskies.chat.extendfun.isFalse
import com.foggyskies.chat.extendfun.isTrue
import com.foggyskies.chat.room.CreateChatRoomController
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.createChatRoutes(){
    val roomChatController by inject<CreateChatRoomController>()

    post("/createChat") {
        val createChatDC = call.receive<CreateChat>()

        val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")

        isTrue(token.toString().isNotEmpty()){
            val isTokenExist = roomChatController.checkOnExistToken(token.toString())
            isFalse(isTokenExist){
                call.respond(HttpStatusCode.NotFound, "Токен не был найден.")
            }
            val userFromToken = roomChatController.getUserByUsername(createChatDC.username)

            val idChat = roomChatController.checkOnExistChat(userFromToken.id, createChatDC.idUserSecond)

            if (idChat.isEmpty()){
                val idChat = roomChatController.createChat(
                    username = createChatDC.username,
                    idUserFirst = userFromToken.id,
                    idUserSecond = createChatDC.idUserSecond
                )
                call.respond(HttpStatusCode.Created, idChat)
            } else {
//                val chatId = roomChatController.getChatId(createChatDC.idUserSecond)
                call.respond(HttpStatusCode.OK, idChat)
            }
        }
    }
}