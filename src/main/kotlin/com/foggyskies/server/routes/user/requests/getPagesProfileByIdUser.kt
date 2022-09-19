package com.foggyskies.server.routes.user.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.content.ContentDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getPageById
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getUserByIdUser
import com.foggyskies.server.databases.mongo.codes.testpacage.subscribe.SubscribeDataBase
import com.foggyskies.server.databases.mongo.getCollection
import com.foggyskies.server.databases.mongo.main.models.PageProfileFormattedDC
import com.foggyskies.server.databases.mongo.subscribers.models.SubscribersDC
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.routes.checks
import com.foggyskies.server.routes.user.requests.settings.SettingRequests
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getPagesProfileByIdUser(
    isCheckToken: Boolean = SettingRequests.isCheckToken
) = cRoute(
    SystemRouting.UserRoute.getPagesProfileByIdUser,
    method = HttpMethod.Get,
    isCheckToken = isCheckToken
) { token ->

    lateinit var idOtherUser: String

    checks {
        idOtherUser = call.parameters["idUser"] ?: return@cRoute call.respondText(
            status = HttpStatusCode.BadRequest,
            text = "IdUser не получен."
        )
    }

    val listPages = getAllPagesByIdUser(idOtherUser)
    call.respond(HttpStatusCode.OK, listPages)
}

suspend fun getAllPagesByIdUser(idUser: String): List<PageProfileFormattedDC> {
    val listIdPages = MainDataBase.Users.getUserByIdUser(idUser).pages_profile
    return getAllPagesByList(listIdPages)
}

private suspend fun getAllPagesByList(listIds: List<String>): List<PageProfileFormattedDC> {
    val listAllPages = listIds.mapNotNull { id ->
        MainDataBase.PagesProfile.getPageById(id)?.let {
                val countSubscribers =
                    SubscribeDataBase.db.getCollection<SubscribersDC>("subscribers_${it.id}").countDocuments()
                        .toString()
                val countContent = (ContentDataBase.Content.getCollection(it.id).countDocuments() - 1).toString()

                it.withCountSubsAndContents(countSubscribers, countContent)
            }
    }
    return listAllPages
}