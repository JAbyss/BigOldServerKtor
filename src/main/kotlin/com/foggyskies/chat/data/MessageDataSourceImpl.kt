package com.foggyskies.chat.data

import com.foggyskies.chat.data.MessageDataSource
import com.jetbrains.handson.chat.server.chat.data.model.ChatMessage
import org.litote.kmongo.coroutine.CoroutineDatabase

class MessageDataSourceImpl(
    private val db: CoroutineDatabase
): MessageDataSource {

    override suspend fun insertOne(idChat: String, message: ChatMessage) {
        val messages = db.getCollection<ChatMessage>("messages-$idChat")
        messages.insertOne(message)
    }

    override suspend fun getAllMessage(idChat: String): List<ChatMessage> {
        val messages = db.getCollection<ChatMessage>("messages-$idChat").find().toList()

        return messages
    }

    override suspend fun getFiftyMessage(idChat: String): List<ChatMessage> {
        val messages = db.getCollection<ChatMessage>("messages-$idChat").find().limit(50).descendingSort(ChatMessage::date).toList()
        return messages
    }

}