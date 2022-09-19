//package com.foggyskies.server.newroom
//
//import com.foggyskies.server.data.bettamodels.Notification
//import com.foggyskies.server.databases.mongo.main.MainDBImpl
//import com.foggyskies.server.databases.mongo.main.models.ChatMainEntity
//import com.foggyskies.server.databases.mongo.main.models.ChatMainEntity_
////import com.foggyskies.server.databases.main.models.ChatMainEntity_
//import com.foggyskies.server.databases.mongo.main.models.FormattedChatDC
//import com.foggyskies.server.databases.mongo.main.models.UserMainEntity
//import io.ktor.server.websocket.*
//import org.litote.kmongo.and
//import org.litote.kmongo.coroutine.CoroutineDatabase
//import org.litote.kmongo.eq
//import org.litote.kmongo.or
//
//class NotifyRoutController(
//    private val mainDBImpl: MainDBImpl,
//    private val db: CoroutineDatabase
//) {
//
//    suspend fun getNotification(id: String): Notification?{
//        val notification = mainDBImpl.getNotification(id)
//        return notification
//    }
//
//    suspend fun watchForNotification(idUser: String, socket: DefaultWebSocketServerSession){
//        mainDBImpl.watchForNotification(idUser, socket)
//    }
//
//    suspend fun getUserByToken(token: String): UserMainEntity? {
//        val username = mainDBImpl.getTokenByToken(token).username
//        return mainDBImpl.getUserByUsername(username)
//    }
//
//    suspend fun getFormattedChatByUsers(idUserFirst: String, idUserSecond: String): FormattedChatDC {
//
//        val user = mainDBImpl.getUserByIdUser(idUserSecond)
//
//        val idChat = db.getCollection<ChatMainEntity>("chats").findOne(
//            and(
//                or(
//                    ChatMainEntity_.FirstCompanion.idUser eq idUserFirst,
//                    ChatMainEntity_.FirstCompanion.idUser eq idUserSecond
//                ),
//                or(
//                    ChatMainEntity_.SecondCompanion.idUser eq idUserSecond,
//                    ChatMainEntity_.SecondCompanion.idUser eq idUserFirst
//                )
//            )
//        )?.idChat!!
//
//        val formattedChat = FormattedChatDC(
//            id = idChat,
//            idCompanion = user.idUser,
//            nameChat = user.username,
//            image = "user.image",
//            lastMessage = ""
//        )
//
//        return formattedChat
//    }
//}