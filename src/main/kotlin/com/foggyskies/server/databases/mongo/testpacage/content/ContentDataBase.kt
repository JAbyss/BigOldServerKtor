package com.foggyskies.server.databases.mongo.testpacage.content

import com.foggyskies.server.databases.mongo.MongoCollection
import com.foggyskies.server.databases.mongo.testpacage.CONTENT
import com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase.NameCollection.content
import com.foggyskies.server.databases.mongo.content.models.ContentUsersDC
import com.foggyskies.server.plugin.KClient

object ContentDataBase {

    val db = KClient.getDatabase(com.foggyskies.server.databases.mongo.testpacage.CONTENT)

    object NameCollection {
        const val content = "content_"
    }

    object Content : MongoCollection<ContentUsersDC>(com.foggyskies.server.databases.mongo.testpacage.content.ContentDataBase.db, content)
}