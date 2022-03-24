package com.foggyskies.chat.datanew

import com.foggyskies.chat.data.bettamodels.Notification
import com.foggyskies.chat.data.bettamodels.NotificationDocument
import com.foggyskies.chat.data.model.*
import com.foggyskies.chat.extendfun.forEachSuspend
import com.jetbrains.handson.chat.server.chat.data.model.ChatMessage
import com.jetbrains.handson.chat.server.chat.data.model.Token
import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.insertOne
import kotlin.streams.toList

class AllCollectionImpl(
    private val db: CoroutineDatabase
) : UsersCollectionDataSource, ChatsCollectionDataSource, FriendsCollectionDataSource,
    RequestsFriendsCollectionDataSource, TokenCollectionDataSource, MessagesCollectionDataSource,
    NotifyCollectionDataSource {
    override suspend fun checkOnExistChatByIdUsers(idUserFirst: String, idUserSecond: String): String {

        val idChat = db.getCollection<ChatMainEntity>("chats").findOne(
            and(
                or(
                    ChatMainEntity_.FirstCompanion.idUser eq idUserFirst,
                    ChatMainEntity_.FirstCompanion.idUser eq idUserSecond
                ),
                or(
                    ChatMainEntity_.SecondCompanion.idUser eq idUserSecond,
                    ChatMainEntity_.SecondCompanion.idUser eq idUserFirst
                )
            )
        )?.idChat

        return idChat ?: ""
    }

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

    suspend fun getFriendsDocumentFriendByIdUser(idUser: String): FriendDC? {
        return db.getCollection<FriendDC>("friends").findOne(FriendDC::idUser eq idUser)
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
                    val updatedFields =
                        Json.decodeFromString<List<UserNameID>>(item.updateDescription.updatedFields["friends"]?.asArray()?.values.toString())
                    println("FRIENDS 1${updatedFields.toString()}")

                    val formattedList = mutableListOf<UserIUSI>()
                    updatedFields.forEach { userNameID ->
                        val user = getUserByIdUser(userNameID.id)
                        formattedList.add(
                            UserIUSI(
                                id = user.idUser,
                                username = userNameID.username,
                                status = user.status,
                                image = user.image
                            )
                        )
                    }
                    println("FRIENDS 2${formattedList.toString()}")
                    socket.send(Frame.Text("getFriends|${formattedList.json}"))
                } else {
                    val json = Json.decodeFromString<List<UserNameID>>(item.fullDocument.friends.json)

                    val formattedList = mutableListOf<UserIUSI>()
                    json.forEach { userNameID ->
                        val user = getUserByIdUser(userNameID.id)
                        formattedList.add(
                            UserIUSI(
                                id = user.idUser,
                                username = userNameID.username,
                                status = user.status,
                                image = user.image
                            )
                        )
                    }

                    println("FRIENDS 3${json.toString()}")
                    socket.send(Frame.Text("getFriends|${formattedList.json}"))
                }
            }
        }
    }

    override suspend fun createRequestsFriendsByIdUser(idUser: String, firstRequest: UserNameID) {
        val request = RequestFriendDC(id = idUser, requests = listOf(firstRequest))
        db.getCollection<RequestFriendDC>("requestsFriend").insertOne(request)
    }

    suspend fun getRequestsDocumentFriendByIdUser(idUser: String): RequestFriendDC? {
        return db.getCollection<RequestFriendDC>("requestsFriend").findOne(RequestFriendDC::id eq idUser)
    }

    override suspend fun getRequestsFriendByIdUser(idUser: String): List<UserNameID> {
        return db.getCollection<RequestFriendDC>("requestsFriend").findOne(RequestFriendDC::id eq idUser)?.requests
            ?: emptyList()
    }

    override suspend fun addRequestFriendsByIdUser(idUser: String, newRequest: UserNameID) {
        val requests = getRequestsDocumentFriendByIdUser(idUser)
        if (requests != null)
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
                    val updatedFields =
                        Json.decodeFromString<List<UserNameID>>(item.updateDescription.updatedFields["requests"]?.asArray()?.values.toString())

                    println("REQUEST 1 $updatedFields")

                    val formattedList = mutableListOf<UserIUSI>()
                    updatedFields.forEach { userNameID ->
                        val user = getUserByIdUser(userNameID.id)
                        formattedList.add(
                            UserIUSI(
                                id = user.idUser,
                                username = userNameID.username,
                                status = user.status,
                                image = user.image
                            )
                        )
                    }
                    println("REQUEST 2 $formattedList")

                    socket.send(Frame.Text("getRequestsFriends|${formattedList.json}"))
                } else {
                    val json = Json.decodeFromString<List<UserNameID>>(item.fullDocument.requests.json)

                    val formattedList = mutableListOf<UserIUSI>()
                    json.forEach { userNameID ->
                        val user = getUserByIdUser(userNameID.id)
                        formattedList.add(
                            UserIUSI(
                                id = user.idUser,
                                username = userNameID.username,
                                status = user.status,
                                image = user.image
                            )
                        )
                    }
                    println("REQUEST 3 $formattedList")
                    socket.send(Frame.Text("getRequestsFriends|${formattedList.json}"))
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

    override suspend fun searchUsers(idUser: String, username: String): List<UsersSearch> {
        val users = db.getCollection<UserMainEntity>("users")
            .find(" { \"username\": { ${MongoOperator.regex}: '^$username.+|$username', ${MongoOperator.options}: 'i' } } ")
            .limit(10).toList()

        val listUsersSearch = mutableListOf<UsersSearch>()

        users.forEachSuspend { user ->
            val isFriend = db.getCollection<FriendDC>("friends").findOne(FriendDC::idUser eq idUser)?.friends?.contains(
                UserNameID(
                    id = user.idUser,
                    username = user.username
                )
            ) ?: false
            var awaitAccept = false
            db.getCollection<RequestFriendDC>("requestsFriend")
                .findOne(RequestFriendDC::id eq user.idUser)?.requests?.forEach {
                    if (it.id == idUser) {
                        awaitAccept = true
                        return@forEach
                    }
                }
            listUsersSearch.add(
                UsersSearch(
                    id = user.idUser,
                    username = user.username,
                    status = user.status,
                    image = user.image,
                    isFriend = isFriend,
                    awaitAccept = awaitAccept
                )
            )
        }

        return listUsersSearch
    }

    override suspend fun addChatToUsersByIdUsers(idUserFirst: String, idUserSecond: String, idChat: String) {
        db.getCollection<UserMainEntity>("users").updateMany(
            or(UserMainEntity::idUser eq idUserFirst, UserMainEntity::idUser eq idUserSecond),
            addToSet(UserMainEntity::chats, idChat)
        )
    }

    override suspend fun setStatusUser(idUser: String, status: String) {
        db.getCollection<UserMainEntity>("users")
            .findOneAndUpdate(UserMainEntity::idUser eq idUser, setValue(UserMainEntity::status, status))
    }

    override suspend fun checkOnExistEmail(e_mail: String): Boolean {
        return db.getCollection<UserMainEntity>("Users").findOne(UserMainEntity::e_mail eq e_mail) != null
    }

    override suspend fun checkPasswordOnCorrect(username: String, password: String): Boolean {
        return db.getCollection<UserMainEntity>("users")
            .findOne(and(UserMainEntity::username eq username, UserMainEntity::password eq password)) != null
    }

    override suspend fun checkOnExistUser(username: String): Boolean {
        return db.getCollection<UserMainEntity>("users").findOne(UserMainEntity::username eq username) != null
    }

    override suspend fun insertOne(idChat: String, message: ChatMessage) {
        db.getCollection<ChatMessage>("messages-$idChat").insertOne(message)
    }

    override suspend fun getAllMessages(idChat: String): List<ChatMessage> {
        return db.getCollection<ChatMessage>("messages-$idChat").find().toList()
    }

    override suspend fun getFiftyMessage(idChat: String): List<ChatMessage> {
        return db.getCollection<ChatMessage>("messages-$idChat").find().sort("{ \$natural: 1 }".bson).limit(50).toList()
    }

    override suspend fun getLastMessage(idChat: String): String {
        return db.getCollection<ChatMessage>("messages-$idChat").find().sort("{ \$natural: -1 }".bson).limit(1)
            .first()?.message ?: ""
    }

    override suspend fun getNotification(id: String): Notification? {
        return db.getCollection<Notification>("notification").findOne(Notification::id eq id)
    }

    override suspend fun watchForNotification(idUser: String, socket: DefaultWebSocketServerSession) {
        val watcher = db.getCollection<NotificationDocument>("notifications")
            .watch<NotificationDocument>()

        val oldMutableList = mutableListOf<Notification>()
        println("Срабатываю 1 раз!!")
        oldMutableList.addAll(db.getCollection<NotificationDocument>("notifications").findOne(NotificationDocument::id eq "63cf0a3e88c4a08c2842c08")?.notifications!!)

        watcher.consumeEach { item ->
            if ((item.documentKey["_id"]?.asString())?.value.equals("63cf0a3e88c4a08c2842c08")) {
                if (item.updateDescription != null) {
                    val updatedFields =
                        Json.decodeFromString<List<UserNameID>>(item.updateDescription.updatedFields["notifications"]?.asArray()?.values.toString())
                    println(updatedFields)
                } else {
                    val json = Json.decodeFromString<List<Notification>>(item.fullDocument.notifications.json)
                    if (json.size > oldMutableList.size){
                        json.forEach { notification ->
                            if (!oldMutableList.contains(notification)){
                                val jjson = Json.encodeToString(notification)
                                socket.send(jjson)
                            }
                        }
                    }
                    println(json)
//                    socket.send(json.json)
                }
            }
        }
    }
}