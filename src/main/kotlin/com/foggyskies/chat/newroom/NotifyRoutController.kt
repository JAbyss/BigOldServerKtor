package com.foggyskies.chat.newroom

import com.foggyskies.chat.data.bettamodels.Notification
import com.foggyskies.chat.datanew.AllCollectionImpl
import io.ktor.websocket.*
import org.litote.kmongo.coroutine.CoroutineDatabase

class NotifyRoutController(
    private val allCollectionImpl: AllCollectionImpl,
    private val db: CoroutineDatabase
) {

    suspend fun getNotification(id: String): Notification?{
        val notification = allCollectionImpl.getNotification(id)
        return notification
    }

    suspend fun watchForNotification(idUser: String, socket: DefaultWebSocketServerSession){
        allCollectionImpl.watchForNotification(idUser, socket)
    }
}