package com.foggyskies.chat.datanew

import com.foggyskies.chat.data.model.UserNameID
import com.jetbrains.handson.chat.server.chat.data.model.Token

interface TokenCollectionDataSource {

    suspend fun createToken(user: UserNameID)

    suspend fun delTokenByTokenId(idToken: String)

    suspend fun checkOnExistToken(token: String): Boolean

    suspend fun checkOnExistTokenByUsername(username: String): Boolean

    suspend fun getTokenByToken(token: String): Token

//    suspend fun delTokenByIdUser(idUser: String)
//
//    suspend fun delTokenByUsername(username: String)

    suspend fun getTokenByUsername(username: String): Token

//    suspend fun getTokenByIdUser(idUser: String): String

//    suspend fun getTokenByUserNameID(user: UserNameID): String
}