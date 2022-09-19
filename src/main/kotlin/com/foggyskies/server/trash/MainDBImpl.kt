//package com.foggyskies.server.databases.mongo.main
//
//import com.foggyskies.server.data.bettamodels.Notification
//import com.foggyskies.server.data.bettamodels.NotificationDocument
//import com.foggyskies.server.data.model.ChatUserEntity
//import com.foggyskies.server.data.model.ChatUserEntity_
////import com.foggyskies.server.databases.main.models.*
//import com.foggyskies.server.databases.mongo.main.datasources.*
//import com.foggyskies.server.databases.mongo.main.models.*
//import com.foggyskies.server.extendfun.forEachSuspend
//import io.ktor.server.websocket.*
//import io.ktor.websocket.*
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//import org.bson.types.ObjectId
//import org.litote.kmongo.*
//import org.litote.kmongo.coroutine.CoroutineDatabase
//
////MessagesCollectionDataSource
//class MainDBImpl(
//    private val db: CoroutineDatabase
//) : UsersCollectionDataSource, ChatsCollectionDataSource, FriendsCollectionDataSource,
//    RequestsFriendsCollectionDataSource, TokenCollectionDataSource,
//    NotifyCollectionDataSource, PagesProfileDataSource, AvatarsCollectionDataSource, BlockedUsersCollectionDataSource {
//    override suspend fun checkOnExistChatByIdUsers(idUserFirst: String, idUserSecond: String): String {
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
//        )?.idChat
//        return idChat ?: ""
//    }
//
//    override suspend fun getChatById(idChat: String): ChatMainEntity {
//        return db.getCollection<ChatMainEntity>("chats").findOne(ChatMainEntity::idChat eq idChat)!!
//    }
//
//    override suspend fun createChat(firstCompanion: UserNameID, secondCompanion: UserNameID): String {
//        val document = ChatMainEntity(
//            idChat = ObjectId().toString(),
//            firstCompanion = ChatUserEntity(idUser = firstCompanion.id),
//            secondCompanion = ChatUserEntity(idUser = secondCompanion.id)
//        )
//        db.getCollection<ChatMainEntity>("chats").insertOne(document)
//        return document.idChat
//    }
//
//    override suspend fun muteChat(
//        idChat: String,
//        idUser: String,
//        nameField: ChatUserEntity_<ChatMainEntity>,
//        time: String
//    ) {
//        db.getCollection<ChatMainEntity>("chats")
//            .findOneAndUpdate(
//                ChatMainEntity::idChat eq idChat, setValue(
//                    nameField.notifiable, ""
//                )
//            )
//    }
//
//
//    suspend fun getFriendsDocumentFriendByIdUser(idUser: String): FriendDC? {
//        return db.getCollection<FriendDC>("friends").findOne(FriendDC::idUser eq idUser)
//    }
//
//    override suspend fun getFriendsByIdUser(idUser: String): List<> {
//        return db.getCollection<FriendDC>("friends").findOne(FriendDC::idUser eq idUser)?.friends ?: emptyList()
//    }
//
//    override suspend fun createFriendsDocument(idUser: String, firstFriend: UserNameID) {
//        val document = FriendDC(idUser = idUser, friends = listOf(firstFriend))
//        db.getCollection<FriendDC>("friends").insertOne(document)
//    }
//
//    override suspend fun addFriendByIdUser(idUser: String, newFriend: UserNameID) {
//        db.getCollection<FriendDC>("friends")
//            .updateOne(FriendDC::idUser eq idUser, addToSet(FriendDC::friends, newFriend))
//    }
//
//    override suspend fun delFriendByIdUser(idUser: String, delFriend: UserNameID) {
//        db.getCollection<FriendDC>("friends")
//            .updateOne(FriendDC::idUser eq idUser, pull(FriendDC::friends, delFriend))
//    }
//
//    override suspend fun watchForFriend(idUser: String, socket: DefaultWebSocketServerSession) {
//        val requestsToFriend = db.getCollection<FriendDC>("friends").watch<FriendDC>()
//
//        requestsToFriend.consumeEach { item ->
//            if ((item.documentKey["_id"]?.asString())?.value.equals(idUser)) {
//                if (item.updateDescription != null) {
//                    val updatedFields =
//                        Json.decodeFromString<List<UserNameID>>(item.updateDescription.updatedFields["friends"]?.asArray()?.values.toString())
//                    println("FRIENDS 1${updatedFields.toString()}")
//
//                    val formattedList = mutableListOf<UserIUSI>()
//                    updatedFields.forEach { userNameID ->
//                        val user = getUserByIdUser(userNameID.id)
//                        formattedList.add(
//                            UserIUSI(
//                                id = user.idUser,
//                                username = userNameID.username,
//                                status = user.status,
//                                image = "user.image"
//                            )
//                        )
//                    }
//                    println("FRIENDS 2${formattedList.toString()}")
//                    socket.send(Frame.Text("getFriends|${formattedList.json}"))
//                } else {
//                    val json = Json.decodeFromString<List<UserNameID>>(item.fullDocument.friends.json)
//
//                    val formattedList = mutableListOf<UserIUSI>()
//                    json.forEach { userNameID ->
//                        val user = getUserByIdUser(userNameID.id)
//                        formattedList.add(
//                            UserIUSI(
//                                id = user.idUser,
//                                username = userNameID.username,
//                                status = user.status,
//                                image = "user.image"
//                            )
//                        )
//                    }
//
//                    println("FRIENDS 3${json.toString()}")
//                    socket.send(Frame.Text("getFriends|${formattedList.json}"))
//                }
//            }
//        }
//    }
//
//    override suspend fun createRequestsFriendsByIdUser(idUser: String, firstRequest: UserNameID) {
//        val request = RequestFriendDC(id = idUser, requests = listOf(firstRequest))
//        db.getCollection<RequestFriendDC>("requestsFriend").insertOne(request)
//    }
//
//    suspend fun getRequestsDocumentFriendByIdUser(idUser: String): RequestFriendDC? {
//        return db.getCollection<RequestFriendDC>("requestsFriend").findOne(RequestFriendDC::id eq idUser)
//    }
//
//    override suspend fun getRequestsFriendByIdUser(idUser: String): List<UserNameID> {
//        return db.getCollection<RequestFriendDC>("requestsFriend").findOne(RequestFriendDC::id eq idUser)?.requests
//            ?: emptyList()
//    }
//
//    override suspend fun addRequestFriendsByIdUser(idUser: String, newRequest: UserNameID) {
//        val requests = getRequestsDocumentFriendByIdUser(idUser)
//        if (requests != null)
//            db.getCollection<RequestFriendDC>("requestsFriend")
//                .updateOne(RequestFriendDC::id eq idUser, addToSet(RequestFriendDC::requests, newRequest))
//        else
//            createRequestsFriendsByIdUser(idUser, newRequest)
//    }
//
//    override suspend fun delRequestFriendsByIdUser(idUser: String, delRequest: UserNameID) {
//        db.getCollection<RequestFriendDC>("requestsFriend")
//            .updateOne(RequestFriendDC::id eq idUser, pull(RequestFriendDC::requests, delRequest))
//    }
//
//    override suspend fun watchForRequestsFriends(idUser: String, socket: DefaultWebSocketServerSession) {
//        val requestsToFriend = db.getCollection<RequestFriendDC>("requestsFriend").watch<RequestFriendDC>()
//
//        requestsToFriend.consumeEach { item ->
//            if ((item.documentKey["_id"]?.asString())?.value.equals(idUser)) {
//                if (item.updateDescription != null) {
//                    val updatedFields =
//                        Json.decodeFromString<List<UserNameID>>(item.updateDescription.updatedFields["requests"]?.asArray()?.values.toString())
//
//                    println("REQUEST 1 $updatedFields")
//
//                    val formattedList = mutableListOf<UserIUSI>()
//                    updatedFields.forEach { userNameID ->
//                        val user = getUserByIdUser(userNameID.id)
//                        formattedList.add(
//                            UserIUSI(
//                                id = user.idUser,
//                                username = userNameID.username,
//                                status = user.status,
//                                image = "user.image"
//                            )
//                        )
//                    }
//                    println("REQUEST 2 $formattedList")
//
//                    socket.send(Frame.Text("getRequestsFriends|${formattedList.json}"))
//                } else {
//                    val json = Json.decodeFromString<List<UserNameID>>(item.fullDocument.requests.json)
//
//                    val formattedList = mutableListOf<UserIUSI>()
//                    json.forEach { userNameID ->
//                        val user = getUserByIdUser(userNameID.id)
//                        formattedList.add(
//                            UserIUSI(
//                                id = user.idUser,
//                                username = userNameID.username,
//                                status = user.status,
//                                image = "user.image"
//                            )
//                        )
//                    }
//                    println("REQUEST 3 $formattedList")
//                    socket.send(Frame.Text("getRequestsFriends|${formattedList.json}"))
//                }
//            }
//        }
//    }
//
//    override suspend fun createToken(user: UserNameID) {
//        val token = Token(
//            idUser = user.id,
//            username = user.username
//        )
//        db.getCollection<Token>("tokens").insertOne(token)
//    }
//
//    override suspend fun delTokenByTokenId(idToken: String) {
//        db.getCollection<Token>("tokens").deleteOne(Token::id eq idToken)
//    }
//
//    override suspend fun checkOnExistToken(token: String): Boolean {
//        return db.getCollection<Token>("tokens").findOne(Token::id eq token) != null
//    }
//
//    override suspend fun checkOnExistTokenByUsername(username: String): Boolean {
//        return db.getCollection<Token>("tokens").findOne(Token::username eq username) != null
//    }
//
//    override suspend fun getTokenByToken(token: String): Token {
//        return db.getCollection<Token>("tokens").findOneById(token)!!
//    }
//
//    override suspend fun getTokenByUsername(username: String): Token {
//        return db.getCollection<Token>("tokens").findOne(Token::username eq username)!!
//    }
//
//    override suspend fun deleteTokenByIdUser(idUser: String) {
//        db.getCollection<Token>("tokens").deleteOne(Token::idUser eq idUser)
//    }
//
//    override suspend fun getUsers(): List<UserMainEntity> {
//        return db.getCollection<UserMainEntity>("users").find().toList()
//    }
//
//    override suspend fun getUserByUsername(username: String): UserMainEntity? {
//        return db.getCollection<UserMainEntity>("users").findOne(UserMainEntity::username eq username)
//    }
//
//    override suspend fun getUserByIdUser(idUser: String): UserMainEntity {
//        return db.getCollection<UserMainEntity>("users").findOne(UserMainEntity::idUser eq idUser)!!
//    }
//
//    override suspend fun getChatsByIdUser(idUser: String): List<String> {
//        return db.getCollection<UserMainEntity>("users").findOne(UserMainEntity::idUser eq idUser)?.chats ?: emptyList()
//    }
//
//    override suspend fun createUser(registrationUserDC: RegistrationUserDC) {
//        val user = UserMainEntity(
//            username = registrationUserDC.username,
//            password = registrationUserDC.password,
//            e_mail = registrationUserDC.e_mail,
//            status = "Не в сети",
//        )
//        val avatar = AvatarDC(
//            idUser = user.idUser,
//            image = ""
//        )
//        db.getCollection<AvatarDC>("avatars").insertOne(avatar)
//        db.getCollection<UserMainEntity>("users").insertOne(user)
//    }
//
//    override suspend fun searchUsers(idUser: String, username: String): List<UsersSearch> {
//        val users = db.getCollection<UserMainEntity>("users")
//            .find(" { \"username\": { ${MongoOperator.regex}: '^$username.+|$username', ${MongoOperator.options}: 'i' } } ")
//            .limit(10).toList()
//
//        val listUsersSearch = mutableListOf<UsersSearch>()
//
//        users.forEachSuspend { user ->
//            val isFriend = db.getCollection<FriendDC>("friends").findOne(FriendDC::idUser eq idUser)?.friends?.contains(
//                UserNameID(
//                    id = user.idUser,
//                    username = user.username
//                )
//            ) ?: false
//            var awaitAccept = false
//            db.getCollection<RequestFriendDC>("requestsFriend")
//                .findOne(RequestFriendDC::id eq user.idUser)?.requests?.forEach {
//                    if (it.id == idUser) {
//                        awaitAccept = true
//                        return@forEach
//                    }
//                }
//            listUsersSearch.add(
//                UsersSearch(
//                    id = user.idUser,
//                    username = user.username,
//                    status = user.status,
//                    image = "user.image",
//                    isFriend = isFriend,
//                    awaitAccept = awaitAccept
//                )
//            )
//        }
//
//        return listUsersSearch
//    }
//
//    override suspend fun addChatToUsersByIdUsers(idUserFirst: String, idUserSecond: String, idChat: String) {
//        db.getCollection<UserMainEntity>("users").updateMany(
//            or(UserMainEntity::idUser eq idUserFirst, UserMainEntity::idUser eq idUserSecond),
//            addToSet(UserMainEntity::chats, idChat)
//        )
//    }
//
//    override suspend fun setStatusUser(idUser: String, status: String) {
//        db.getCollection<UserMainEntity>("users")
//            .findOneAndUpdate(UserMainEntity::idUser eq idUser, setValue(UserMainEntity::status, status))
//    }
//
//    override suspend fun checkOnExistEmail(e_mail: String): Boolean {
//        return db.getCollection<UserMainEntity>("users").findOne(UserMainEntity::e_mail eq e_mail) != null
//    }
//
////    override suspend fun checkPasswordOnCorrect(username: String, password: String): Boolean {
////        return db.getCollection<UserMainEntity>("users")
////            .findOne(UserMainEntity::username eq username)
////    }
//
//    override suspend fun checkOnExistUser(username: String): Boolean {
//        return db.getCollection<UserMainEntity>("users").findOne(UserMainEntity::username eq username) != null
//    }
//
//    override suspend fun getStatusByIdUser(idUser: String): String {
//        return db.getCollection<UserMainEntity>("users").findOne(UserMainEntity::idUser eq idUser)?.status!!
//    }
//
//    override suspend fun lockUser(idUser: String, value: Boolean) {
//        db.getCollection<UserMainEntity>("users")
//            .updateOne(UserMainEntity::idUser eq idUser, setValue(UserMainEntity::isLocked, value))
//    }
//
////    override suspend fun insertOne(idChat: String, message: ChatMessage) {
////        db.getCollection<ChatMessage>("messages-$idChat").insertOne(message)
////    }
////
////    override suspend fun getAllMessages(idChat: String): List<ChatMessage> {
////        return db.getCollection<ChatMessage>("messages-$idChat").find().toList()
////    }
////
////    override suspend fun getFiftyMessage(idChat: String): List<ChatMessage> {
////        return db.getCollection<ChatMessage>("messages-$idChat").find().sort("{ \$natural: -1 }".bson).limit(50)
////            .toList().reversed()
////    }
////
////    override suspend fun getLastMessage(idChat: String): String {
////        return db.getCollection<ChatMessage>("messages-$idChat").find().sort("{ \$natural: -1 }".bson).limit(1)
////            .first()?.message ?: ""
////    }
//
//    override suspend fun checkOnExistNotificationDocument(idReceiver: String): Boolean {
//        return db.getCollection<NotificationDocument>("notifications")
//            .findOne(NotificationDocument::id eq idReceiver) != null
//    }
//
//    override suspend fun createNotificationDocument(idReceiver: String, notification: Notification) {
//        db.getCollection<NotificationDocument>("notifications")
//            .insertOne(NotificationDocument(idReceiver, listOf(notification)))
//    }
//
//    override suspend fun addNotification(idReceiver: String, notification: Notification) {
//        db.getCollection<NotificationDocument>("notifications").findOneAndUpdate(
//            NotificationDocument::id eq idReceiver,
//            addToSet(NotificationDocument::notifications, notification)
//        )
//    }
//
//    override suspend fun getNotification(id: String): Notification? {
//        return db.getCollection<Notification>("notification").findOne(Notification::id eq id)
//    }
//
//    override suspend fun watchForNotification(idUser: String, socket: DefaultWebSocketServerSession) {
//        val isExist = checkOnExistNotificationDocument(idUser)
//        if (!isExist) {
//            val item = NotificationDocument(
//                id = idUser,
//                notifications = emptyList()
//            )
//            db.getCollection<NotificationDocument>("notifications").insertOne(item)
//        } else {
//            val watcher = db.getCollection<NotificationDocument>("notifications")
//                .watch<NotificationDocument>()
//
//            val oldMutableList = mutableListOf<Notification>()
//            println("Срабатываю 1 раз!!")
//            oldMutableList.addAll(
//                db.getCollection<NotificationDocument>("notifications")
//                    .findOne(NotificationDocument::id eq idUser)?.notifications!!
//            )
//
//            watcher.consumeEach { item ->
//                if ((item.documentKey["_id"]?.asString())?.value.equals(idUser)) {
//                    if (item.updateDescription != null) {
//                        val updatedFields =
//                            Json.decodeFromString<List<Notification>>(item.updateDescription.updatedFields["notifications"]?.asArray()?.values.toString())
//                        if (updatedFields.isEmpty())
//                            oldMutableList.clear()
//                        if (updatedFields.size > oldMutableList.size) {
//                            updatedFields.forEach { notification ->
//                                if (!oldMutableList.contains(notification)) {
//                                    oldMutableList.add(notification)
//                                    val jjson = Json.encodeToString(notification)
//                                    println("ПЕРВЫЙ ПУНКТ $updatedFields")
//                                    socket.send(jjson)
//                                }
//                            }
//                        }
//                    } else {
//                        val json = Json.decodeFromString<List<Notification>>(item.fullDocument.notifications.json)
//                        if (json.isEmpty())
//                            oldMutableList.clear()
//                        if (json.size > oldMutableList.size) {
//                            json.forEach { notification ->
//                                if (!oldMutableList.contains(notification)) {
//                                    oldMutableList.add(notification)
//                                    val jjson = Json.encodeToString(notification)
//                                    println("ВТОРОЙ ПУНКТ $jjson")
//                                    socket.send(jjson)
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    override suspend fun deleteAllSentNotifications(idUser: String) {
//        db.getCollection<NotificationDocument>("notifications").findOneAndUpdate(
//            NotificationDocument::id eq idUser,
//            setValue(NotificationDocument::notifications, emptyList())
//        )
//    }
//
//    override suspend fun checkOnExistInternalNotificationDocument(idReceiver: String): Boolean {
//        return db.getCollection<NotificationDocument>("internal_notifications")
//            .findOne(NotificationDocument::id eq idReceiver) != null
//    }
//
//    override suspend fun createInternalNotificationDocument(idReceiver: String, notification: Notification) {
//        db.getCollection<NotificationDocument>("internal_notifications")
//            .insertOne(NotificationDocument(idReceiver, listOf(notification)))
//    }
//
//    override suspend fun addInternalNotification(idReceiver: String, notification: Notification) {
//        db.getCollection<NotificationDocument>("internal_notifications").findOneAndUpdate(
//            NotificationDocument::id eq idReceiver,
//            addToSet(NotificationDocument::notifications, notification)
//        )
//    }
//
//    override suspend fun watchForInternalNotifications(idUser: String, socket: DefaultWebSocketServerSession) {
//
//        val watcher = db.getCollection<NotificationDocument>("internal_notifications")
//            .watch<NotificationDocument>()
//
//        watcher.consumeEach { item ->
//            if ((item.documentKey["_id"]?.asString())?.value.equals(idUser)) {
//                if (item.operationType.value != "replace") {
//                    if (item.updateDescription != null) {
//                        println("Первое")
//                        val json = item.updateDescription.updatedFields["notifications"]?.asArray()?.last()?.json!!
//                        val item = Json.decodeFromString<Notification>(json)
//                        db.getCollection<NotificationDocument>("internal_notifications").findOneAndUpdate(
//                            NotificationDocument::id eq idUser,
//                            pull(NotificationDocument::notifications, item)
//                        )
//                        socket.send("getInternalNotification|$json")
//                    } else {
//                        println("Второе")
//                        val json = item.fullDocument.notifications[0].json
//                        socket.send("getInternalNotification|$json")
//                    }
//                }
//            }
//        }
//    }
//
//    override suspend fun addOnePage(item: PageProfileDC) {
//        db.getCollection<PageProfileDC>("pages_profile").insertOne(item)
//    }
//
//    override suspend fun getPageById(idPage: String): PageProfileDC? {
//        return db.getCollection<PageProfileDC>("pages_profile").findOne(PageProfileDC::id eq idPage)
//    }
//
//    override suspend fun getAllPagesByList(listIds: List<String>): List<PageProfileDC> {
//        return db.getCollection<PageProfileDC>("pages_profile").find().toList()
//    }
//
//    override suspend fun deletePage(idPage: String) {
//        db.getCollection<PageProfileDC>("pages_profile").deleteOne(PageProfileDC::id eq idPage)
//    }
//
//    override suspend fun getAvatarPageProfile(idPage: String): String {
//        return db.getCollection<PageProfileDC>("pages_profile").findOne(PageProfileDC::id eq idPage)?.image ?: ""
//    }
//
//    override suspend fun changeAvatarByIdPage(idPage: String, pathToImage: String): String {
//        db.getCollection<PageProfileDC>("pages_profile")
//            .findOneAndUpdate(PageProfileDC::id eq idPage, setValue(PageProfileDC::image, pathToImage))?.image
//        return pathToImage
//    }
//
//    override suspend fun getAvatarByIdUser(idUser: String): String {
//        return db.getCollection<AvatarDC>("avatars").findOne(AvatarDC::idUser eq idUser)?.image ?: ""
//    }
//
//    override suspend fun changeAvatarByUserId(idUser: String, pathToImage: String): String {
//        db.getCollection<AvatarDC>("avatars")
//            .findOneAndUpdate(AvatarDC::idUser eq idUser, setValue(AvatarDC::image, pathToImage))?.image
//        return pathToImage
//    }
//
//    override suspend fun giveBlockByIdUser(idUser: String, block: BlockUserDC) {
//        db.getCollection<BlockUserDC>("blocked_users").insertOne(block)
//    }
//}