package com.foggyskies.chat.data

import com.jetbrains.handson.chat.server.chat.data.model.Token
import org.litote.kmongo.coroutine.CoroutineDatabase

class TokenDataSourceImpl(
    private val db: CoroutineDatabase
): TokenDataSource {

    override suspend fun checkOnExistToken(token: String): Boolean {
        val isTokenExist = db.getCollection<Token>("token").find("{ \"_id\": \"$token\" }").toList().isNotEmpty()

        return isTokenExist
    }
}