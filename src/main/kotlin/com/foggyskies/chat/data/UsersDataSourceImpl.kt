package com.foggyskies.chat.data

import com.foggyskies.chat.extendfun.isTrue
import com.foggyskies.chat.routes.ChatMainEntity
import com.foggyskies.chat.routes.UserMainEntity
import com.jetbrains.handson.chat.server.chat.data.model.Token
import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch
import com.mongodb.BasicDBObject

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.util.KMongoUtil.toBson

@kotlinx.serialization.Serializable
data class ChatsDC(
    var chats: List<String>
)

//@kotlinx.serialization.Serializable
//data class ChatsDD(
//    var id: String,
//    var image: String
//)

@kotlinx.serialization.Serializable
data class FormattedChatDC(
    var id: String,
    var nameChat: String,
    var idCompanion: String,
    var image: String,
)

class UsersDataSourceImpl(
    private val db: CoroutineDatabase
) : UsersDataSource {

    override suspend fun checkOnExistToken(token: String): Boolean {
        val isTokenExist = db.getCollection<Token>("token").find("{ \"_id\": \"$token\" }").toList().isNotEmpty()

        return isTokenExist
    }

    override suspend fun getUsers(): List<UsersSearch> {
        val users = db.getCollection<UsersSearch>("users").find().toList()

        return users
    }

    override suspend fun getUsersByUsername(username: String): List<UsersSearch> {
        val users = db.getCollection<UsersSearch>("users")
            .find(" { \"username\": { ${MongoOperator.regex}: '^$username.+', ${MongoOperator.options}: 'i' } } ")
            .limit(20).toList()

        return users
    }

    override suspend fun getChats(token: String): List<FormattedChatDC> {

        val usernameByToken = db.getCollection<Token>("token").find(" { \"_id\": \"$token\" } ").toList()[0].username

        val chatsId = db.getCollection<ChatsDC>("users").find("{ \"username\": \"$usernameByToken\" }").toList()[0]


        val chats = mutableListOf<FormattedChatDC>()

        isTrue(chatsId.chats.isNotEmpty()) {
//                chatsId.chats.fore

            chatsId.chats.forEachSuspend { chatId ->

                val chat = db.getCollection<ChatMainEntity>("chats").find(" { \"_id\" : \"${chatId}\" } ").toList()[0]
                val companion =
                    if (chat.firstCompanion.nameUser != usernameByToken) chat.firstCompanion else chat.secondCompanion
                val image = db.getCollection<UserMainEntity>("users").find(" { \"_id\" : \"${companion.idUser}\" } ")
                    .toList()[0].image

                val formattedItem = FormattedChatDC(
                    id = chat.idChat,
                    nameChat = companion.nameUser,
                    idCompanion = companion.idUser,
                    image = image
                )
                chats.add(formattedItem)
            }
        }
        return chats
    }

    override suspend fun addRequestToFriend(userSender: UserNameID, idUserReceiver: String) {

        val requestsFriend =
            db.getCollection<RequestFriendDC>("requestsFriend").find("{ \"_id\": \"$idUserReceiver\" } ").toList()

        if (requestsFriend.isNotEmpty()) {
            if (!requestsFriend[0].requests.contains(userSender)) {
                val newList = requestsFriend[0].requests.toMutableList()
                newList.add(userSender)
                db.getCollection<RequestFriendDC>("requestsFriend").updateOne(
                    " { \"_id\": \"$idUserReceiver\" } ",
                    "{ \"${MongoOperator.set}\": { \"requests\" : \"$newList\" }"
                )
            }
        } else {
            val document = RequestFriendDC(
                id = idUserReceiver,
                requests = listOf(userSender)
            )
            db.getCollection<RequestFriendDC>("requestsFriend").insertOne(document)
        }
    }

    override suspend fun getUserByToken(token: String): UserMainEntity {
        val usernameByToken = db.getCollection<Token>("token").find(" { \"_id\": \"$token\" } ").toList()[0].username
        val user = db.getCollection<UserMainEntity>("users").find("{ \"username\": \"$usernameByToken\" }").toList()[0]

        return user
    }

    override suspend fun acceptRequestFriend(userReceiver: UserNameID, userSender: UserNameID) {
        val requestsFriend =
            db.getCollection<RequestFriendDC>("requestsFriend").find("{ \"_id\": \"${userReceiver.id}\" } ")
                .toList()

        if (requestsFriend.isNotEmpty()) {
            requestsFriend[0].requests.forEach { userSenderL ->
                if (userSenderL.id == userSender.id) {
                    val friendCollection = db.getCollection<FriendDC>("friends")
                    val friendsReceiver = friendCollection.find(" { \"_id\": \"${userReceiver.id}\" } ").toList()
                    val friendsSender = friendCollection.find(" { \"_id\": \"${userSender.id}\" } ").toList()

                    if (friendsReceiver.isNotEmpty()) {

                        friendCollection.updateOne(
                            FriendDC::idUser eq userReceiver.id,
                            addToSet(FriendDC::friends, userSender)
                        )

                        val requestsFriendCollection = db.getCollection<RequestFriendDC>("requestsFriend")
                        val requestsFriend =
                            requestsFriendCollection.findOne(RequestFriendDC::id eq userReceiver.id)
                        if (requestsFriend != null)
                            requestsFriendCollection.updateOne(
                                RequestFriendDC::id eq userReceiver.id,
                                pull(RequestFriendDC::requests, userSender)
                            )
                    } else {
                        val document = FriendDC(
                            idUser = userReceiver.id,
                            friends = listOf(userSender)
                        )
                        friendCollection.insertOne(document)
                        val requestsFriendCollection = db.getCollection<RequestFriendDC>("requestsFriend")
                        val requestsFriend =
                            requestsFriendCollection.findOne(RequestFriendDC::id eq userReceiver.id)

                        if (requestsFriend != null)
                            requestsFriendCollection.updateOne(
                                RequestFriendDC::id eq userReceiver.id,
                                pull(RequestFriendDC::requests, userSender)
                            )
                    }

                    if (friendsSender.isNotEmpty()) {
                        friendCollection.updateOne(
                            FriendDC::idUser eq userSender.id,
                            addToSet(FriendDC::friends, userReceiver)
                        )
                    } else {
                        val document = FriendDC(
                            idUser = userSender.id,
                            friends = listOf(userReceiver)
                        )
                        friendCollection.insertOne(document)
                    }
                }
            }
        }
    }
}

suspend fun <T> Iterable<T>.forEachSuspend(action: suspend (T) -> Unit): Unit {
    for (element in this) action(element)
}

@kotlinx.serialization.Serializable
data class UserNameID(
//    @BsonId
    var id: String,
    var username: String
)

@kotlinx.serialization.Serializable
data class RequestFriendDC(
    @BsonId
    var id: String = ObjectId().toString(),
    var requests: List<UserNameID>
)

@kotlinx.serialization.Serializable
data class FriendDC(
    @BsonId
    var idUser: String,
    var friends: List<UserNameID>
)