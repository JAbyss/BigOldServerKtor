package com.foggyskies.server.databases.mongo.codes.testpacage.messages

import com.foggyskies.server.databases.message.models.ChatMessageCollection
import com.foggyskies.server.databases.mongo.MongoCollection
import com.foggyskies.server.databases.mongo.codes.testpacage.MESSAGES
import com.foggyskies.server.databases.mongo.codes.testpacage.messages.MessagesDataBase.NameCollections.message
import com.foggyskies.server.plugin.KClient

object MessagesDataBase{

    val db = KClient.getDatabase(MESSAGES)

    object NameCollections{
        const val message = "messages_"
    }

    object Messages: MongoCollection<ChatMessageCollection>(db, message)

}