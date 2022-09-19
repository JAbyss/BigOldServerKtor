//package com.foggyskies.server.databases.mongo.newmessage.datasources
//
//import com.foggyskies.server.data.model.ImpAndDB
//import com.foggyskies.server.databases.message.models.ChatMessageCollection
//import com.foggyskies.server.databases.mongo.newmessage.models.NewMessagesCollection
//import com.foggyskies.server.routes.chat.EditMessageEntity
//import io.ktor.server.websocket.*
//
//interface NewMessageCollectionDataSource {
//
//    suspend fun checkOnExistDocument(idChat: String, idUser: String): Boolean
//
//    suspend fun getNewMessagesByIdChat(idChat: String, idUser: String): List<ChatMessageCollection>
//
//    suspend fun insertOneMessage(idChat: String, idUser: String, message: ChatMessageCollection)
//
//    suspend fun clearOneChat(idChat: String, idUser: String)
//
//    suspend fun createCollection(idUser: String)
//
//    suspend fun createDocument(idChat: String, idUser: String)
//
//    suspend fun watchForNewMessages(idUser: String, socket: DefaultWebSocketServerSession, main: ImpAndDB<MainDBImpl>)
//
//    suspend fun getAllNewMessages(idUser: String): List<NewMessagesCollection>
//
//    suspend fun deleteNewMessage(idUser: String, idChat: String, idMessage: String): Int
//
//    suspend fun editMessage(editMessageEntity: EditMessageEntity): Boolean
//}