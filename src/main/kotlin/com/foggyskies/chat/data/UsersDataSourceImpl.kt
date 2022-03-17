package com.foggyskies.chat.data

import com.jetbrains.handson.chat.server.chat.data.model.Token
import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch
import kotlinx.coroutines.flow.Flow
import org.litote.kmongo.MongoOperator
import org.litote.kmongo.coroutine.CoroutineDatabase

class UsersDataSourceImpl(
    private val db: CoroutineDatabase
) : UsersDataSource {

    override suspend fun checkOnExistToken(token: String): Boolean {
        val isTokenExist = db.getCollection<Token>("token").find("{ \"_id\": \"$token\" }").toList().isNotEmpty()

        return isTokenExist
    }

    override suspend fun getUsers(): List<UsersSearch> {
        val users = db.getCollection<UsersSearch>("users").find().toList()

        return users
    }

    override suspend fun getUsersByUsername(username: String): List<UsersSearch> {
        val users = db.getCollection<UsersSearch>("users").find(" { \"username\": { ${MongoOperator.regex}: '^$username.+', ${MongoOperator.options}: 'i' } } ").toList()

        return users
    }
}