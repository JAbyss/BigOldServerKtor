package com.foggyskies.chat.databases.message.datasources

import com.jetbrains.handson.chat.server.chat.data.model.ChatMessage

interface MessagesCollectionDataSource {

    suspend fun insertOne(idChat: String, message: ChatMessage)

    suspend fun getAllMessages(idChat: String) : List<ChatMessage>

    suspend fun getFiftyMessage(idChat: String) : List<ChatMessage>

    suspend fun getLastMessage(idChat: String): String
}