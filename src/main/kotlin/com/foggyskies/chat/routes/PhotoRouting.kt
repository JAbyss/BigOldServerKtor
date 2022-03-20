package com.foggyskies.chat.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.File

fun Route.photoRouting() {

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