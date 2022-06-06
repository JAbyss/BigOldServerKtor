package com.foggyskies.chat.databases.message

import com.foggyskies.chat.databases.message.datasources.MessagesCollectionDataSource
import com.foggyskies.chat.databases.message.models.ChatMessageCollection
import org.litote.kmongo.bson
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.lt

class MessagesDBImpl(
    private val db: CoroutineDatabase
) : MessagesCollectionDataSource {

    override suspend fun insertOne(idChat: String, message: ChatMessageCollection) {
        db.getCollection<ChatMessageCollection>("${BASE_NAME_COLLECTION}$idChat").insertOne(message)
    }

    override suspend fun getAllMessages(idChat: String): List<ChatMessageCollection> {
        return db.getCollection<ChatMessageCollection>("${BASE_NAME_COLLECTION}$idChat").find().toList()
    }

    override suspend fun getFiftyMessage(idChat: String): List<ChatMessageCollection> {
        return db.getCollection<ChatMessageCollection>("${BASE_NAME_COLLECTION}$idChat").find().sort("{ \$natural: -1 }".bson)
            .limit(100)
            .toList().reversed()
    }

    override suspend fun getNextMessages(idChat: String, lastMessageId: String): List<ChatMessageCollection> {
        return db.getCollection<ChatMessageCollection>("${BASE_NAME_COLLECTION}$idChat").find(ChatMessageCollection::id lt lastMessageId).sort("{ \$natural: -1 }".bson)
            .limit(100)
            .toList()
    }

    override suspend fun getLastMessage(idChat: String): String {
        val chat =
            db.getCollection<ChatMessageCollection>("${BASE_NAME_COLLECTION}$idChat").find().sort("{ \$natural: -1 }".bson).limit(1)
                .first()

        return if (chat != null) {
            if (chat.message.isEmpty()) {
                if (chat.listImages.isNotEmpty())
                    "Изображение"
                else
                    ""
            } else
                db.getCollection<ChatMessageCollection>("${BASE_NAME_COLLECTION}$idChat").find().sort("{ \$natural: -1 }".bson)
                    .limit(1)
                    .first()?.message ?: ""
        } else ""
    }

    override suspend fun createCollection(idChat: String) {
        try {
            db.createCollection("${Companion.BASE_NAME_COLLECTION}$idChat")
        } catch (_: Exception) {

        }
    }

    override suspend fun deleteMessage(idChat: String, idMessage: String): Int {
        return db.getCollection<ChatMessageCollection>("${Companion.BASE_NAME_COLLECTION}$idChat").deleteOne(ChatMessageCollection::id eq idMessage).deletedCount.toInt()
    }

    companion object {
        private const val BASE_NAME_COLLECTION = "messages_"
    }
}