package com.foggyskies.chat.databases.main.datasources

import com.foggyskies.chat.databases.main.models.RegistrationUserDC
import com.foggyskies.chat.databases.main.models.UserMainEntity
import com.foggyskies.chat.databases.main.models.UsersSearch

interface UsersCollectionDataSource {

    suspend fun getUsers(): List<UserMainEntity>

    suspend fun getUserByUsername(username: String): UserMainEntity

    suspend fun getUserByIdUser(idUser: String): UserMainEntity

    suspend fun getChatsByIdUser(idUser: String): List<String>

    suspend fun createUser(registrationUserDC: RegistrationUserDC)

    suspend fun searchUsers(idUser: String, username: String): List<UsersSearch>

    suspend fun addChatToUsersByIdUsers(idUserFirst: String, idUserSecond: String, idChat: String)

    suspend fun setStatusUser(idUser: String, status: String)

    suspend fun checkOnExistEmail(e_mail: String): Boolean

//    suspend fun checkPasswordOnCorrect(username: String, password: String): Boolean

    suspend fun checkOnExistUser(username: String): Boolean

    suspend fun getStatusByIdUser(idUser: String): String
}