package com.foggyskies.chat.databases.main.datasources

import com.foggyskies.chat.data.model.UserNameID
import io.ktor.websocket.*

interface RequestsFriendsCollectionDataSource {

    suspend fun createRequestsFriendsByIdUser(idUser: String, firstRequest: UserNameID)

    suspend fun getRequestsFriendByIdUser(idUser: String): List<UserNameID>

    suspend fun addRequestFriendsByIdUser(idUser: String, newRequest: UserNameID)

    suspend fun delRequestFriendsByIdUser(idUser: String, delRequest: UserNameID)

    suspend fun watchForRequestsFriends(idUser: String, socket: DefaultWebSocketServerSession)
}