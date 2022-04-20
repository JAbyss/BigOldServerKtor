package com.foggyskies.chat.newroom

import com.foggyskies.chat.data.model.UserIUSI
import com.foggyskies.chat.databases.main.AllCollectionImpl
import org.litote.kmongo.coroutine.CoroutineDatabase

class CreateChatRoutController(
    private val allCollectionImpl: AllCollectionImpl,
//    private val mainDB: CoroutineDatabase,
    private val messagesDB: CoroutineDatabase
    ) {

    suspend fun checkOnExistChatByIdUsers(idUserFirst: String, idUserSecond: String): String {
        return allCollectionImpl.checkOnExistChatByIdUsers(idUserFirst, idUserSecond)
    }

    suspend fun createChat(idUserFirst: String, idUserSecond: String): String{
        val firstCompanion = allCollectionImpl.getUserByIdUser(idUserFirst).toUserNameID()
        val secondCompanion = allCollectionImpl.getUserByIdUser(idUserSecond).toUserNameID()
        val idChat = allCollectionImpl.createChat(firstCompanion, secondCompanion)
        allCollectionImpl.addChatToUsersByIdUsers(firstCompanion.id, secondCompanion.id, idChat)
        messagesDB.createCollection("messages-$idChat")
        return idChat
    }

    suspend fun checkOnExistToken(token: String): Boolean {
        return allCollectionImpl.checkOnExistToken(token)
    }

    suspend fun getUserByUsername(username: String): UserIUSI {
        return allCollectionImpl.getUserByUsername(username).toUserIUSI()
    }
}