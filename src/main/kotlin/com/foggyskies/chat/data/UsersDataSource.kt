package com.foggyskies.chat.data

import com.jetbrains.handson.chat.server.chat.data.model.Token
import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch
import kotlinx.coroutines.flow.Flow

interface UsersDataSource {

    suspend fun checkOnExistToken(token: String): Boolean

    suspend fun getUsers(): List<UsersSearch>

    suspend fun getUsersByUsername(username: String): List<UsersSearch>

}