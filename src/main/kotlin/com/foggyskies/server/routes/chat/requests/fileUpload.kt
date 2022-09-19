package com.foggyskies.server.routes.chat.requests

import com.foggyskies.server.extendfun.generateUUID
import com.foggyskies.server.plugin.TaskManager
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.plugin.m
import com.foggyskies.server.routes.user.requests.checkOnExistFolder
import com.foggyskies.server.routes.user.requests.getImageBytes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import org.bson.types.ObjectId
import java.io.File
import java.util.concurrent.ConcurrentHashMap

//FIXME Надо разобраться и доделать

fun Route.fileUpload(isCheckToken: Boolean) = cRoute(
    path = "/fileUpload",
    method = HttpMethod.Post,
    isCheckToken
) { token ->

    val nameOperation = call.request.headers["nameOperation"] ?: return@cRoute call.respond(
        HttpStatusCode.BadRequest,
        "Название операции отсутствует"
    )
    val statusOperation = call.request.headers["statusOperation"] ?: return@cRoute call.respond(
        HttpStatusCode.BadRequest,
        "Статус операции отсутствует"
    )

    val data = call.receiveText()

    maps[nameOperation]?.let {
        val folder = "images/chats/36436"

        checkOnExistFolder(folder)

        File(it.path).writeBytes(getImageBytes(data))

        if (statusOperation == "finish") {
            maps.remove(nameOperation)
            TaskManager.cancelTask(nameOperation)
            call.respond(HttpStatusCode.OK)
        } else
            call.respond(HttpStatusCode.Accepted)

    } ?: call.respond(HttpStatusCode.NotFound)
}

fun Route.createUpload() = cRoute(
    path = "/createUpload",
    method = HttpMethod.Post,
    isCheckToken = true
) { token ->
    val nameOperation = generateUUID(10)
    val extensionFile = call.request.headers["extensionFile"]!!

    val folder = "images/chats/36436"

    checkOnExistFolder(folder)
    val name = ObjectId().toString() + generateUUID(4)
    val readyString = "$folder/${name}.$extensionFile"

    withContext(Dispatchers.IO) {
        File(readyString).createNewFile()
    }

    maps[nameOperation] = uploadingFileDC(
        path = readyString,
        ""
    )
    TaskManager.addTask(TaskManager.Task(
        nameOperation,
        15.m,
        {},
        {
            maps[nameOperation]?.path?.let {
                File(it).delete()
            }
            maps.remove(nameOperation)
        }
    ))

    call.respond(HttpStatusCode.Created, nameOperation)
}

data class uploadingFileDC(
    val path: String,
    val lastActivity: String
)

val maps = ConcurrentHashMap<String, uploadingFileDC>()