package com.foggyskies.chat.data

import com.jetbrains.handson.chat.server.chat.data.model.ChatMessage

interface ChatSessionDataSource {

    suspend fun getLastFiftyMessages(idChat: String): List<ChatMessage>

}