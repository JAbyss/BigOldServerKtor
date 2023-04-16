package com.foggyskies.server.databases.mongo.testpacage.new_messages

import com.foggyskies.server.databases.mongo.MongoCollection
import com.foggyskies.server.databases.mongo.testpacage.NEW_MESSAGES
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.NewMessagesDataBase.NamesCollections.newMessage
import com.foggyskies.server.databases.mongo.newmessage.models.NewMessagesCollection
import com.foggyskies.server.plugin.KClient

object NewMessagesDataBase {

    val db = KClient.getDatabase(com.foggyskies.server.databases.mongo.testpacage.NEW_MESSAGES)

    object NamesCollections{
        const val newMessage = "new_messages_"
    }

    object NewMessages: MongoCollection<NewMessagesCollection>(db, newMessage)

}