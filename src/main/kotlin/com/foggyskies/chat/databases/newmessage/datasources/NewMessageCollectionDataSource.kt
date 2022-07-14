package com.foggyskies.chat.databases.newmessage.datasources

import com.foggyskies.chat.data.model.ImpAndDB
import com.foggyskies.chat.databases.main.MainDBImpl
import com.foggyskies.chat.databases.message.models.ChatMessageCollection
import com.foggyskies.chat.databases.newmessage.models.NewMessagesCollection
import com.foggyskies.chat.routes.EditMessageEntity
import io.ktor.websocket.*

interface NewMessageCollectionDataSource {

    suspend fun checkOnExistDocument(idChat: String, idUser: String): Boolean

    suspend fun getNewMessagesByIdChat(idChat: String, idUser: String): List<ChatMessageCollection>

    suspend fun insertOneMessage(idChat: String, idUser: String, message: ChatMessageCollection)

    suspend fun clearOneChat(idChat: String, idUser: String)

    suspend fun createCollection(idUser: String)

    suspend fun createDocument(idChat: String, idUser: String)

    suspend fun watchForNewMessages(idUser: String, socket: DefaultWebSocketServerSession, main: ImpAndDB<MainDBImpl>)

    suspend fun getAllNewMessages(idUser: String): List<NewMessagesCollection>

    suspend fun deleteNewMessage(idUser: String, idChat: String, idMessage: String): Int

    suspend fun editMessage(editMessageEntity: EditMessageEntity): Boolean
}