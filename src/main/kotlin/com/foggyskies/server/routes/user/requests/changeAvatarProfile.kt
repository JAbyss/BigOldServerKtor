package com.foggyskies.server.routes.user.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.changeAvatarByIdPage
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getAvatarPageProfile
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.routes.user.requests.settings.SettingRequests
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.changeAvatarProfile(
    isCheckToken: Boolean = SettingRequests.isCheckToken
) = cRoute(
    SystemRouting.UserRoute.changeAvatarProfile,
    method = HttpMethod.Post,
    isCheckToken = isCheckToken
) { token ->

    val idPage = call.request.headers["idPage"] ?: return@cRoute call.respond(HttpStatusCode.BadRequest)

    val pathToFile = call.receiveText()

//    val avatarOld = MainDataBase.PagesProfile.getAvatarPageProfile(idPage)

//    checkOldAvatar(avatarOld)

    MainDataBase.PagesProfile.changeAvatarByIdPage(idPage, pathToFile)
    call.respond(HttpStatusCode.OK, pathToFile)

//    val pathString =
//    val path = Paths.get(pathString)
//    val file = File(pathString)
//    if (!Files.exists(path)) {
//        file.mkdirs()
//    }
//    val decodedString = Base64.getDecoder().decode(image)
//    val name = ObjectId().toString()
//    val readyString = "$pathString/image_${name}.jpg"
//    File(readyString).writeBytes(decodedString)
//    val avatar = routController.changeAvatarByIdPage(idPage, readyString)
}

private const val pathString = "${SystemRouting.Images.BASE_DIR}/${SystemRouting.Images.profiles_avatars}"