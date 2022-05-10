package com.foggyskies.chat.newroom

import com.foggyskies.chat.data.model.*
import com.foggyskies.chat.databases.content.ContentImpl
import com.foggyskies.chat.databases.main.MainDBImpl
import com.foggyskies.chat.databases.message.MessagesDBImpl
import com.foggyskies.chat.databases.newmessage.NewMessagesDBImpl
import com.foggyskies.chat.databases.subscribers.SubscribersImpl
import com.jetbrains.handson.chat.server.chat.data.model.Token
import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch
import io.ktor.websocket.*
import org.litote.kmongo.addToSet
import org.litote.kmongo.eq
import java.io.File

class UserRoutController(
    private val content: ImpAndDB<ContentImpl>,
    private val main: ImpAndDB<MainDBImpl>,
    private val message: ImpAndDB<MessagesDBImpl>,
    private val subscribers: ImpAndDB<SubscribersImpl>,
    private val new_messages: ImpAndDB<NewMessagesDBImpl>
    ) {
    suspend fun checkOnExistToken(token: String): Boolean {
        return main.impl.checkOnExistToken(token)
    }

    suspend fun getChats(token: String): List<FormattedChatDC> {
        val idUser = main.db.getCollection<Token>("tokens").findOneById(token)?.idUser!!
        val idsChats = main.impl.getChatsByIdUser(idUser)
        val listChats = mutableListOf<FormattedChatDC>()

        idsChats.forEach { id ->
            val chat = main.impl.getChatById(id)
            val companion = if (idUser == chat.firstCompanion?.idUser) chat.secondCompanion!! else chat.firstCompanion!!
            val imageComp = main.impl.getAvatarByIdUser(companion.idUser)
            val lastMessage = message.impl.getLastMessage(id)

            listChats.add(
                FormattedChatDC(
                    id = chat.idChat,
                    nameChat = companion.nameUser,
                    idCompanion = companion.idUser,
                    image = imageComp,
                    lastMessage = lastMessage
                )
            )
        }
        return listChats
    }

    suspend fun addRequestToFriend(userSender: UserNameID, idUserReceiver: String) {
        main.impl.addRequestFriendsByIdUser(idUserReceiver, userSender)
    }

    suspend fun getUserByToken(token: String): UserMainEntity {
        val username = main.impl.getTokenByToken(token).username
        return main.impl.getUserByUsername(username)
    }

    suspend fun getUserByIdUser(idUser: String): UserMainEntity {
        return main.impl.getUserByIdUser(idUser)
    }

    suspend fun acceptRequestFriend(userReceiver: UserNameID, idUserSender: String) {
        val requestsFriend = main.impl.getRequestsFriendByIdUser(userReceiver.id)


        if (requestsFriend.isNotEmpty()) {
            requestsFriend.forEach { userSenderL ->
                if (userSenderL.id == idUserSender) {
                    val friendCollection = main.db.getCollection<FriendDC>("friends")

                    val userSender = main.impl.getUserByIdUser(idUserSender)

                    val formattedUserSender = UserNameID(
                        id = userSender.idUser,
                        username = userSender.username
                    )

                    val friendsReceiver = main.impl.getFriendsDocumentFriendByIdUser(userReceiver.id)
                    val friendsSender = main.impl.getFriendsDocumentFriendByIdUser(idUserSender)

                    if (friendsReceiver != null) {

                        friendCollection.updateOne(
                            FriendDC::idUser eq userReceiver.id,
                            addToSet(FriendDC::friends, formattedUserSender)
                        )

                        val requestsFriend = main.impl.getRequestsFriendByIdUser(userReceiver.id)
                        if (requestsFriend.isNotEmpty())
                            main.impl.delRequestFriendsByIdUser(userReceiver.id, formattedUserSender)
                    } else {
                        val document = FriendDC(
                            idUser = userReceiver.id,
                            friends = listOf(formattedUserSender)
                        )
                        friendCollection.insertOne(document)
                        val requestsFriend = main.impl.getRequestsFriendByIdUser(userReceiver.id)
                        if (requestsFriend.isNotEmpty())
                            main.impl.delRequestFriendsByIdUser(userReceiver.id, formattedUserSender)
                    }

                    if (friendsSender != null) {
                        main.impl.addFriendByIdUser(formattedUserSender.id, userReceiver)
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

    suspend fun getFriends(token: String): List<FriendListDC> {
        val idUser = main.impl.getTokenByToken(token).idUser
        val friends = main.impl.getFriendsByIdUser(idUser)
        val listFormattedFriends = mutableListOf<FriendListDC>()

        friends.forEach { friend ->
            val user = main.impl.getUserByIdUser(friend.id)
            val image = main.impl.getAvatarByIdUser(friend.id)
            listFormattedFriends.add(
                FriendListDC(
                    id = user.idUser,
                    username = user.username,
                    status = user.status,
                    image = image
                )
            )
        }

        return listFormattedFriends
    }

    suspend fun getRequestsFriends(token: String): List<UserIUSI> {
        val idUser = main.impl.getTokenByToken(token).idUser
        val request = main.impl.getRequestsFriendByIdUser(idUser)
        val listFormattedRequests = mutableListOf<UserIUSI>()

        request.forEach { user ->

            val fullUser = main.impl.getUserByIdUser(user.id)

            listFormattedRequests.add(
                UserIUSI(
                    id = fullUser.idUser,
                    username = fullUser.username,
                    status = fullUser.status,
                    image = fullUser.image
                )
            )
        }
        return listFormattedRequests
    }

    suspend fun watchForRequestsFriends(idUser: String, socket: DefaultWebSocketServerSession) {
        main.impl.watchForRequestsFriends(idUser, socket)
    }

    suspend fun watchForFriend(idUser: String, socket: DefaultWebSocketServerSession) {
        main.impl.watchForFriend(idUser, socket)
    }

    suspend fun watchForInternalNotifications(idUser: String, socket: DefaultWebSocketServerSession) {
        main.impl.watchForInternalNotifications(idUser, socket)
    }

    suspend fun logOut(token: String) {
        main.impl.delTokenByTokenId(token)
    }

    suspend fun searchUsers(idUser: String, username: String): List<UsersSearch> {
        return main.impl.searchUsers(idUser, username)
    }

    suspend fun setStatusUser(idUser: String, status: String) {
        main.impl.setStatusUser(idUser, status)
    }

    suspend fun deleteAllSentNotifications(idUser: String) {
        main.impl.deleteAllSentNotifications(idUser)
    }

    suspend fun addOnePage(idUser: String, item: PageProfileDC) {
        main.db.getCollection<PageProfileDC>("pages_profile").insertOne(item)
        subscribers.db.createCollection("subscribers_${item.id}")
        content.db.createCollection("content_${item.id}")
        main.db.getCollection<UserMainEntity>("users")
            .findOneAndUpdate(UserMainEntity::idUser eq idUser, addToSet(UserMainEntity::pages_profile, item.id))
    }

    suspend fun getPageById(idPage: String): PageProfileDC? {
        return main.db.getCollection<PageProfileDC>("pages_profile").findOne(PageProfileDC::id eq idPage)
    }

    private suspend fun getAllPagesByList(listIds: List<String>): List<PageProfileFormattedDC> {
        val listAllPages = mutableListOf<PageProfileFormattedDC>()
        listIds.forEach { id ->
            main.db.getCollection<PageProfileDC>("pages_profile").findOne(PageProfileDC::id eq id)
                ?.let {
                    val countSubscribers =
                        subscribers.db.getCollection<SubscribersDC>("subscribers_${it.id}").countDocuments().toString()
                    val countContent =
                        content.db.getCollection<ContentUsersDC>("content_${it.id}").countDocuments().toString()
                    listAllPages.add(it.withCountSubsAndContents(countSubscribers, countContent))
                }
        }
        return listAllPages.toList()
    }

    suspend fun deletePage(idPage: String) {
        main.db.getCollection<PageProfileDC>("pages_profile").deleteOne(PageProfileDC::id eq idPage)
        subscribers.db.dropCollection("subscribers_$idPage")
        content.db.dropCollection("content_$idPage")
    }

    suspend fun getAllPagesByIdUser(idUser: String): List<PageProfileFormattedDC> {
        val listIdPages = main.impl.getUserByIdUser(idUser).pages_profile
        return getAllPagesByList(listIdPages)
    }

    suspend fun getAvatarByIdUser(idUser: String): String {
        return main.impl.getAvatarByIdUser(idUser)
    }

    suspend fun changeAvatarByUserId(idUser: String, pathToImage: String): String {
        return main.impl.changeAvatarByUserId(idUser, pathToImage)
    }

    fun deleteAvatarByIdUser(idUser: String, avatarOld: String) {
        File(avatarOld).delete()
    }

    suspend fun watchForNewMessages(idUser: String, socket: DefaultWebSocketServerSession) {
        new_messages.impl.watchForNewMessages(idUser, socket)
    }
}