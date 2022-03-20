package com.foggyskies.chat.room

import com.foggyskies.chat.data.AuthDataSource
import com.foggyskies.chat.data.model.UserMainEntity

class AuthRoomController(
    private val authDataSource: AuthDataSource
) {

    suspend fun checkOnExistUser(username: String): Boolean {

        return authDataSource.checkOnExistUser(username)
    }

    suspend fun insertUser(user: UserMainEntity) {
        authDataSource.insertUser(user)
    }

    suspend fun checkOnExistToken(username: String): Boolean {
        return authDataSource.checkOnExistToken(username)
    }

    suspend fun createToken(username: String): String {
        return authDataSource.createToken(username)
    }

    suspend fun getToken(username: String): String {
        return authDataSource.getToken(username)
    }

    suspend fun checkPasswordOnCorrect(password: String): Boolean {
        return authDataSource.checkPasswordOnCorrect(password)
    }

    suspend fun checkOnExistEmail(e_mail: String): Boolean {
        return authDataSource.checkOnExistEmail(e_mail)
    }

}