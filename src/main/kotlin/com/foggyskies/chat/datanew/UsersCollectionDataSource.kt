package com.foggyskies.chat.datanew

import com.foggyskies.chat.data.FormattedChatDC
import com.foggyskies.chat.data.model.RegistrationUserDC
import com.foggyskies.chat.data.model.UserMainEntity
import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch

interface UsersCollectionDataSource {

    suspend fun getUsers(): List<UserMainEntity>

    suspend fun getUserByUsername(username: String): UserMainEntity

    suspend fun getUserByIdUser(idUser: String): UserMainEntity

    suspend fun getChatsByIdUser(idUser: String): List<String>

    suspend fun createUser(registrationUserDC: RegistrationUserDC)

    suspend fun searchUsers(username: String): List<UsersSearch>
}