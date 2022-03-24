package com.foggyskies.chat.datanew

import com.foggyskies.chat.data.bettamodels.Notification
import io.ktor.websocket.*

interface NotifyCollectionDataSource {

    suspend fun getNotification(id: String): Notification?

    suspend fun watchForNotification(idUser: String, socket: DefaultWebSocketServerSession)
}