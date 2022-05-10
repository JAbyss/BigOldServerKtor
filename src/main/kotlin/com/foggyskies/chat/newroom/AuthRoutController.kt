package com.foggyskies.chat.newroom

import com.foggyskies.chat.data.model.RegistrationUserDC
import com.foggyskies.chat.data.model.UserMainEntity
import com.foggyskies.chat.data.model.UserNameID
import com.foggyskies.chat.databases.main.MainDBImpl
import com.jetbrains.handson.chat.server.chat.data.model.Token
import org.litote.kmongo.coroutine.CoroutineDatabase

class AuthRoutController(
    private val mainDBImpl: MainDBImpl,
    private val db: CoroutineDatabase
) {

    suspend fun getUserByUsername(username: String): UserMainEntity {
        return mainDBImpl.getUserByUsername(username)
    }

    suspend fun checkOnExistUser(username: String): Boolean {
        return mainDBImpl.checkOnExistUser(username)
    }

    suspend fun createUser(user: RegistrationUserDC) {
        mainDBImpl.createUser(user)
    }

    suspend fun checkOnExistTokenByUsername(username: String): Boolean {
        return mainDBImpl.checkOnExistTokenByUsername(username)
    }

    suspend fun createToken(user: UserNameID): Token {
        mainDBImpl.createToken(user)
        return  mainDBImpl.getTokenByUsername(user.username)
    }

    suspend fun getToken(username: String): Token {
        return mainDBImpl.getTokenByUsername(username)
    }

    suspend fun checkPasswordOnCorrect(username: String, password: String): Boolean {
        return mainDBImpl.checkPasswordOnCorrect(username, password)
    }

    suspend fun checkOnExistEmail(e_mail: String): Boolean {
        return mainDBImpl.checkOnExistEmail(e_mail)
    }
}