package com.foggyskies.server.routes.cloud

import com.foggyskies.server.databases.message.models.FileDC
import com.foggyskies.server.extendfun.getSizeFile
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.fileTree(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.CloudRoute.fileTree,
    method = HttpMethod.Get,
    isCheckToken
){token ->

    val allFiles = File("cloud").listFiles()?.toList()?.map { file ->
        FileDC(
            name = file.nameWithoutExtension,
            size = getSizeFile(file.length()),
            type = file.extension.let { return@let if (it == "") "dir" else it },
            path = file.path
        )
    }

    call.respond(HttpStatusCode.OK, allFiles ?: emptyList())
}