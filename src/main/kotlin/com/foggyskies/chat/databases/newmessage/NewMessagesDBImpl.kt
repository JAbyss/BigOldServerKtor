package com.foggyskies.chat.databases.newmessage

import com.foggyskies.chat.data.model.RequestFriendDC
import com.foggyskies.chat.data.model.UserIUSI
import com.foggyskies.chat.data.model.UserNameID
import com.foggyskies.chat.databases.newmessage.datasources.NewMessageCollectionDataSource
import com.jetbrains.handson.chat.server.chat.data.model.ChatMessage
import com.mongodb.MongoCommandException
import com.mongodb.client.model.changestream.OperationType
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.reactive.collect
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.addToSet
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.json

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
        } catch (_: MongoCommandException) {

        }
    }

    override suspend fun createDocument(idChat: String, idUser: String) {
        getCollection(idUser).insertOne(NewMessagesCollection(id = idChat, new_messages = emptyList()))
    }

    override suspend fun watchForNewMessages(idUser: String, socket: DefaultWebSocketServerSession) {
        val newMessages = getCollection(idUser).watch<NewMessagesCollection>()

        var oldList = emptyList<ChatMessage>()

        newMessages.consumeEach { item ->
            if (item.operationType == OperationType.INSERT || item.operationType == OperationType.UPDATE)
            //            if ((item.documentKey["_id"]?.asString())?.value.equals(idUser)) {
                if (item.updateDescription != null) {
                    val updatedFields =
                        Json.decodeFromString<List<ChatMessage>>(item.updateDescription.updatedFields["new_messages"]?.asArray()?.values.toString())
                    if (updatedFields.size > oldList.size) {
                        val formattedList = updatedFields - oldList
                        println("REQUEST 1 $formattedList")
                    }
                    oldList = updatedFields

                    //                    socket.send(Frame.Text("getRequestsFriends|${updatedFields.json}"))
                } else {
                    val json = Json.decodeFromString<List<ChatMessage>>(item.fullDocument.new_messages.json)

                    println("REQUEST 3 $json")
                    //                    socket.send(Frame.Text("getRequestsFriends|${json.json}"))
                }
        }
    }
}

@kotlinx.serialization.Serializable
data class NewMessagesCollection(
    @BsonId
    val id: String,
    val new_messages: List<ChatMessage>
)