package com.foggyskies.chat.data

import com.jetbrains.handson.chat.server.chat.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import org.litote.kmongo.coroutine.CoroutineDatabase

class MessageDataSourceImpl(
    private val db: CoroutineDatabase
): MessageDataSource {

    override suspend fun insertOne(idChat: String, message: ChatMessage) {
        val messages = db.getCollection<ChatMessage>("messages-$idChat")
        messages.insertOne(message)
    }

    override suspend fun getAllMessage(idChat: String): List<ChatMessage> {

        return db.getCollection<ChatMessage>("messages-$idChat").find().toList()
    }

    override suspend fun getFiftyMessage(idChat: String): List<ChatMessage> {
        return db.getCollection<ChatMessage>("messages-$idChat").find().limit(50).ascendingSort(ChatMessage::date)
            .toList()
    }

}