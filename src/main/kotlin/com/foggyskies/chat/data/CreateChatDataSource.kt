package com.foggyskies.chat.data

interface CreateChatDataSource {

    suspend fun checkOnExistChat(idUser: String) : Boolean

    suspend fun createChat(username: String, idUserFirst: String, idUserSecond: String) : String

    suspend fun getChatId(idUser: String): String

    suspend fun createMessages(idChat: String)

}