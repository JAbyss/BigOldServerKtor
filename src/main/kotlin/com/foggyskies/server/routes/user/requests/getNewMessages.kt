package com.foggyskies.server.routes.user.requests

import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.NewMessagesDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.collections.getAllNewMessages
import com.foggyskies.server.databases.mongo.newmessage.models.NewMessagesCollection
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getNewMessages(isCheckToken: Boolean) = cRoute(
    path = SystemRouting.UserRoute.getNewMessages,
    method = HttpMethod.Get,
    isCheckToken = isCheckToken
) { token ->

    getUserByIdUser(token.idUser).let {
        val newMessages = NewMessagesDataBase.NewMessages.getAllNewMessages(it.idUser)
        call.respond(status = HttpStatusCode.OK, message = newMessages)
    }
}