package com.foggyskies.server.databases.mongo.codes.testpacage.main.collections

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.getCollection
import com.foggyskies.server.databases.mongo.main.models.RequestFriendDC
import com.foggyskies.server.databases.mongo.main.models.UserIUSI
import com.foggyskies.server.databases.mongo.main.models.UserNameID
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.litote.kmongo.addToSet
import org.litote.kmongo.eq
import org.litote.kmongo.json
import org.litote.kmongo.pull

suspend fun MainDataBase.Requests.createRequestsFriendsByIdUser(idUser: String, firstRequest: String) {
    val request = RequestFriendDC(id = idUser, requests = listOf(firstRequest))
    getCollection().insertOne(request)
}

suspend fun MainDataBase.Requests.getRequestsDocumentFriendByIdUser(idUser: String): RequestFriendDC? {
    return getCollection().findOne(RequestFriendDC::id eq idUser)
}

suspend fun MainDataBase.Requests.getRequestsFriendByIdUser(idUser: String): List<String> {
    return getCollection().findOne(RequestFriendDC::id eq idUser)?.requests
        ?: emptyList()
}

suspend fun MainDataBase.Requests.addRequestFriendsByIdUser(idUser: String, newRequest: String) {
    val requests = getRequestsDocumentFriendByIdUser(idUser)
    if (requests != null)
        getCollection()
            .updateOne(RequestFriendDC::id eq idUser, addToSet(RequestFriendDC::requests, newRequest))
    else
        createRequestsFriendsByIdUser(idUser, newRequest)
}

suspend fun MainDataBase.Requests.delRequestFriendsByIdUser(idUser: String, delRequest: String) {
    getCollection()
        .updateOne(RequestFriendDC::id eq idUser, pull(RequestFriendDC::requests, delRequest))
}

suspend fun MainDataBase.Requests.watchForRequestsFriends(idUser: String, socket: DefaultWebSocketServerSession) {
    val requestsToFriend = getCollection().watch<RequestFriendDC>()

    requestsToFriend.consumeEach { item ->
        if ((item.documentKey["_id"]?.asString())?.value.equals(idUser)) {
            if (item.updateDescription != null) {
                val updatedFields =
                    Json.decodeFromString<List<UserNameID>>(item.updateDescription.updatedFields["requests"]?.asArray()?.values.toString())

                println("REQUEST 1 $updatedFields")

                val formattedList = mutableListOf<UserIUSI>()
                updatedFields.forEach { userNameID ->
                    val user = MainDataBase.Users.getUserByIdUser(userNameID.id)
                    formattedList.add(
                        UserIUSI(
                            id = user.idUser,
                            username = userNameID.username,
                            status = user.status,
                            image = "user.image"
                        )
                    )
                }
                println("REQUEST 2 $formattedList")

                socket.send(Frame.Text("getRequestsFriends|${formattedList.json}"))
            } else {
                val json = Json.decodeFromString<List<UserNameID>>(item.fullDocument.requests.json)

                val formattedList = mutableListOf<UserIUSI>()
                json.forEach { userNameID ->
                    val user = MainDataBase.Users.getUserByIdUser(userNameID.id)
                    formattedList.add(
                        UserIUSI(
                            id = user.idUser,
                            username = userNameID.username,
                            status = user.status,
                            image = "user.image"
                        )
                    )
                }
                println("REQUEST 3 $formattedList")
                socket.send(Frame.Text("getRequestsFriends|${formattedList.json}"))
            }
        }
    }
}