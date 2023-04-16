package com.foggyskies.server.trash//package com.foggyskies.server.databases.mongo.main.datasources
//
//import com.foggyskies.server.databases.mongo.main.models.UserNameID
//import io.ktor.server.websocket.*
//
//interface RequestsFriendsCollectionDataSource {
//
//    suspend fun createRequestsFriendsByIdUser(idUser: String, firstRequest: UserNameID)
//
//    suspend fun getRequestsFriendByIdUser(idUser: String): List<UserNameID>
//
//    suspend fun addRequestFriendsByIdUser(idUser: String, newRequest: UserNameID)
//
//    suspend fun delRequestFriendsByIdUser(idUser: String, delRequest: UserNameID)
//
//    suspend fun watchForRequestsFriends(idUser: String, socket: DefaultWebSocketServerSession)
//}