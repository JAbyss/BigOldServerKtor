package com.foggyskies.chat.databases.newmessage

import com.foggyskies.ServerDate
//import com.foggyskies.chat.data.model.ChatMainEntity_
import com.foggyskies.chat.databases.message.models.ChatMessageCollection
import com.foggyskies.chat.data.model.ImpAndDB
import com.foggyskies.chat.databases.main.MainDBImpl
import com.foggyskies.chat.databases.main.models.ChatMainEntity_
import com.foggyskies.chat.databases.main.models.FriendDC
import com.foggyskies.chat.databases.newmessage.datasources.NewMessageCollectionDataSource
import com.foggyskies.chat.databases.newmessage.models.NewMessagesCollection
import com.foggyskies.chat.databases.newmessage.models.WatchNewMessage
import com.mongodb.MongoCommandException
import com.mongodb.client.model.changestream.OperationType
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.BsonString
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

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

    override suspend fun getNewMessagesByIdChat(idChat: String, idUser: String): List<ChatMessageCollection> {
        return getCollection(idUser).findOne(NewMessagesCollection::id eq idChat)?.new_messages ?: emptyList()
    }

    override suspend fun insertOneMessage(idChat: String, idUser: String, message: ChatMessageCollection) {
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

    override suspend fun watchForNewMessages(
        idUser: String,
        socket: DefaultWebSocketServerSession,
        main: ImpAndDB<MainDBImpl>
    ) {
        val newMessages = getCollection(idUser).watch<NewMessagesCollection>()

//        val oldMapNewMessages = getCollection(idUser).find().toList().toMutableList()
//            mutableListOf<NewMessagesCollection>()



        newMessages.consumeEach { item ->
            if (item.operationType == OperationType.INSERT || item.operationType == OperationType.UPDATE) {
                //            if ((item.documentKey["_id"]?.asString())?.value.equals(idUser)) {
                if (item.updateDescription != null) {
                    val updatedFields =
                        Json.decodeFromString<List<ChatMessageCollection>>(item.updateDescription.updatedFields["new_messages"]?.asArray()?.values.toString())
                    val idDocument = (item.documentKey[item.documentKey.firstKey] as BsonString).value

                    val chat = main.impl.getChatById(idDocument)
                    val (receiverCompanion, nameField) =
                        if (chat.firstCompanion?.idUser != idUser)
                            Pair(chat.firstCompanion!!, ChatMainEntity_.FirstCompanion)
                        else
                            Pair(chat.secondCompanion!!, ChatMainEntity_.SecondCompanion)

                    suspend fun sendNewMessages(){
                        val new_message = updatedFields.last()
                        val image = main.impl.getAvatarByIdUser(new_message.idUser)
                        val username = main.impl.getUserByIdUser(new_message.idUser).username
                        val newMessagesCollection = WatchNewMessage(
                            idChat = idDocument,
                            image = image,
                            username = username,
                            new_message = new_message.toCMDC()
                        )

                        socket.send(Frame.Text("getNewMessages|${Json.encodeToString(newMessagesCollection)}"))
                    }

                    if (!receiverCompanion.notifiable.isEmpty()) {
                        val timeMute = receiverCompanion.notifiable.toInt()
                        if (ServerDate.muteDate.toInt() > timeMute) {
                            // Размут
                            main.impl.muteChat(idDocument, idUser, nameField = nameField)
                            sendNewMessages()
                        }
                    } else {
                        sendNewMessages()
                    }
                }
            }
        }
    }

    override suspend fun getAllNewMessages(idUser: String): List<NewMessagesCollection> {
        return getCollection(idUser).find().toList()
    }

    override suspend fun deleteNewMessage(idUser: String, idChat: String, idMessage: String): Int {
        return getCollection(idUser).updateOne(
            NewMessagesCollection::id eq idChat,
            pullByFilter(NewMessagesCollection::new_messages, "{_id: '$idMessage'}".bson)
        ).matchedCount.toInt()
    }
}