package com.foggyskies.chat.data

import com.foggyskies.chat.routes.ChatMainEntity
import com.foggyskies.chat.routes.ChatUserEntity
import com.foggyskies.chat.routes.UserMainEntity
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase

class CreateChatDataSourceImpl(
    private val db: CoroutineDatabase
) : CreateChatDataSource {

    override suspend fun checkOnExistChat(idUser: String): Boolean {
        val isChatExist =
            db.getCollection<ChatMainEntity>("chats").find(" { \"users.idUser\" : \"$idUser\" } ").toList().isNotEmpty()

        return isChatExist
    }

    override suspend fun createChat(username: String, idUserFirst: String, idUserSecond: String): String {

        val usernameSecond =
            db.getCollection<UserMainEntity>("users").find(" { \"_id\": \"$idUserSecond\" } ").toList()[0].username

        val idChat = ObjectId().toString()

        val chat = ChatMainEntity(
            idChat = idChat,
            firstCompanion = ChatUserEntity(
                idUser = idUserFirst,
                nameUser = username
            ),
            secondCompanion = ChatUserEntity(
                idUser = idUserSecond,
                nameUser = usernameSecond
            )
        )

        db.getCollection<ChatMainEntity>("chats").insertOne(chat)

        return idChat
    }

    override suspend fun getChatId(idUser: String): String {
        return db.getCollection<ChatMainEntity>("chats").find(" { \"users.idUser\" : \"$idUser\" } ").toList()[0].idChat
    }

    override suspend fun createMessages(idChat: String) {
        db.createCollection("messages-$idChat")
    }

}