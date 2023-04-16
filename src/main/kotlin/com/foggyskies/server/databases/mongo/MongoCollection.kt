package com.foggyskies.server.databases.mongo

import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

open class MongoCollection<T>(
    val db: CoroutineDatabase,
    val name: String
)

inline fun <reified T : Any> MongoCollection<T>.getCollection(advName: String = ""): CoroutineCollection<T> {
    return db.getCollection(this.name+advName)
}