package com.foggyskies.chat.room

import com.foggyskies.chat.data.FormattedChatDC
import com.foggyskies.chat.data.UsersDataSource
import com.foggyskies.chat.data.model.FriendListDC
import com.foggyskies.chat.data.model.UserMainEntity
import com.foggyskies.chat.data.model.UserNameID
import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch
import io.ktor.websocket.*

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

    suspend fun getUserByToken(token: String): UserMainEntity {
        return usersDataSource.getUserByToken(token)
    }

    suspend fun acceptRequestFriend(userReceiver: UserNameID, userSender: UserNameID){
        usersDataSource.acceptRequestFriend(userReceiver, userSender)
    }

    suspend fun getFriends(token: String): List<FriendListDC>{
        return usersDataSource.getFriends(token)
    }

    suspend fun getRequestsFriends(token: String): List<UserNameID>? {
        return usersDataSource.getRequestsFriends(token)
    }

    suspend fun watchForRequestsFriends(idUser: String, socket: DefaultWebSocketServerSession) {
        usersDataSource.watchForRequestsFriends(idUser, socket)
    }

    suspend fun watchForFriend(idUser: String, socket: DefaultWebSocketServerSession){
        usersDataSource.watchForFriend(idUser, socket)
    }
}