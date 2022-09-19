package com.foggyskies.server.trash//package com.foggyskies.server.databases.mongo.main.datasources
//
//import com.foggyskies.server.data.model.*
//import com.foggyskies.server.databases.mongo.main.models.ChatMainEntity
//import com.foggyskies.server.databases.mongo.main.models.UserNameID
//
//interface ChatsCollectionDataSource {
//
//    suspend fun checkOnExistChatByIdUsers(idUserFirst: String, idUserSecond: String): String
//
//    suspend fun getChatById(idChat: String): ChatMainEntity
//
//    suspend fun createChat(firstCompanion: UserNameID, secondCompanion: UserNameID): String
//
//    suspend fun muteChat(idChat: String, idUser: String, nameField: ChatUserEntity_<ChatMainEntity>, time: String = "")
//}