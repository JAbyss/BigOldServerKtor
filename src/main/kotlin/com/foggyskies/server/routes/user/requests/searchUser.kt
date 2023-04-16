package com.foggyskies.server.routes.user.requests

import com.foggyskies.server.data.model.ChatSession
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.searchUsers
import com.foggyskies.server.plugin.SystemRouting
import com.foggyskies.server.plugin.cRoute
import com.foggyskies.server.plugin.cWS
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

//TODO Нужно пересмотреть
//fun Route.searchUser(isCheckToken: Boolean) = cWS(
//    "/searchUser",
//    isCheckToken
//){token ->
//        val user = getUserByToken(token.toString())
//
//        val session = call.sessions.get<ChatSession>()
//        if (session == null) {
//            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
//            return@cWS
//        }
//        try {
//            incoming.consumeEach { frame ->
//                if (frame is Frame.Text) {
//                    if (frame.readText().length >= 3) {
//                        val users = MainDataBase.Users.searchUsers(idUser = user.idUser, frame.readText())
//                        if (users.isNotEmpty()) {
//                            val parsedString = Json.encodeToString(users)
//                            send(parsedString)
//                        } else {
//                            send("[]")
//                        }
//                    } else {
//                        send("[]")
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            println(e)
//        }
//}

fun Route.searchUser(isCheckToken: Boolean) = cRoute(
    SystemRouting.UserRoute.searchUser,
    method = HttpMethod.Post,
    isCheckToken
) { token ->
    val username = call.receiveText()

    val foundedUsers = MainDataBase.Users.searchUsers(idUser = token.idUser, username)
    call.respond(status = HttpStatusCode.OK, foundedUsers)
}