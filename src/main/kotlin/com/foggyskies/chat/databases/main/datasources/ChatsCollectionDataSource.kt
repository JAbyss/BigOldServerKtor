package com.foggyskies.chat.databases.main.datasources

import com.foggyskies.chat.data.model.*
import com.foggyskies.chat.databases.main.models.ChatMainEntity
import com.foggyskies.chat.databases.main.models.UserNameID

interface ChatsCollectionDataSource {

    suspend fun checkOnExistChatByIdUsers(idUserFirst: String, idUserSecond: String): String

    suspend fun getChatById(idChat: String): ChatMainEntity

    suspend fun createChat(firstCompanion: UserNameID, secondCompanion: UserNameID): String

    suspend fun muteChat(idChat: String, idUser: String, nameField: ChatUserEntity_<ChatMainEntity>, time: String = "")
}