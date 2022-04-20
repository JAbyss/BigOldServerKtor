package com.foggyskies.chat.routes

import com.foggyskies.chat.data.model.LoginUserDC
import com.foggyskies.chat.data.model.RegistrationUserDC
import com.foggyskies.chat.newroom.AuthRoutController
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val routController by inject<AuthRoutController>()
    post("/registration") {
        val params = call.receive<RegistrationUserDC>()

        val isUserExist = routController.checkOnExistUser(username = params.username)
        val isEmailExist = routController.checkOnExistEmail(e_mail = params.e_mail)


        if (isUserExist)
            call.respond(status = HttpStatusCode.Conflict, message = "Пользователь с таким логином уже существует.")
        else {
            if (isEmailExist) {
                call.respond(status = HttpStatusCode.Conflict, message = "Пользователь с таким e_mail уже существует.")
            } else {
                val user = RegistrationUserDC(
                    username = params.username,
                    password = params.password,
                    e_mail = params.e_mail,
                )
                routController.creteUser(user)
                call.respond(status = HttpStatusCode.Created, message = "Регистрация прошла успешно.")
            }
        }
    }
    post("/auth") {
        val params = call.receive<LoginUserDC>()

        val isUserExist = routController.checkOnExistUser(params.username)

        val isCorrectPassword = routController.checkPasswordOnCorrect(params.username, params.password)


        if (isUserExist) {
            val user = routController.getUserByUsername(params.username)
            if (isCorrectPassword) {
                var token = ""
                val isTokenExist = routController.checkOnExistTokenByUsername(params.username)
                if (!isTokenExist) {
                    val tokenDC = routController.createToken(user.toUserNameID())
                    token = "${tokenDC.id}|${tokenDC.idUser}"
                    println("Новый токен создан")
                    call.respond(HttpStatusCode.Created, token)
                } else {
                    val tokenDC = routController.createToken(user.toUserNameID())
                    token = "${tokenDC.id}|${tokenDC.idUser}"
                    println("Старый токен получен")
                    call.respond(HttpStatusCode.OK, token)
                }
            } else
                call.respond(HttpStatusCode.NotFound, "Пароль неверный")
        } else {
            call.respond(HttpStatusCode.NotFound, "Пользователь не найден")
        }
    }
}