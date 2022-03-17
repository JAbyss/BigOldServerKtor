package com.foggyskies.chat.routes

import com.foggyskies.chat.room.AuthRoomController
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

@kotlinx.serialization.Serializable
data class RegistrationUserDC(
    var username: String,
    var password: String,
    var e_mail: String
)

@kotlinx.serialization.Serializable
data class LoginUserDC(
    var username: String,
    var password: String
)

fun Route.authRoutes(roomAuthController: AuthRoomController) {
    post("/registration") {
        val params = call.receive<RegistrationUserDC>()

        val isUserExist = roomAuthController.checkOnExistUser(username = params.username)
        val isEmailExist = roomAuthController.checkOnExistEmail(e_mail = params.e_mail)


        if (isUserExist)
            call.respond(status = HttpStatusCode.Conflict, message = "Пользователь с таким логином уже существует.")
        else {
            if(isEmailExist){
                call.respond(status = HttpStatusCode.Conflict, message = "Пользователь с таким e_mail уже существует.")
            } else {
                val user = UserMainEntity(
                    username = params.username,
                    password = params.password,
                    e_mail = params.e_mail,
                )
                roomAuthController.insertUser(user)
                call.respond(status = HttpStatusCode.Created, message = "Регистрация прошла успешно.")
            }
        }
    }
    post("/auth") {
            val params = call.receive<LoginUserDC>()

            val isUserExist = roomAuthController.checkOnExistUser(params.username)

            val isCorrectPassword = roomAuthController.checkPasswordOnCorrect(params.password)

            if (isCorrectPassword)
                if (isUserExist) {
                    var token = ""
                    val isTokenExist = roomAuthController.checkOnExistToken(params.username)
                    if (!isTokenExist) {
                        token = roomAuthController.createToken(params.username)
                        call.respond(HttpStatusCode.Created, token)
                    }else {

                        token = roomAuthController.getToken(params.username)
                        call.respond(HttpStatusCode.OK, token)
                    }
                } else
                    call.respond(HttpStatusCode.NotFound, "Пароль неверный")
    }
}