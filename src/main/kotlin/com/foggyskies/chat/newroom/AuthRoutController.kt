package com.foggyskies.chat.newroom

import com.foggyskies.PasswordCoder
import com.foggyskies.chat.databases.main.models.RegistrationUserDC
import com.foggyskies.chat.databases.main.models.UserMainEntity
import com.foggyskies.chat.databases.main.models.UserNameID
import com.foggyskies.chat.databases.main.MainDBImpl
import com.foggyskies.chat.databases.main.models.Token
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
        val user = mainDBImpl.getUserByUsername(username)
        return PasswordCoder.decodeStringFS(password) == PasswordCoder.decodeStringFS(user.password)
    }

    suspend fun checkOnExistEmail(e_mail: String): Boolean {
        return mainDBImpl.checkOnExistEmail(e_mail)
    }
}