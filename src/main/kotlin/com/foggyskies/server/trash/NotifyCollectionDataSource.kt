package com.foggyskies.server.trash//package com.foggyskies.server.databases.mongo.main.datasources
//
//import com.foggyskies.server.data.bettamodels.Notification
//import io.ktor.server.websocket.*
//
//interface NotifyCollectionDataSource {
//
//    suspend fun checkOnExistNotificationDocument(idReceiver: String): Boolean
//
//    suspend fun createNotificationDocument(idReceiver: String, notification: Notification)
//
//    suspend fun addNotification(idReceiver: String, notification: Notification)
//
//    suspend fun getNotification(id: String): Notification?
//
//    suspend fun watchForNotification(idUser: String, socket: DefaultWebSocketServerSession)
//
//    suspend fun deleteAllSentNotifications(idUser: String)
//
//    suspend fun checkOnExistInternalNotificationDocument(idReceiver: String): Boolean
//
//    suspend fun createInternalNotificationDocument(idReceiver: String, notification: Notification)
//
//    suspend fun addInternalNotification(idReceiver: String, notification: Notification)
//
//    suspend fun watchForInternalNotifications(idUser: String, socket: DefaultWebSocketServerSession)
//}