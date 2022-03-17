package com.foggyskies.chat.room

import com.foggyskies.chat.data.UsersDataSource
import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch
import kotlinx.coroutines.flow.Flow

class UserRoomController(
    private val usersDataSource: UsersDataSource
) {

    suspend fun checkOnExistToken(token: String): Boolean {
        return usersDataSource.checkOnExistToken(token)
    }

    suspend fun getUsers(): List<UsersSearch> {
        return usersDataSource.getUsers()
    }

    suspend fun getUsersByUsername(username: String): List<UsersSearch> {
        return usersDataSource.getUsersByUsername(username)
    }

}