package com.foggyskies.server.routes

import com.foggyskies.server.extendfun.getIpAddress
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.plugin.getLocation
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import java.io.File

fun Route.photoRouting() {

    cRoute("/testtt", HttpMethod.Post, isCheckToken = true) {
        println("Все норм")
//        getIpAddress()

        call.respondText("All Ok", status = HttpStatusCode.OK)
        println("Все норм")

    }

    webSocket("/testii") {
        getIpAddress()

        try {
//                            for (frame in incoming){
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    val rawMessage = frame.readText()
                    val regexCommand = "^nextMessages".toRegex()
                    this.send(rawMessage)

                }
            }
        } catch (e: Exception) {
            println(e)
        }
        println("Я закрываюсь")
    }

    post("/dawd") {

//        checkTokenRule()

        if (call.request.headers["Auth"] == null) {
            println("Тоби пизда")

            finish()
            call.respondText("Токена нету нихуя")
            return@post
        } else {
//        val token = call.request.headers["Auth"]
//        val userDB by inject<MainDBImpl>(MainDBImpl::class.java)
//
//        val isTokenExist = userDB.checkOnExistToken(token.toString())
//
//        if (isTokenExist) {
            println("Все норм")
//        } else {
//            println("Токен не существует")
//            call.respondText("Токен не существует")
//        }
        }

        println("Я зашел сюда")
//        return@avss

//        call.respondText("Daleko", status = HttpStatusCode.OK)
        println("И сюда")

    }
    get("/HEY") {
        call.respond(status = HttpStatusCode.OK, "HIIII" ?: "Error")
    }
    get("/hi") {

        println("Hi Сработал")
//        val ip = getIpAddress()
//
//        val location = ip?.let {
//            it.getLocation()
//        }

        call.respond(status = HttpStatusCode.OK, "hiiiiii Error")
    }

    get("/photo{name}") {
        val name = call.parameters["name"]
        val file = File("photos/$name")
        call.response.header(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, name!!)
                .toString()
        )
        call.respondFile(file)
    }
}