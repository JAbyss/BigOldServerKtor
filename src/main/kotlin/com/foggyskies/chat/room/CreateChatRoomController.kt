package com.foggyskies.chat.room

import com.foggyskies.chat.data.CreateChatDataSource
import com.foggyskies.chat.data.TokenDataSource
import com.foggyskies.chat.data.model.UserMainEntity
import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch

class CreateChatRoomController(
    private val createChatDataSource: CreateChatDataSource,
    private val tokenDataSource: TokenDataSource
) {

    suspend fun checkOnExistChat(idUserFirst: String, idUserSecond: String): String {
        return createChatDataSource.checkOnExistChatByIdUsers(idUserFirst, idUserSecond)
    }

    suspend fun createChat(username: String, idUserFirst: String, idUserSecond: String): String{
        val idChat = createChatDataSource.createChat(username, idUserFirst, idUserSecond)
        createChatDataSource.createMessages(idChat)
        return idChat
    }

    suspend fun getChatId(idUser: String): String {
        return createChatDataSource.getChatId(idUser)
    }

    suspend fun checkOnExistToken(token: String): Boolean {
        return tokenDataSource.checkOnExistToken(token)
    }

    suspend fun getUserByUsername(username: String): UsersSearch{
        return createChatDataSource.getUserByUsername(username)
    }

}