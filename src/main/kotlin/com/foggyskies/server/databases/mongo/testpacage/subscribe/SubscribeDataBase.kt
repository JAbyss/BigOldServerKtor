package com.foggyskies.server.databases.mongo.testpacage.subscribe

import com.foggyskies.server.databases.mongo.MongoCollection
import com.foggyskies.server.databases.mongo.testpacage.SUBSCRIBERS
import com.foggyskies.server.databases.mongo.codes.testpacage.subscribe.SubscribeDataBase.NamesCollections.subscribers
import com.foggyskies.server.databases.mongo.subscribers.models.SubscribersDC
import com.foggyskies.server.plugin.KClient

object SubscribeDataBase {

    val db = KClient.getDatabase(com.foggyskies.server.databases.mongo.testpacage.SUBSCRIBERS)

    object NamesCollections{
        const val subscribers = "subscribers_"
    }

    object Subscribers: MongoCollection<SubscribersDC>(db, subscribers)

}