package com.foggyskies.chat.datanew

import com.foggyskies.chat.data.model.*
import com.jetbrains.handson.chat.server.chat.data.model.Token
import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.insertOne

class AllCollectionImpl(
    private val db: CoroutineDatabase
) : UsersCollectionDataSource, ChatsCollectionDataSource, FriendsCollectionDataSource,
    RequestsFriendsCollectionDataSource, TokenCollectionDataSource {
    override suspend fun getChatById(idChat: String): ChatMainEntity {
        return db.getCollection<ChatMainEntity>("chats").findOne(ChatMainEntity::idChat eq idChat)!!
    }

    override suspend fun createChat(firstCompanion: UserNameID, secondCompanion: UserNameID): String {
        val document = ChatMainEntity(
            idChat = ObjectId().toString(),
            firstCompanion = ChatUserEntity(idUser = firstCompanion.id, nameUser = firstCompanion.username),
            secondCompanion = ChatUserEntity(idUser = secondCompanion.id, nameUser = secondCompanion.username)
        )
        db.getCollection<ChatMainEntity>("chats").insertOne(document)
        return document.idChat
    }

    override suspend fun getFriendsByIdUser(idUser: String): List<UserNameID> {
        return db.getCollection<FriendDC>("friends").findOne(FriendDC::idUser eq idUser)?.friends ?: emptyList()
    }

    override suspend fun createFriendsDocument(idUser: String, firstFriend: UserNameID) {
        val document = FriendDC(idUser = idUser, friends = listOf(firstFriend))
        db.getCollection<FriendDC>("friends").insertOne(document)
    }

    override suspend fun addFriendByIdUser(idUser: String, newFriend: UserNameID) {
        db.getCollection<FriendDC>("friends")
            .updateOne(FriendDC::idUser eq idUser, addToSet(FriendDC::friends, newFriend))
    }

    override suspend fun delFriendByIdUser(idUser: String, delFriend: UserNameID) {
        db.getCollection<FriendDC>("friends")
            .updateOne(FriendDC::idUser eq idUser, pull(FriendDC::friends, delFriend))
    }

    override suspend fun watchForFriend(idUser: String, socket: DefaultWebSocketServerSession) {
        val requestsToFriend = db.getCollection<FriendDC>("friends").watch<FriendDC>()

        requestsToFriend.consumeEach { item ->
            if ((item.documentKey["_id"]?.asString())?.value.equals(idUser)) {
                if (item.updateDescription != null) {
                    val updatedFields = item.updateDescription.updatedFields["friends"]?.asArray()?.values.toString()
                    socket.send(Frame.Text("getFriends$updatedFields"))
                } else {
                    val json = item.fullDocument.friends.json
                    socket.send(Frame.Text("getFriends$json"))
                }
            }
        }
    }

    override suspend fun createRequestsFriendsByIdUser(idUser: String, firstRequest: UserNameID) {
        val request = RequestFriendDC(id = idUser, requests = listOf(firstRequest))
        db.getCollection<RequestFriendDC>("requestsFriend").insertOne(request)
    }

    override suspend fun getRequestsFriendByIdUser(idUser: String): List<UserNameID> {
        return db.getCollection<RequestFriendDC>("requestsFriend").findOne(RequestFriendDC::id eq idUser)?.requests
            ?: emptyList()
    }

    override suspend fun addRequestFriendsByIdUser(idUser: String, newRequest: UserNameID) {
        val requests = getRequestsFriendByIdUser(idUser)
        if (requests.isNotEmpty())
            db.getCollection<RequestFriendDC>("requestsFriend")
                .updateOne(RequestFriendDC::id eq idUser, addToSet(RequestFriendDC::requests, newRequest))
        else
            createRequestsFriendsByIdUser(idUser, newRequest)
    }

    override suspend fun delRequestFriendsByIdUser(idUser: String, delRequest: UserNameID) {
        db.getCollection<RequestFriendDC>("requestsFriend")
            .updateOne(RequestFriendDC::id eq idUser, pull(RequestFriendDC::requests, delRequest))
    }

    override suspend fun watchForRequestsFriends(idUser: String, socket: DefaultWebSocketServerSession) {
        val requestsToFriend = db.getCollection<RequestFriendDC>("requestsFriend").watch<RequestFriendDC>()

        requestsToFriend.consumeEach { item ->
            if ((item.documentKey["_id"]?.asString())?.value.equals(idUser)) {
                if (item.updateDescription != null) {

                    val updatedFields = item.updateDescription.updatedFields.toJson()
                    socket.send(Frame.Text("getRequestsFriends$updatedFields"))
                } else {
                    val json = item.fullDocument.requests.json
                    socket.send(Frame.Text("getRequestsFriends$json"))
                }
            }
        }
    }

    override suspend fun createToken(user: UserNameID) {
        val token = Token(
            idUser = user.id,
            username = user.username
        )
        db.getCollection<Token>("tokens").insertOne(token)
    }

    override suspend fun delTokenByTokenId(idToken: String) {
        db.getCollection<Token>("tokens").deleteOne(Token::id eq idToken)
    }

    override suspend fun checkOnExistToken(token: String): Boolean {
        return db.getCollection<Token>("tokens").findOne(Token::id eq token) != null
    }

    override suspend fun getTokenByToken(token: String): Token {
        return db.getCollection<Token>("tokens").findOneById(token)!!
    }

    override suspend fun getTokenByUsername(username: String): Token {
        return db.getCollection<Token>("tokens").findOne(Token::username eq username)!!
    }

    override suspend fun getUsers(): List<UserMainEntity> {
        return db.getCollection<UserMainEntity>("users").find().toList()
    }

    override suspend fun getUserByUsername(username: String): UserMainEntity {
        return db.getCollection<UserMainEntity>("users").findOne(UserMainEntity::username eq username)!!
    }

    override suspend fun getUserByIdUser(idUser: String): UserMainEntity {
        return db.getCollection<UserMainEntity>("users").findOne(UserMainEntity::idUser eq idUser)!!
    }

    override suspend fun getChatsByIdUser(idUser: String): List<String> {
        return db.getCollection<UserMainEntity>("users").findOne(UserMainEntity::idUser eq idUser)?.chats ?: emptyList()
    }

    override suspend fun createUser(registrationUserDC: RegistrationUserDC) {
        val user = UserMainEntity(
            username = registrationUserDC.username,
            password = registrationUserDC.password,
            e_mail = registrationUserDC.e_mail,
        )
        db.getCollection<UserMainEntity>("users").insertOne(user)
    }

    override suspend fun searchUsers(username: String): List<UsersSearch> {
        val users = db.getCollection<UsersSearch>("users")
            .find(" { \"username\": { ${MongoOperator.regex}: '^$username.+|$username', ${MongoOperator.options}: 'i' } } ")
            .limit(10).toList()

        return users
    }
}