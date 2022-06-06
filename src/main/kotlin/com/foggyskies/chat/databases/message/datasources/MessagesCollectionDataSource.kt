package com.foggyskies.chat.databases.message.datasources

import com.foggyskies.ServerDate
import com.foggyskies.chat.databases.message.models.ChatMessageCollection
import kotlinx.coroutines.delay

interface MessagesCollectionDataSource {

    suspend fun insertOne(idChat: String, message: ChatMessageCollection)

    suspend fun getAllMessages(idChat: String): List<ChatMessageCollection>

    suspend fun getFiftyMessage(idChat: String): List<ChatMessageCollection>

    suspend fun getNextMessages(idChat: String, lastMessageId: String): List<ChatMessageCollection>

    suspend fun getLastMessage(idChat: String): String

    suspend fun createCollection(idChat: String)

    suspend fun deleteMessage(idChat: String, idMessage: String): Int
}