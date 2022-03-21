package com.foggyskies.chat.datanew

import com.foggyskies.chat.data.model.UserNameID
import io.ktor.websocket.*

interface FriendsCollectionDataSource {

    suspend fun getFriendsByIdUser(idUser: String): List<UserNameID>

    suspend fun createFriendsDocument(idUser: String, firstFriend: UserNameID)

    suspend fun addFriendByIdUser(idUser: String, newFriend: UserNameID)

    suspend fun delFriendByIdUser(idUser: String, delFriend: UserNameID)

    suspend fun watchForFriend(idUser: String, socket: DefaultWebSocketServerSession)
}