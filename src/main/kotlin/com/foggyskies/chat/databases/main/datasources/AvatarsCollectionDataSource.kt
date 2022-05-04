package com.foggyskies.chat.databases.main.datasources

interface AvatarsCollectionDataSource {

    suspend fun getAvatarByIdUser(idUser: String): String

    suspend fun changeAvatarByUserId(idUser: String, pathToImage: String): String
}