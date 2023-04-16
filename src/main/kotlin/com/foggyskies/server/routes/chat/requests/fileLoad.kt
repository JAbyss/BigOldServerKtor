package com.foggyskies.server.routes.chat.requests

import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.routes.chat.BodyFile
import com.foggyskies.server.routes.user.requests.checkOnExistFolder
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

val fileInLoads = ConcurrentHashMap<String, String>()
val synchronizedList = Collections.synchronizedList(mutableListOf<String>())

enum class TypeLoadFile {
    CHAT, PROFILE, AVATAR, CONTENT_PROFILE
}

fun Route.fileLoad(isCheckToken: Boolean) = cRoute(
    path = "/fileLoad",
    method = HttpMethod.Post,
    isCheckToken = isCheckToken
) {

    val body = call.receive<BodyFile>()

    try {
        if (!synchronizedList.contains(body.idUpload)){
            synchronizedList.add(body.idUpload)
        }

        val id = body.infoData

        val path = when (body.typeLoad) {
            TypeLoadFile.CHAT -> {
                "images/chats/$id"
            }
            TypeLoadFile.PROFILE -> {
                "images/profiles_avatar/$id"
            }
            TypeLoadFile.AVATAR -> {
                "images/user_avatar/$id"
            }
            TypeLoadFile.CONTENT_PROFILE -> {
                "images/profiles_content/$id"
            }
        }

        checkOnExistFolder(path)

        val fullPath = "$path/${body.idUpload}.${body.typeFile}"

        val file = File(fullPath)

        val decodedString = Base64.getDecoder().decode(body.contentFile)
        file.appendBytes(decodedString)

        if (body.status == "finish") {
            synchronizedList.remove(body.idUpload)
            return@cRoute call.respond(status = HttpStatusCode.Created, fullPath)
        }
    } catch (e: Exception) {
        println(e)
    }
    call.respond(HttpStatusCode.OK)
}