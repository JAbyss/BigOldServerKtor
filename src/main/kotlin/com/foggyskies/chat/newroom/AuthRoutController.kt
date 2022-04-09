package com.foggyskies.chat.newroom

import com.foggyskies.chat.data.model.RegistrationUserDC
import com.foggyskies.chat.data.model.UserMainEntity
import com.foggyskies.chat.data.model.UserNameID
import com.foggyskies.chat.datanew.AllCollectionImpl
import com.jetbrains.handson.chat.server.chat.data.model.Token
import org.litote.kmongo.coroutine.CoroutineDatabase

class AuthRoutController(
    private val allCollectionImpl: AllCollectionImpl,
    private val db: CoroutineDatabase
) {

    suspend fun getUserByUsername(username: String): UserMainEntity {
        return allCollectionImpl.getUserByUsername(username)
    }

    suspend fun checkOnExistUser(username: String): Boolean {
        return allCollectionImpl.checkOnExistUser(username)
    }

    suspend fun creteUser(user: RegistrationUserDC) {
        allCollectionImpl.createUser(user)
    }

    suspend fun checkOnExistTokenByUsername(username: String): Boolean {
        return allCollectionImpl.checkOnExistTokenByUsername(username)
    }

    suspend fun createToken(user: UserNameID): Token {
        allCollectionImpl.createToken(user)
        return  allCollectionImpl.getTokenByUsername(user.username)
    }

    suspend fun getToken(username: String): Token {
        return allCollectionImpl.getTokenByUsername(username)
    }

    suspend fun checkPasswordOnCorrect(username: String, password: String): Boolean {
        return allCollectionImpl.checkPasswordOnCorrect(username, password)
    }

    suspend fun checkOnExistEmail(e_mail: String): Boolean {
        return allCollectionImpl.checkOnExistEmail(e_mail)
    }

}