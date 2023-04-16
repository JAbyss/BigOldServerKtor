//package com.foggyskies.server.routes.user.requests
//
//import com.foggyskies.server.data.model.ChatSession
//import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
//import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getAvatarByIdUser
//import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getFriendsByIdUser
//import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getTokenByToken
//import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getUserByIdUser
//import com.foggyskies.server.databases.mongo.main.models.FriendListDC
//import com.foggyskies.server.databases.mongo.main.models.UserNameID
//import com.foggyskies.server.extendfun.generateUUID
//import com.foggyskies.server.plugin.Action
//import com.foggyskies.server.plugin.SusAction
//import com.foggyskies.server.routes.user.UserRoutController
//import com.foggyskies.server.routes.user.requests.settings.SettingRequests
//import com.foggyskies.server.plugin.SystemRouting
//import com.foggyskies.server.plugin.cRoute
//import com.sun.tools.javac.Main
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//import io.ktor.server.sessions.*
//import io.ktor.server.websocket.*
//import io.ktor.websocket.*
//import kotlinx.coroutines.async
//import kotlinx.coroutines.channels.consumeEach
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//
//val createdSockets = mutableListOf<String>()
//
//fun Route.createMainSocket(
//    routController: UserRoutController,
//    isCheckToken: Boolean = SettingRequests.isCheckToken
//): Route {
//
//
//    fun _createMainSocket(idUser: String) {
//        if (!createdSockets.contains(idUser)) {
//            webSocket("/mainSocket/$idUser") {
//
//                val session = call.sessions.get<ChatSession>()
//                if (session == null) {
//                    routController.setStatusUser(idUser, "Не в сети")
//                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
////                    sessionsMainSockets.remove(idUser)
//                    return@webSocket
//                }
//                val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
//                val isTokenExist = routController.checkOnExistToken(token.toString())
//
//                val map_actions = mapOf(
////                    "getFriends" to suspend { "getFriends|${Json.encodeToString(routController.getFriends(token.toString()))}" },
////                    "getChats" to suspend { "getFriends|${Json.encodeToString(routController.getChats(token.toString()))}" },
////                    "getRequestsFriends" to suspend {
////                        "getRequestsFriends|${
////                            Json.encodeToString(
////                                routController.getRequestsFriends(
////                                    token.toString()
////                                )
////                            )
////                        }"
////                    },
////                    "getChats" to suspend {
////                        "getChats|${Json.encodeToString(routController.getChats(token.toString()))}"
////                    },
////                    "getPagesProfile" to suspend {
////                        "getPagesProfile|${
////                            Json.encodeToString(
////                                routController.getAllPagesByIdUser(
////                                    idUser
////                                )
////                            )
////                        }"
////                    },
////                    "getNewMessages" to suspend {
////                        "getNewMessages|${Json.encodeToString(routController.getAllNewMessages(idUser))}"
////                    },
//                )
//                val map_action_unit = mapOf(
//                    "logOut" to suspend { routController.logOut(token.toString()) },
//                    "deleteAllSentNotifications" to suspend { routController.deleteAllSentNotifications(idUser) }
////            "acceptRequestFriend" to suspend { roomUserController.acceptRequestFriend() }
//                )
//
//                if (isTokenExist) {
//                    routController.setStatusUser(idUser, "В сети")
//                    val user = routController.getUserByToken(token.toString())
//
////                    try {
//                    val watcherFriends = async {
//                        routController.watchForFriend(idUser, this@webSocket)
//                    }
//                    val watcherRequestsFriends = async {
//                        routController.watchForRequestsFriends(idUser, this@webSocket)
//                    }
//                    //fixme На время выключено
////                    val watcherInternalNotifications = async {
////                        routController.watchForInternalNotifications(idUser, this@webSocket)
////                    }
//                    val watcherNewMessages = async {
//                        routController.watchForNewMessages(idUser, this@webSocket)
//                    }
//
//
//                    incoming.consumeEach { frame ->
//                        if (frame is Frame.Text) {
//                            val incomingData = frame.readText()
//                            val regex = "\\w+(?=\\|)".toRegex()
//                            val action = regex.find(incomingData)?.value
//
//                            if (map_actions.containsKey(action)) {
//                                val friends = map_actions[action]?.invoke()
//                                if (friends != null) {
//                                    send(friends)
//                                }
//                            } else if (map_action_unit.containsKey(action)) {
//                                map_action_unit[action]?.invoke()
//                            } else if (action == "acceptRequestFriend") {
//                                val idUserReceiver = incomingData.replace("$action|", "")
//                                val userSender = UserNameID(
//                                    id = user.idUser,
//                                    username = user.username
//                                )
//                                routController.acceptRequestFriend(userSender, idUserReceiver)
//                            } else if (action == "addFriend") {
//                                val idReceiver = incomingData.replace("$action|", "")
//                                routController.addRequestToFriend(
//                                    UserNameID(
//                                        id = user.idUser,
//                                        username = user.username
//                                    ), idReceiver
//                                )
//                            }
//                            /**
//                             * loadFile|{Путь к файлу}|{Название файла}
//                             */
//                            else if (action == "loadFile") {
//                                val data = "(?<=\\|).+(?=\\|)".toRegex().find(incomingData)?.value!!.split("|")
//                                val pathWithFile = data[0]
//                                val nameOperation = data[1] + "-" + generateUUID(7)
//                                routController.loadFile(pathWithFile, nameOperation, this)
//                            }
//                        }
//                    }
////                    } catch (e: Exception) {
////                        println(e)
////                    } finally {
//                    routController.setStatusUser(idUser, "Не в сети")
//                    watcherFriends.cancel()
//                    watcherRequestsFriends.cancel()
//                    watcherNewMessages.cancel()
////                    watcherInternalNotifications.cancel()
//                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Неверный токен"))
////                    sessionsMainSockets.remove(idUser)
//                    return@webSocket
////                    }
//                } else {
//                    routController.setStatusUser(idUser, "Не в сети")
//                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Неверный токен"))
////                    sessionsMainSockets.remove(idUser)
//                    return@webSocket
//                }
//            }
//            createdSockets.add(idUser)
//        }
//    }
//
//    return cRoute(
//        SystemRouting.UserRoute.createMainSocket,
//        method = HttpMethod.Post,
//        isCheckToken = isCheckToken
//    ) { token ->
//
//        val user = getUserByToken(token)
//
//        _createMainSocket(user.idUser)
//        call.respondText(user.idUser, status = HttpStatusCode.OK)
//    }
//}
//
//
//fun Any.toJsonString(): String {
//    return Json.encodeToString(this)
//}
