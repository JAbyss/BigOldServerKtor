package com.foggyskies.chat.data

import com.jetbrains.handson.chat.server.chat.data.model.Token
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class TokenDataSourceImpl(
    private val db: CoroutineDatabase
): TokenDataSource {

    override suspend fun checkOnExistToken(token: String): Boolean {
        val isTokenExist = db.getCollection<Token>("tokens").find(Token::id eq token).toList().isNotEmpty()

        return isTokenExist
    }
}