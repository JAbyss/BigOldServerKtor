package com.foggyskies.chat.databases.subscribers

import com.foggyskies.chat.databases.subscribers.datasources.SubscribersCollectionDataSource
import org.litote.kmongo.coroutine.CoroutineDatabase

class SubscribersImpl(
    private val db: CoroutineDatabase
) : SubscribersCollectionDataSource {


}