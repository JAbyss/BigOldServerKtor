package com.foggyskies.chat.data

import com.foggyskies.chat.data.model.FriendListDC
import com.foggyskies.chat.data.model.UserMainEntity
import com.foggyskies.chat.data.model.UserNameID
import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch
import io.ktor.websocket.*

interface UsersDataSource {

    suspend fun checkOnExistToken(token: String): Boolean

    suspend fun getUsers(): List<UsersSearch>

    suspend fun getUsersByUsername(username: String): List<UsersSearch>

    suspend fun getChats(token: String): List<FormattedChatDC>

    suspend fun addRequestToFriend(userSender: UserNameID, idUserReceiver: String)

    suspend fun getUserByToken(token: String): UserMainEntity

    suspend fun acceptRequestFriend(userReceiver: UserNameID, userSender: UserNameID)

    suspend fun getFriends(token: String): List<FriendListDC>

    suspend fun getUserByUsername(username: String): UsersSearch

    suspend fun getRequestsFriends(token: String): List<UserNameID>?

    suspend fun watchForRequestsFriends(idUser: String, socket: DefaultWebSocketServerSession)

    suspend fun watchForFriend(idUser: String, socket: DefaultWebSocketServerSession)
}