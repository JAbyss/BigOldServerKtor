package com.foggyskies.chat.newroom

import com.jetbrains.handson.chat.server.chat.data.model.Token
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

sealed class CheckTokenExist(
    private val db: CoroutineDatabase
){
    suspend fun checkOnExistToken(token: String): Boolean {
        return db.getCollection<Token>("tokens").findOne(Token::id eq token) != null
    }
}