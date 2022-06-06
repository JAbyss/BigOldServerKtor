package com.foggyskies.chat.newroom

import com.foggyskies.chat.databases.main.models.UserIUSI
import com.foggyskies.chat.databases.main.MainDBImpl
import org.litote.kmongo.coroutine.CoroutineDatabase

class CreateChatRoutController(
    private val mainDBImpl: MainDBImpl,
//    private val mainDB: CoroutineDatabase,
    private val messagesDB: CoroutineDatabase
    ) {

    suspend fun checkOnExistChatByIdUsers(idUserFirst: String, idUserSecond: String): String {
        return mainDBImpl.checkOnExistChatByIdUsers(idUserFirst, idUserSecond)
    }

    suspend fun createChat(idUserFirst: String, idUserSecond: String): String{
        val firstCompanion = mainDBImpl.getUserByIdUser(idUserFirst).toUserNameID()
        val secondCompanion = mainDBImpl.getUserByIdUser(idUserSecond).toUserNameID()
        val idChat = mainDBImpl.createChat(firstCompanion, secondCompanion)
        mainDBImpl.addChatToUsersByIdUsers(firstCompanion.id, secondCompanion.id, idChat)
        messagesDB.createCollection("messages_$idChat")
        return idChat
    }

    suspend fun checkOnExistToken(token: String): Boolean {
        return mainDBImpl.checkOnExistToken(token)
    }

    suspend fun getUserByUsername(username: String): UserIUSI {
        return mainDBImpl.getUserByUsername(username).toUserIUSI()
    }
}