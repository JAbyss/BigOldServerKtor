package com.foggyskies.chat.routes

import com.foggyskies.chat.data.model.ChatPreviewEntity
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable

//fun Route.chatListRoutes() {
//    route("/chatslist") {
//        get {
//            val listChats = listOf(
//                ChatPreviewEntity(
//                    idChat = "123",
//                    image = "http://192.168.0.88:8080/photo?name=puppy_1.jpg",
//                    lastMessage = "Hi friend",
//                    chatName = "Kalterfad"
//                ),
//                ChatPreviewEntity(
//                    idChat = "321",
//                    image = "http://192.168.0.88:8080/photo?name=puppy_1.jpg",
//                    lastMessage = "fwafawFwfwf",
//                    chatName = "Ебаный насрал"
//                ),
//                ChatPreviewEntity(
//                    idChat = "431",
//                    image = "http://192.168.0.88:8080/photo?name=puppy_1.jpg",
//                    lastMessage = "Ahahahahahahah",
//                    chatName = "Аафафафацаафц"
//                ),
//            )
//            call.respond(listChats)
//        }
//    }
//}