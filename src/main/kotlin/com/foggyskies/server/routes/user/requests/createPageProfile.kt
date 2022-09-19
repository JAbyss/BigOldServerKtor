package com.foggyskies.server.routes.user.requests

import SystemDoc
import com.foggyskies.ServerDate
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.content.ContentDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.addOnePage
import com.foggyskies.server.databases.mongo.codes.testpacage.subscribe.SubscribeDataBase
import com.foggyskies.server.databases.mongo.main.models.PageProfileDC
import com.foggyskies.server.databases.mongo.main.models.UserMainEntity
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.routes.user.requests.settings.SettingRequests
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId
import org.litote.kmongo.addToSet
import org.litote.kmongo.eq

fun Route.createPageProfile(isCheckToken: Boolean = SettingRequests.isCheckToken) = cRoute(
    "/createPageProfile",
    method = HttpMethod.Post,
    isCheckToken = isCheckToken
) { token ->

    val page = call.receive<PageProfileDC>()

//    createFile(page.image, path = "${SystemRouting.Images.BASE_DIR}/${SystemRouting.Images.profiles_avatars}"){path->

    val idPage = ObjectId().toString()

    addOnePage(
        token.idUser,
        page.copy(id = idPage, image = page.image)
    )

    call.respondText(status = HttpStatusCode.OK, text = idPage)
//    }

//    val pathString = "${SystemRouting.Images.BASE_DIR}/${SystemRouting.Images.profiles_avatars}"
//    val path = Paths.get(pathString)
//    val decodedString = Base64.getDecoder().decode(page.image)
//    val file = File(pathString)
//    if (!Files.exists(path)) {
//        file.mkdirs()
//    }
//    val readyPath = "$pathString/image_$objectId.jpg"
//    File(readyPath).writeBytes(decodedString)
}

suspend fun addOnePage(idUser: String, item: PageProfileDC) {
    MainDataBase.PagesProfile.addOnePage(item)
    SubscribeDataBase.db.createCollection("${SubscribeDataBase.NamesCollections.subscribers}${item.id}")
    ContentDataBase.db.createCollection("${ContentDataBase.NameCollection.content}${item.id}")

    val systemDoc = SystemDoc(
        date_create = ServerDate.fullDate,
        owner_id = idUser
    )
    ContentDataBase.db.getCollection<SystemDoc>("content_${item.id}").insertOne(systemDoc)
    MainDataBase.db.getCollection<UserMainEntity>("users")
        .findOneAndUpdate(UserMainEntity::idUser eq idUser, addToSet(UserMainEntity::pages_profile, item.id))
}