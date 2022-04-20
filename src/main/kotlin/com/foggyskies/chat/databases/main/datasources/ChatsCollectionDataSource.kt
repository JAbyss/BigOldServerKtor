package com.foggyskies.chat.databases.main.datasources

import com.foggyskies.chat.data.model.ChatMainEntity
import com.foggyskies.chat.data.model.ChatMainEntity_
import com.foggyskies.chat.data.model.ChatUserEntity
import com.foggyskies.chat.data.model.UserNameID

interface ChatsCollectionDataSource {

    suspend fun checkOnExistChatByIdUsers(idUserFirst: String, idUserSecond: String): String

    suspend fun getChatById(idChat: String): ChatMainEntity

    suspend fun createChat(firstCompanion: UserNameID, secondCompanion: UserNameID): String

}