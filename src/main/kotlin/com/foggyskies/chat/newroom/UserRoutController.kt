package com.foggyskies.chat.newroom

import com.foggyskies.chat.data.FormattedChatDC
import com.foggyskies.chat.data.model.*
import com.foggyskies.chat.datanew.AllCollectionImpl
import com.jetbrains.handson.chat.server.chat.data.model.Token
import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch
import io.ktor.websocket.*
import org.litote.kmongo.addToSet
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.pull

class UserRoutController(
    private val allCollectionImpl: AllCollectionImpl,
    private val db: CoroutineDatabase
) {
    suspend fun checkOnExistToken(token: String): Boolean {
        return allCollectionImpl.checkOnExistToken(token)
    }

//    suspend fun getUsersByUsername(username: String): List<UsersSearch> {
//        return allCollectionImpl.getUserByUsername(username)
//    }

    suspend fun getChats(token: String): List<FormattedChatDC> {
        val idUser = db.getCollection<Token>("tokens").findOneById(token)?.idUser!!
        val idsChats = allCollectionImpl.getChatsByIdUser(idUser)
        val listChats = mutableListOf<FormattedChatDC>()

        idsChats.forEach { id ->
            val chat = allCollectionImpl.getChatById(id)
            val companion = if (idUser == chat.firstCompanion?.idUser) chat.secondCompanion!! else chat.firstCompanion!!
            val imageComp = allCollectionImpl.getUserByIdUser(companion.idUser).image

            listChats.add(
                FormattedChatDC(
                    id = chat.idChat,
                    nameChat = companion.nameUser,
                    idCompanion = companion.idUser,
                    image = imageComp
                )
            )
        }

        return listChats
    }

    suspend fun addRequestToFriend(userSender: UserNameID, idUserReceiver: String) {
        allCollectionImpl.addRequestFriendsByIdUser(idUserReceiver, userSender)
    }

    suspend fun getUserByToken(token: String): UserMainEntity {
        val username = allCollectionImpl.getTokenByToken(token).username
        return allCollectionImpl.getUserByUsername(username)
    }

    suspend fun acceptRequestFriend(userReceiver: UserNameID, idUserSender: String) {
        val requestsFriend = allCollectionImpl.getRequestsFriendByIdUser(userReceiver.id)


        if (requestsFriend.isNotEmpty()) {
            requestsFriend.forEach { userSenderL ->
                if (userSenderL.id == idUserSender) {
                    val friendCollection = db.getCollection<FriendDC>("friends")

                    val userSender = allCollectionImpl.getUserByIdUser(idUserSender)

                    val formattedUserSender = UserNameID(
                        id = userSender.idUser,
                        username = userSender.username
                    )

                    val friendsReceiver = allCollectionImpl.getFriendsByIdUser(userReceiver.id)
                    val friendsSender = allCollectionImpl.getFriendsByIdUser(idUserSender)

                    if (friendsReceiver.isNotEmpty()) {

                        friendCollection.updateOne(
                            FriendDC::idUser eq userReceiver.id,
                            addToSet(FriendDC::friends, formattedUserSender)
                        )

//                        val requestsFriendCollection = db.getCollection<RequestFriendDC>("requestsFriend")
                        val requestsFriend = allCollectionImpl.getRequestsFriendByIdUser(userReceiver.id)
                        if (requestsFriend.isNotEmpty())
                            allCollectionImpl.delRequestFriendsByIdUser(userReceiver.id, formattedUserSender)
//                            requestsFriendCollection.updateOne(
//                                RequestFriendDC::id eq userReceiver.id,
//                                pull(RequestFriendDC::requests, formattedUserSender)
//                            )
                    } else {
                        val document = FriendDC(
                            idUser = userReceiver.id,
                            friends = listOf(formattedUserSender)
                        )
                        friendCollection.insertOne(document)
                        val requestsFriend = allCollectionImpl.getRequestsFriendByIdUser(userReceiver.id)
                        if (requestsFriend.isNotEmpty())
                            allCollectionImpl.delRequestFriendsByIdUser(userReceiver.id, formattedUserSender)
                    }

                    if (friendsSender.isNotEmpty()) {
                        allCollectionImpl.addFriendByIdUser(formattedUserSender.id, userReceiver)
//                        friendCollection.updateOne(
//                            FriendDC::idUser eq formattedUserSender.id,
//                            addToSet(FriendDC::friends, userReceiver)
//                        )
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
        val idUser = allCollectionImpl.getTokenByToken(token).idUser
        val friends = allCollectionImpl.getFriendsByIdUser(idUser)
        val listFormattedFriends = mutableListOf<FriendListDC>()

        friends.forEach { friend ->
            val user = allCollectionImpl.getUserByIdUser(friend.id)
            listFormattedFriends.add(
                FriendListDC(
                    id = user.idUser,
                    username = user.username,
                    status = user.status,
                    image = user.image
                )
            )
        }

        return listFormattedFriends
    }

    suspend fun getRequestsFriends(token: String): List<UsersSearch> {
        val idUser = allCollectionImpl.getTokenByToken(token).idUser
        val request = allCollectionImpl.getRequestsFriendByIdUser(idUser)
        val listFormattedRequests = mutableListOf<UsersSearch>()

        request.forEach { user ->

            val fullUser = allCollectionImpl.getUserByIdUser(user.id)

            listFormattedRequests.add(UsersSearch(
                id = fullUser.idUser,
                username = fullUser.username,
                status = fullUser.status,
                image = fullUser.image
            ))
        }
        return listFormattedRequests
    }

    suspend fun watchForRequestsFriends(idUser: String, socket: DefaultWebSocketServerSession) {
        allCollectionImpl.watchForRequestsFriends(idUser, socket)
    }

    suspend fun watchForFriend(idUser: String, socket: DefaultWebSocketServerSession) {
        allCollectionImpl.watchForFriend(idUser, socket)
    }

    suspend fun logOut(token: String) {
        allCollectionImpl.delTokenByTokenId(token)
    }

    suspend fun searchUsers(username: String): List<UsersSearch>{
        return allCollectionImpl.searchUsers(username)
    }
}