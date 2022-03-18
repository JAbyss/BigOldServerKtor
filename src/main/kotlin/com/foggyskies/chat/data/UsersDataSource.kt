package com.foggyskies.chat.data

import com.foggyskies.chat.routes.UserMainEntity
import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch

interface UsersDataSource {

    suspend fun checkOnExistToken(token: String): Boolean

    suspend fun getUsers(): List<UsersSearch>

    suspend fun getUsersByUsername(username: String): List<UsersSearch>

    suspend fun getChats(token: String): List<FormattedChatDC>

    suspend fun addRequestToFriend(userSender: UserNameID, idUserReceiver: String)

    suspend fun getUserByToken(token: String): UserMainEntity

    suspend fun acceptRequestFriend(userReceiver: UserNameID, userSender: UserNameID)
}