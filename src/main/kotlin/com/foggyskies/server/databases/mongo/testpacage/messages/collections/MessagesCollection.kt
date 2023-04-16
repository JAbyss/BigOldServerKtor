package com.foggyskies.server.databases.mongo.testpacage.messages.collections

import com.foggyskies.server.databases.message.models.ChatMessageCollection
import com.foggyskies.server.databases.mongo.codes.testpacage.messages.MessagesDataBase
import com.foggyskies.server.databases.mongo.getCollection
import com.foggyskies.server.extendfun.toBoolean
import org.litote.kmongo.bson
import org.litote.kmongo.eq
import org.litote.kmongo.lt
import org.litote.kmongo.setValue

suspend fun MessagesDataBase.Messages.insertOne(idChat: String, message: ChatMessageCollection) {
    getCollection(idChat).insertOne(message)
}

suspend fun MessagesDataBase.Messages.getAllMessages(idChat: String): List<ChatMessageCollection> {
    return getCollection(idChat).find().toList()
}

suspend fun MessagesDataBase.Messages.getFiftyMessage(idChat: String): List<ChatMessageCollection> {
    return getCollection(idChat).find()
        .sort("{ \$natural: -1 }".bson)
        .limit(100)
        .toList()
}

suspend fun MessagesDataBase.Messages.getNextMessages(
    idChat: String,
    lastMessageId: String
): List<ChatMessageCollection> {
    return getCollection(idChat)
        .find(ChatMessageCollection::id lt lastMessageId).sort("{ \$natural: -1 }".bson)
        .limit(100)
        .toList()
}

suspend fun MessagesDataBase.Messages.getLastMessage(idChat: String): String {
    val chat =
        getCollection(idChat).find()
            .sort("{ \$natural: -1 }".bson).limit(1)
            .first()

    return if (chat != null) {
        if (chat.message.isEmpty()) {
            if (chat.listImages.isNotEmpty())
                "Изображение"
            else
                ""
        } else
            getCollection(idChat).find()
                .sort("{ \$natural: -1 }".bson)
                .limit(1)
                .first()?.message ?: ""
    } else ""
}

suspend fun MessagesDataBase.Messages.createCollection(idChat: String) {
    MessagesDataBase.Messages.db.createCollection(name + idChat)
}

suspend fun MessagesDataBase.Messages.deleteMessage(idChat: String, idMessage: String): Int {
    return getCollection(idChat)
        .deleteOne(ChatMessageCollection::id eq idMessage).deletedCount.toInt()
}

suspend fun MessagesDataBase.Messages.editMessage(idChat: String, idMessage: String, newMessage: String): Boolean {

    return getCollection(idChat)
        .updateOneById(idMessage, setValue(ChatMessageCollection::message, newMessage)).modifiedCount.toInt()
        .toBoolean()

}

suspend fun MessagesDataBase.Messages.getMessageById(idChat: String, idMessage: String): ChatMessageCollection? {
    return getCollection(idChat).findOne(ChatMessageCollection::id eq idMessage)
}