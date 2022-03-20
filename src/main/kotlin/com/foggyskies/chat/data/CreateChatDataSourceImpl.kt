package com.foggyskies.chat.data

import com.foggyskies.chat.data.model.*
//import com.foggyskies.chat.data.model.ChatMainEntity
//import com.foggyskies.chat.data.model.ChatMainEntity_
//import com.foggyskies.chat.data.model.ChatMainEntity
//import com.foggyskies.chat.data.model.ChatUserEntity
import com.jetbrains.handson.chat.server.chat.data.model.UsersSearch
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.Data
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.gt
import org.litote.kmongo.lt
import org.litote.kmongo.util.idValue


class CreateChatDataSourceImpl(
    private val db: CoroutineDatabase
) : CreateChatDataSource {

    override suspend fun checkOnExistChatByIdUsers(idUserFirst: String, idUserSecond: String): String {
        val idChat = db.getCollection<ChatMainEntity>("chats").findOne(
            ChatMainEntity_.FirstCompanion.idUser eq idUserFirst,
            ChatMainEntity_.SecondCompanion.idUser eq idUserSecond
        )?.idChat

        return idChat ?: ""
    }

    override suspend fun checkOnExistChat(idUser: String): Boolean {

        return db.getCollection<ChatMainEntity>("chats").find(ChatMainEntity::idChat eq idUser).toList().isNotEmpty()
    }

    override suspend fun createChat(username: String, idUserFirst: String, idUserSecond: String): String {

        val usernameSecond =
            db.getCollection<UserMainEntity>("users").findOne(UserMainEntity::idUser eq idUserSecond)?.username

        val idChat = ObjectId().toString()

        val chat = ChatMainEntity(
            idChat = idChat,
            firstCompanion = ChatUserEntity(
                idUser = idUserFirst,
                nameUser = username
            ),
            secondCompanion = ChatUserEntity(
                idUser = idUserSecond,
                nameUser = usernameSecond!!
            )
        )

        db.getCollection<ChatMainEntity>("chats").insertOne(chat)

        return idChat
    }

    override suspend fun getChatId(idUser: String): String {
        return db.getCollection<ChatMainEntity>("chats").findOne(" { \"users.idUser\" : \"$idUser\" } ")?.idChat!!
    }

    override suspend fun createMessages(idChat: String) {
        db.createCollection("messages-$idChat")
    }

    override suspend fun getUserByUsername(username: String): UsersSearch {
        return db.getCollection<UsersSearch>("users").findOne(UsersSearch::username eq username)!!
    }

}