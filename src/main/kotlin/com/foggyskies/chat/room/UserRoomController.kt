package com.foggyskies.chat.room

import com.foggyskies.chat.data.FormattedChatDC
import com.foggyskies.chat.data.UserNameID
import com.foggyskies.chat.data.UsersDataSource
import com.foggyskies.chat.routes.UserMainEntity
import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch

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

    suspend fun getChats(token: String): List<FormattedChatDC>{
        return usersDataSource.getChats(token)
    }

    suspend fun addRequestToFriend(userSender: UserNameID, idUserReceiver: String){
        usersDataSource.addRequestToFriend(userSender, idUserReceiver)
    }

    suspend fun getUserByToken(token: String): UserMainEntity{
        return usersDataSource.getUserByToken(token)
    }

    suspend fun acceptRequestFriend(userReceiver: UserNameID, userSender: UserNameID){
        usersDataSource.acceptRequestFriend(userReceiver, userSender)
    }
}