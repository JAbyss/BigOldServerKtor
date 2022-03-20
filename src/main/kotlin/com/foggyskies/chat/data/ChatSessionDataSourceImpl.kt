package com.foggyskies.chat.data

import com.jetbrains.handson.chat.server.chat.data.model.ChatMessage
import org.litote.kmongo.coroutine.CoroutineDatabase

class ChatSessionDataSourceImpl(
    private val db: CoroutineDatabase
):ChatSessionDataSource {

    override suspend fun getLastFiftyMessages(idChat: String): List<ChatMessage> {
        return db.getCollection<ChatMessage>("messages-$idChat").find().descendingSort(ChatMessage::date).toList()
    }

}