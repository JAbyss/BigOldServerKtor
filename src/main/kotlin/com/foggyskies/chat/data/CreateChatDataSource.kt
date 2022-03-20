package com.foggyskies.chat.data

import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch

interface CreateChatDataSource {

    suspend fun checkOnExistChatByIdUsers(idUserFirst: String, idUserSecond: String): String

    suspend fun checkOnExistChat(idUser: String) : Boolean

    suspend fun createChat(username: String, idUserFirst: String, idUserSecond: String) : String

    suspend fun getChatId(idUser: String): String

    suspend fun createMessages(idChat: String)

    suspend fun  getUserByUsername(username: String): UsersSearch
}