package com.foggyskies.chat.routes

import com.foggyskies.chat.extendfun.isFalse
import com.foggyskies.chat.extendfun.isTrue
import com.foggyskies.chat.room.CreateChatRoomController
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
data class CreateChat(
    var username: String,
    var idUserFirst: String,
    var idUserSecond: String
)

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
            val isChatExist = roomChatController.checkOnExistChat(createChatDC.idUserSecond)


            if (!isChatExist){
                val idChat = roomChatController.createChat(
                    username = createChatDC.username,
                    idUserFirst = createChatDC.idUserFirst,
                    idUserSecond = createChatDC.idUserSecond
                )

                call.respond(HttpStatusCode.Created, idChat)
            } else {
                val chatId = roomChatController.getChatId(createChatDC.idUserSecond)
                call.respond(HttpStatusCode.OK, chatId)
            }
        }
    }
}