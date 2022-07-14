package com.foggyskies.chat.routes

import com.foggyskies.chat.databases.main.models.LoginUserDC
import com.foggyskies.chat.databases.main.models.RegistrationUserDC
import com.foggyskies.chat.extendfun.generateUUID
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
            call.respondText(status = HttpStatusCode.Conflict, text = "Пользователь с таким логином уже существует.")
        else {
            if (isEmailExist) {
                call.respondText(status = HttpStatusCode.Conflict, text = "Пользователь с таким e_mail уже существует.")
            } else {
                val codeConfirmation = generateUUID(4)

                val user = RegistrationUserDC(
                    username = params.username,
                    password = params.password,
                    e_mail = params.e_mail,
                )
                routController.createUser(user)
                call.respondText(status = HttpStatusCode.Created, text = "Регистрация прошла успешно.")
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
                    call.respondText(token, status = HttpStatusCode.Created)
                } else {
                    val tokenDC = routController.getToken(params.username)
                    token = "${tokenDC.id}|${tokenDC.idUser}"
                    println("Старый токен получен")
                    call.respondText(token, status = HttpStatusCode.OK)
                }
            } else
                call.respondText("Пароль неверный", status = HttpStatusCode.NotFound)
        } else {
            call.respondText("Пользователь не найден", status = HttpStatusCode.NotFound)
        }
    }
}