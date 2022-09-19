package com.foggyskies.server.trash//package com.foggyskies.server.databases.mongo.main.datasources
//
//import com.foggyskies.server.databases.mongo.main.models.UserNameID
//import io.ktor.server.websocket.*
//
//interface FriendsCollectionDataSource {
//
//    suspend fun getFriendsByIdUser(idUser: String): List<UserNameID>
//
//    suspend fun createFriendsDocument(idUser: String, firstFriend: UserNameID)
//
//    suspend fun addFriendByIdUser(idUser: String, newFriend: UserNameID)
//
//    suspend fun delFriendByIdUser(idUser: String, delFriend: UserNameID)
//
//    suspend fun watchForFriend(idUser: String, socket: DefaultWebSocketServerSession)
//}