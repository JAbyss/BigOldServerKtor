package com.foggyskies.server.routes.user.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.changeAvatarByUserId
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getAvatarByIdUser
import com.foggyskies.server.routes.user.requests.settings.SettingRequests
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

fun Route.changeAvatar(
    isCheckToken: Boolean = SettingRequests.isCheckToken
) = cRoute(
    SystemRouting.UserRoute.changeAvatar,
    method = HttpMethod.Post,
    isCheckToken = isCheckToken
) { token ->

    val pathToImage = call.receiveText()

    val idUser = token.idUser

//    checkOldAvatar(idUser)

    changeAvatar(idUser, pathToImage).let { avatar ->
        call.respondText(status = HttpStatusCode.OK, text = avatar)
    }
}

suspend inline fun checkOldAvatar(idUser: String) {
    val avatar = MainDataBase.Avatars.getAvatarByIdUser(idUser)
    if (avatar.isNotEmpty()) File(avatar).delete()
}

fun getImageBytes(base64: String): ByteArray {
    return Base64.getDecoder().decode(base64)
}

private const val pathString = "${SystemRouting.Images.BASE_DIR}/${SystemRouting.Images.AVATARS}"

suspend fun createFile(base64: String, path: String, action: suspend (String) -> Unit) {

    checkOnExistFolder(path)

    val name = ObjectId().toString()
    val readyString = "$path/avatar_${name}.jpg"
    File(readyString).writeBytes(getImageBytes(base64))
    action(readyString)
}

private suspend inline fun changeAvatar(idUser: String, path: String): String {
    return MainDataBase.Avatars.changeAvatarByUserId(idUser, path)
}

fun checkOnExistFolder(path: String) {
    val pathLink = Paths.get(path)
    val file = File(path)
    if (!Files.exists(pathLink)) file.mkdirs()
}