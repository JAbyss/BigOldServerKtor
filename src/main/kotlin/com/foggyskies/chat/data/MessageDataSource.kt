package com.foggyskies.chat.data;

import com.jetbrains.handson.chat.server.chat.data.model.ChatMessage

interface MessageDataSource {

    suspend fun insertOne(idChat: String, message: ChatMessage)

    suspend fun getAllMessage(idChat: String) : List<ChatMessage>

    suspend fun getFiftyMessage(idChat: String) : List<ChatMessage>

}
