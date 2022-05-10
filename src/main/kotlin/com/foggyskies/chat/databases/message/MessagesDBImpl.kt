package com.foggyskies.chat.databases.message

import com.foggyskies.chat.databases.message.datasources.MessagesCollectionDataSource
import com.jetbrains.handson.chat.server.chat.data.model.ChatMessage
import org.litote.kmongo.bson
import org.litote.kmongo.coroutine.CoroutineDatabase

class MessagesDBImpl(
    private val db: CoroutineDatabase
) : MessagesCollectionDataSource {

    private val BASE_NAME_COLLECTION = "messages_"

    override suspend fun insertOne(idChat: String, message: ChatMessage) {
        db.getCollection<ChatMessage>("$BASE_NAME_COLLECTION$idChat").insertOne(message)
    }

    override suspend fun getAllMessages(idChat: String): List<ChatMessage> {
        return db.getCollection<ChatMessage>("$BASE_NAME_COLLECTION$idChat").find().toList()
    }

    override suspend fun getFiftyMessage(idChat: String): List<ChatMessage> {
        return db.getCollection<ChatMessage>("$BASE_NAME_COLLECTION$idChat").find().sort("{ \$natural: -1 }".bson)
            .limit(50)
            .toList().reversed()
    }

    override suspend fun getLastMessage(idChat: String): String {
        val chat =
            db.getCollection<ChatMessage>("$BASE_NAME_COLLECTION$idChat").find().sort("{ \$natural: -1 }".bson).limit(1)
                .first()

        return if (chat != null) {
            if (chat.message.isEmpty()) {
                if (chat.listImages.isNotEmpty())
                    "Изображение"
                else
                    ""
            } else
                db.getCollection<ChatMessage>("$BASE_NAME_COLLECTION$idChat").find().sort("{ \$natural: -1 }".bson)
                    .limit(1)
                    .first()?.message ?: ""
        } else ""
    }

    override suspend fun createCollection(idChat: String) {
        try {
            db.createCollection("$BASE_NAME_COLLECTION$idChat")
        } catch (_: Exception) {

        }
    }

}