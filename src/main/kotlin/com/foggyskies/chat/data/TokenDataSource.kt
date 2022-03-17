package com.foggyskies.chat.data

interface TokenDataSource {

    suspend fun checkOnExistToken(token: String): Boolean

}