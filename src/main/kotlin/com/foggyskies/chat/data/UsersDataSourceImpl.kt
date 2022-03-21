package com.foggyskies.chat.data

import com.foggyskies.chat.data.model.*
import com.foggyskies.chat.extendfun.isTrue
import com.jetbrains.handson.chat.server.chat.data.model.Token
import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase

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
        val isTokenExist = db.getCollection<Token>("token").find(Token::id eq token).toList().isNotEmpty()

        return isTokenExist
    }

    override suspend fun getUsers(): List<UsersSearch> {
        val users = db.getCollection<UsersSearch>("users").find().toList()

        return users
    }

    override suspend fun getUsersByUsername(username: String): List<UsersSearch> {
        val users = db.getCollection<UsersSearch>("users")
            .find(" { \"username\": { ${MongoOperator.regex}: '^$username.+|$username', ${MongoOperator.options}: 'i' } } ")
            .limit(10).toList()

        return users
    }

    override suspend fun getChats(token: String): List<FormattedChatDC> {

        val usernameByToken = db.getCollection<Token>("token").findOne(Token::id eq token)?.username

        val chatsId = db.getCollection<ChatsDC>("users").findOne("{ username: \"$usernameByToken\" }")


        val chats = mutableListOf<FormattedChatDC>()

        chatsId?.chats?.isNotEmpty()?.let {
            isTrue(it) {

                chatsId.chats.forEachSuspend { chatId ->

                    val chat = db.getCollection<ChatMainEntity>("chats").findOne(ChatMainEntity::idChat eq chatId)
                    val companion =
                        if (chat?.firstCompanion?.nameUser != usernameByToken) chat?.firstCompanion!! else chat?.secondCompanion
                    val image =
                        db.getCollection<UserMainEntity>("users").findOne(UserMainEntity::idUser eq companion?.idUser)
                            ?.image

                    val formattedItem = FormattedChatDC(
                        id = chat?.idChat!!,
                        nameChat = companion?.nameUser!!,
                        idCompanion = companion.idUser,
                        image = image!!
                    )
                    chats.add(formattedItem)
                }
            }
        }
        return chats
    }

    override suspend fun addRequestToFriend(userSender: UserNameID, idUserReceiver: String) {

        val requestsFriend =
            db.getCollection<RequestFriendDC>("requestsFriend").findOne(RequestFriendDC::id eq idUserReceiver)

        if (requestsFriend != null) {
            if (!requestsFriend.requests.contains(userSender)) {
                db.getCollection<RequestFriendDC>("requestsFriend")
                    .updateOne(RequestFriendDC::id eq idUserReceiver, addToSet(RequestFriendDC::requests, userSender))
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
        val usernameByToken = db.getCollection<Token>("token").findOne(Token::id eq token)?.username
        val user = db.getCollection<UserMainEntity>("users").findOne(UserMainEntity::username eq usernameByToken)

        return user!!
    }

    override suspend fun acceptRequestFriend(userReceiver: UserNameID, idUserSender: String) {
        val requestsFriend =
            db.getCollection<RequestFriendDC>("requestsFriend").findOne(RequestFriendDC::id eq userReceiver.id)


        if (requestsFriend != null) {
            requestsFriend.requests.forEach { userSenderL ->
                if (userSenderL.id == idUserSender) {
                    val friendCollection = db.getCollection<FriendDC>("friends")

                    val userSender = db.getCollection<UserMainEntity>("users").findOne(UserMainEntity::idUser eq idUserSender)!!

                    val formattedUserSender = UserNameID(
                        id = userSender.idUser,
                        username = userSender.username
                    )

                    val friendsReceiver = friendCollection.findOne(FriendDC::idUser eq userReceiver.id)
                    val friendsSender = friendCollection.findOne(FriendDC::idUser eq idUserSender)

                    if (friendsReceiver != null) {

                        friendCollection.updateOne(
                            FriendDC::idUser eq userReceiver.id,
                            addToSet(FriendDC::friends, formattedUserSender)
                        )

                        val requestsFriendCollection = db.getCollection<RequestFriendDC>("requestsFriend")
                        val requestsFriend =
                            requestsFriendCollection.findOne(RequestFriendDC::id eq userReceiver.id)
                        if (requestsFriend != null)
                            requestsFriendCollection.updateOne(
                                RequestFriendDC::id eq userReceiver.id,
                                pull(RequestFriendDC::requests, formattedUserSender)
                            )
                    } else {
                        val document = FriendDC(
                            idUser = userReceiver.id,
                            friends = listOf(formattedUserSender)
                        )
                        friendCollection.insertOne(document)
                        val requestsFriendCollection = db.getCollection<RequestFriendDC>("requestsFriend")
                        val requestsFriend =
                            requestsFriendCollection.findOne(RequestFriendDC::id eq userReceiver.id)
                        if (requestsFriend != null)
                            requestsFriendCollection.updateOne(
                                RequestFriendDC::id eq userReceiver.id,
                                pull(RequestFriendDC::requests, formattedUserSender)
                            )
                    }

                    if (friendsSender != null) {
                        friendCollection.updateOne(
                            FriendDC::idUser eq formattedUserSender.id,
                            addToSet(FriendDC::friends, userReceiver)
                        )
                    } else {
                        val document = FriendDC(
                            idUser = formattedUserSender.id,
                            friends = listOf(userReceiver)
                        )
                        friendCollection.insertOne(document)
                    }
                }
            }
        }
    }

    override suspend fun getFriends(token: String): List<FriendListDC> {

        val userId = getUserByToken(token)

        val friends = db.getCollection<FriendDC>("friends").findOne(FriendDC::idUser eq userId.idUser)

        if (friends != null) {
            val friendList = mutableListOf<FriendListDC>()
            friends.friends.forEachSuspend { friendDC ->
                val user = getUserByUsername(friendDC.username)
                friendList.add(
                    FriendListDC(
                        id = user.id,
                        username = user.username,
                        status = user.status,
                        image = user.image
                    )
                )
            }
            return friendList
        } else {
            return emptyList()
        }
    }

    override suspend fun getUserByUsername(username: String): UsersSearch {
        return db.getCollection<UsersSearch>("users").findOne(UsersSearch::username eq username)!!
    }

    override suspend fun getRequestsFriends(token: String): List<UsersSearch> {
        val user = getUserByToken(token)
        val requestsToFriend = db.getCollection<RequestFriendDC>("requestsFriend").findOne(UserMainEntity::idUser eq user.idUser)?.requests
        val listFormattedRequests = mutableListOf<UsersSearch>()

        requestsToFriend?.forEach { request ->
            val user = db.getCollection<UserMainEntity>("users").findOne(UserMainEntity::idUser eq request.id)!!
            val formattedUser = UsersSearch(
                id = user.idUser,
                username = user.username,
                image = user.image,
                status = user.status
            )
            listFormattedRequests.add(formattedUser)
        }

        return listFormattedRequests.toList()
    }

    override suspend fun watchForRequestsFriends(
        idUser: String,
        socket: DefaultWebSocketServerSession
    ) {
//        val pipeline = listOf(Document("\$match", Document("_id", Document("\$eq", "62333996647f736674632563a"))))

        val requestsToFriend = db.getCollection<RequestFriendDC>("requestsFriend").watch<RequestFriendDC>()

        requestsToFriend.consumeEach { item ->
            if ((item.documentKey["_id"]?.asString())?.value.equals(idUser)) {
                if (item.updateDescription != null) {

                    val updatedFields = item.updateDescription.updatedFields.toJson()
                    socket.send(Frame.Text("getRequestsFriends$updatedFields"))
                }else {
                    val json = item.fullDocument.requests.json
                    socket.send(Frame.Text("getRequestsFriends$json"))
                }
            }
        }

    }

    override suspend fun watchForFriend(idUser: String, socket: DefaultWebSocketServerSession) {
        val requestsToFriend = db.getCollection<FriendDC>("friends").watch<FriendDC>()
//        requestsToFriend.onEach { item ->
//            if ((item.documentKey["_id"]?.asString())?.value.equals(idUser)) {
//                val updatedFields = item.updateDescription.updatedFields.toJson()
//                socket.send(Frame.Text(updatedFields))
//            }
//        }.flowOn(Dispatchers.IO)

        requestsToFriend.consumeEach { item ->
            if ((item.documentKey["_id"]?.asString())?.value.equals(idUser)) {
                if (item.updateDescription != null) {
                    val updatedFields = item.updateDescription.updatedFields["friends"]?.asArray()?.values.toString()
                    socket.send(Frame.Text("getFriends$updatedFields"))
                }else {
                    val json = item.fullDocument.friends.json
                    socket.send(Frame.Text("getFriends$json"))
                }
            }
        }
    }

    override suspend fun logOut(token: String) {
        db.getCollection<Token>("token").deleteOne(Token::id eq token)
    }
}

suspend fun <T> Iterable<T>.forEachSuspend(action: suspend (T) -> Unit): Unit {
    for (element in this) action(element)
}