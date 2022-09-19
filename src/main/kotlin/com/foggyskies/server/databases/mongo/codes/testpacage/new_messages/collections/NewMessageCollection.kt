package com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.collections

import com.foggyskies.ServerDate
import com.foggyskies.server.databases.message.models.ChatMessageCollection
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getAvatarByIdUser
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.getChatById
import com.foggyskies.server.databases.mongo.codes.testpacage.new_messages.NewMessagesDataBase
import com.foggyskies.server.databases.mongo.getCollection
import com.foggyskies.server.databases.mongo.main.models.ChatMainEntity_
import com.foggyskies.server.databases.mongo.newmessage.models.NewMessagesCollection
import com.foggyskies.server.databases.mongo.newmessage.models.WatchNewMessage
import com.foggyskies.server.extendfun.toBoolean
import com.foggyskies.server.routes.chat.EditMessageEntity
import com.foggyskies.server.routes.user.requests.getUserByIdUser
import com.mongodb.MongoCommandException
import com.mongodb.client.model.changestream.OperationType
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.BsonDocument
import org.bson.BsonString
import org.bson.Document
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.aggregate

suspend fun NewMessagesDataBase.NewMessages.checkOnExistDocument(idChat: String, idUser: String): Boolean {
    return getCollection(idUser).findOne(NewMessagesCollection::id eq idChat) != null
}

suspend fun NewMessagesDataBase.NewMessages.getNewMessagesByIdChat(
    idChat: String,
    idUser: String
): List<ChatMessageCollection> {
    return getCollection(idUser).findOne(NewMessagesCollection::id eq idChat)?.new_messages ?: emptyList()
}

suspend fun NewMessagesDataBase.NewMessages.insertOneMessage(
    idChat: String,
    idUser: String,
    message: ChatMessageCollection
) {
    if (getCollection(idUser).findOne(NewMessagesCollection::id eq idChat) == null)
        createDocument(idChat, idUser)
    getCollection(idUser)
        .updateOne(NewMessagesCollection::id eq idChat, addToSet(NewMessagesCollection::new_messages, message))
}

suspend fun NewMessagesDataBase.NewMessages.clearOneChat(idChat: String, idUser: String) {
    getCollection(idUser).deleteOne(NewMessagesCollection::id eq idChat)
}

suspend fun NewMessagesDataBase.NewMessages.createCollection(idUser: String) {
    try {
        db.createCollection(idUser)
    } catch (_: MongoCommandException) {

    }
}

suspend fun NewMessagesDataBase.NewMessages.createDocument(idChat: String, idUser: String) {
    getCollection(idUser).insertOne(NewMessagesCollection(id = idChat, new_messages = emptyList()))
}

suspend fun NewMessagesDataBase.NewMessages.watchForNewMessages(
    idUser: String,
    socket: DefaultWebSocketServerSession
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

                val chat = MainDataBase.Chats.getChatById(idDocument)
                val (receiverCompanion, nameField) =
                    if (chat.firstCompanion?.idUser != idUser)
                        Pair(chat.firstCompanion!!, ChatMainEntity_.FirstCompanion)
                    else
                        Pair(chat.secondCompanion!!, ChatMainEntity_.SecondCompanion)

                suspend fun sendNewMessages() {
                    val new_message = updatedFields.last()
                    val image = MainDataBase.Avatars.getAvatarByIdUser(new_message.idUser)
                    val username = getUserByIdUser(new_message.idUser).username
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
                        //TODO Какие-то непонятки
//                        MainDataBase.Chats.muteChat(idDocument, idUser, nameField = nameField)
                        sendNewMessages()
                    }
                } else {
                    sendNewMessages()
                }
            }
        }
    }
}

suspend fun NewMessagesDataBase.NewMessages.getAllNewMessages(idUser: String): List<NewMessagesCollection> {
    return getCollection(idUser).find().toList()
}

suspend fun NewMessagesDataBase.NewMessages.deleteNewMessage(idUser: String, idChat: String, idMessage: String): Int {
    return getCollection(idUser).updateOne(
        NewMessagesCollection::id eq idChat,
        pullByFilter(NewMessagesCollection::new_messages, "{_id: '$idMessage'}".bson)
    ).matchedCount.toInt()
}

suspend fun NewMessagesDataBase.NewMessages.editMessage(editMessageEntity: EditMessageEntity): Boolean {

    return getCollection(editMessageEntity.idUser).updateOne(
        "{_id: '${editMessageEntity.idChat}', 'new_messages._id': '${editMessageEntity.idMessage}'}",
        "{\$set: {'new_messages.$.message': '${editMessageEntity.newMessage}'} }"
    ).modifiedCount.toInt().toBoolean()
}

suspend fun NewMessagesDataBase.NewMessages.getMessageById(idChat: String, idUser: String, idMessage: String): ChatMessageCollection {
    val message = Json.decodeFromString<ChatMessageCollection>(getCollection(idUser).aggregate<BsonDocument>(
        match(NewMessagesCollection::id eq idChat),
        unwind(fieldName = "\$new_messages"),
        project("{_id: 0}".bson),
        match("{ 'new_messages._id': '$idMessage'}".bson),
    ).first()?.get("new_messages").toString())
    return message
}