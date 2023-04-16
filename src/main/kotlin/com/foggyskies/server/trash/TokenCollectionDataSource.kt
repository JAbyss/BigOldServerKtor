package com.foggyskies.server.trash//package com.foggyskies.server.databases.mongo.main.datasources
//
//import com.foggyskies.server.databases.mongo.main.models.UserNameID
//import com.foggyskies.server.databases.mongo.main.models.Token
//
//interface TokenCollectionDataSource {
//
//    suspend fun createToken(user: UserNameID)
//
//    suspend fun delTokenByTokenId(idToken: String)
//
//    suspend fun checkOnExistToken(token: String): Boolean
//
//    suspend fun checkOnExistTokenByUsername(username: String): Boolean
//
//    suspend fun getTokenByToken(token: String): Token
//
//    suspend fun getTokenByUsername(username: String): Token
//
//    suspend fun deleteTokenByIdUser(idUser: String)
//}