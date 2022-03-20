package com.foggyskies.chat.data

import com.foggyskies.chat.data.model.UserMainEntity

interface AuthDataSource {

    suspend fun insertUser(user: UserMainEntity)

    suspend fun getUser(username: String): UserMainEntity

    suspend fun checkOnExistUser(username: String): Boolean

    suspend fun checkOnExistToken(username: String): Boolean

    suspend fun createToken(username: String): String

    suspend fun getToken(username: String): String

    suspend fun checkPasswordOnCorrect(password: String): Boolean

    suspend fun checkOnExistEmail(e_mail: String): Boolean
}