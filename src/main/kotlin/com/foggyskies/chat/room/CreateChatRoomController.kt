package com.foggyskies.chat.room

import com.foggyskies.chat.data.CreateChatDataSource
import com.foggyskies.chat.data.TokenDataSource

class CreateChatRoomController(
    private val createChatDataSource: CreateChatDataSource,
    private val tokenDataSource: TokenDataSource
) {

    suspend fun checkOnExistChat(idUser: String): Boolean {
        return createChatDataSource.checkOnExistChat(idUser)
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

}