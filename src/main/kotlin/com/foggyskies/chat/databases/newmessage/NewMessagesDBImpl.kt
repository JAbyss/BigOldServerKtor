package com.foggyskies.chat.databases.newmessage

import com.foggyskies.chat.databases.newmessage.datasources.NewMessageCollectionDataSource
import com.jetbrains.handson.chat.server.chat.data.model.ChatMessage
import com.mongodb.MongoCommandException
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.addToSet
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.util.idValue

class NewMessagesDBImpl(
    private val db: CoroutineDatabase
) : NewMessageCollectionDataSource {

    private val BASE_END_POINT = "new_messages_"

    private fun getCollection(
        component: String = ""
    ): CoroutineCollection<NewMessagesCollection> {
        return db.getCollection(BASE_END_POINT + component)
    }

    override suspend fun checkOnExistDocument(idChat: String, idUser: String): Boolean {
        return getCollection(idUser).findOne(NewMessagesCollection::id eq idChat) != null
    }

    override suspend fun getAllNewMessages(idChat: String, idUser: String): List<ChatMessage> {
        return getCollection(idUser).findOne(NewMessagesCollection::id eq idChat)?.new_messages ?: emptyList()
    }

    override suspend fun insertOneMessage(idChat: String, idUser: String, message: ChatMessage) {
        getCollection(idUser)
            .updateOne(NewMessagesCollection::id eq idChat, addToSet(NewMessagesCollection::new_messages, message))
    }

    override suspend fun clearOneChat(idChat: String, idUser: String) {
        getCollection(idUser).deleteOne(NewMessagesCollection::id eq idChat)
    }

    override suspend fun createCollection(idUser: String) {
        try {
            db.createCollection(BASE_END_POINT + idUser)
        }catch (_: MongoCommandException){

        }
    }

    override suspend fun createDocument(idChat: String, idUser: String) {
        getCollection(idUser).insertOne(NewMessagesCollection(id = idChat, new_messages = emptyList()))
    }
}

@kotlinx.serialization.Serializable
data class NewMessagesCollection(
    @BsonId
    val id: String,
    val new_messages: List<ChatMessage>
)