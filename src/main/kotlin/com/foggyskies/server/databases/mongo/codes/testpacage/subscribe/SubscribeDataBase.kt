package com.foggyskies.server.databases.mongo.codes.testpacage.subscribe

import com.foggyskies.server.databases.mongo.MongoCollection
import com.foggyskies.server.databases.mongo.codes.testpacage.SUBSCRIBERS
import com.foggyskies.server.databases.mongo.codes.testpacage.subscribe.SubscribeDataBase.NamesCollections.subscribers
import com.foggyskies.server.databases.mongo.subscribers.models.SubscribersDC
import com.foggyskies.server.plugin.KClient

object SubscribeDataBase {

    val db = KClient.getDatabase(SUBSCRIBERS)

    object NamesCollections{
        const val subscribers = "subscribers_"
    }

    object Subscribers: MongoCollection<SubscribersDC>(db, subscribers)

}