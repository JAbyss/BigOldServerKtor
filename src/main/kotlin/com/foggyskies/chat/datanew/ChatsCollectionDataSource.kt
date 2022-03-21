package com.foggyskies.chat.datanew

import com.foggyskies.chat.data.model.ChatMainEntity
import com.foggyskies.chat.data.model.ChatMainEntity_
import com.foggyskies.chat.data.model.ChatUserEntity
import com.foggyskies.chat.data.model.UserNameID

interface ChatsCollectionDataSource {

    suspend fun getChatById(idChat: String): ChatMainEntity

    suspend fun createChat(firstCompanion: UserNameID, secondCompanion: UserNameID): String

}