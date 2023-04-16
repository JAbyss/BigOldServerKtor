package com.foggyskies.server.routes

import io.ktor.server.routing.*

private val listNotify = mutableListOf<String>()

fun Route.notificationRoutes() {
//    val routeController by inject<NotifyRoutController>()
    //TODO Починить переделать
//    fun createNotifySession(token: String) {
//        if (!listNotify.contains(token)) {
//            listNotify.add(token)
//            webSocket("/notify/$token") {
////            val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
//
//                val session = call.sessions.get<ChatSession>()
//                if (session == null) {
//                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
//                    listNotify.remove(token)
//                    return@webSocket
//                }
//
//                val user = routeController.getUserByToken(token) ?: return@webSocket
////        val notification = routeController.getNotification("63cf0a3e88c4a08c2842c08")
//
//                async {
//                    routeController.watchForNotification(user.idUser, this@webSocket)
//                }
//
////        val json = Json.encodeToString(notification)
////
////        send(json)
//
//                incoming.consumeEach { frame ->
//                    if (frame is Frame.Text) {
//                        val incomingData = frame.readText()
//                    }
//                }
//            }
//        }
//    }
//    get("/createNotificationSession{token}") {
//        val token =
//            call.parameters["token"] ?: call.respond(HttpStatusCode.BadRequest, "Параметр token не получен.")
//        if (token.toString().isNotEmpty()) {
//            createNotifySession(token.toString())
////                val messages = roomController.getFiftyMessage(idChat.toString())
//            call.respond(HttpStatusCode.Created)
//        }
//    }
}