package com.foggyskies.server.databases.mongo.testpacage.main.collections

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.getCollection
import com.foggyskies.server.databases.mongo.main.models.FriendDC
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

suspend fun MainDataBase.Friends.getFriendsDocumentFriendByIdUser(idUser: String): FriendDC? {
    return getCollection().findOne(FriendDC::idUser eq idUser)
}

suspend fun MainDataBase.Friends.getFriendsByIdUser(idUser: String): List<String> {
    return getCollection().findOne(FriendDC::idUser eq idUser)?.friends ?: emptyList()
}

suspend fun MainDataBase.Friends.createFriendsDocument(idUser: String, firstFriend: String) {
    val document = FriendDC(idUser = idUser, friends = listOf(firstFriend))
    getCollection().insertOne(document)
}

suspend fun MainDataBase.Friends.addFriendByIdUser(idUser: String, newFriend: String) {
    getCollection()
        .updateOne(FriendDC::idUser eq idUser, addToSet(FriendDC::friends, newFriend))
}

suspend fun MainDataBase.Friends.insertFriendByIdUser(document: FriendDC) {
    getCollection().insertOne(document)
}

suspend fun MainDataBase.Friends.delFriendByIdUser(idUser: String, delFriend: String) {
    getCollection()
        .updateOne(FriendDC::idUser eq idUser, pull(FriendDC::friends, delFriend))
}

suspend fun MainDataBase.Friends.watchForFriend(idUser: String, socket: DefaultWebSocketServerSession) {
    val requestsToFriend = getCollection().watch<FriendDC>()

    requestsToFriend.consumeEach { item ->
        if ((item.documentKey["_id"]?.asString())?.value.equals(idUser)) {
            if (item.updateDescription != null) {
                val updatedFields =
                    Json.decodeFromString<List<UserNameID>>(item.updateDescription.updatedFields["friends"]?.asArray()?.values.toString())
                println("FRIENDS 1${updatedFields.toString()}")

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
                println("FRIENDS 2${formattedList.toString()}")
                socket.send(Frame.Text("getFriends|${formattedList.json}"))
            } else {
                val json = Json.decodeFromString<List<UserNameID>>(item.fullDocument.friends.json)

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

                println("FRIENDS 3${json.toString()}")
                socket.send(Frame.Text("getFriends|${formattedList.json}"))
            }
        }
    }
}