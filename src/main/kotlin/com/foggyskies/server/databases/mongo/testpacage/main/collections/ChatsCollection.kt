package com.foggyskies.server.databases.mongo.testpacage.main.collections

import com.foggyskies.server.data.model.ChatUserEntity_
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.getCollection
import com.foggyskies.server.databases.mongo.main.models.ChatMainEntity
import com.foggyskies.server.databases.mongo.main.models.ChatMainEntity_
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.or
import org.litote.kmongo.setValue

suspend fun MainDataBase.Chats.checkOnExistChatByIdUsers(idUserFirst: String, idUserSecond: String): String? {
    val idChat = getCollection().findOne(
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
    return idChat
}

suspend fun MainDataBase.Chats.getChatById(idChat: String): ChatMainEntity {
    return getCollection().findOne(ChatMainEntity::idChat eq idChat)!!
}

//suspend fun MainDataBase.Chats.createChat(firstCompanion: UserNameID, secondCompanion: UserNameID): String {
//    val document = ChatMainEntity(
//        idChat = ObjectId().toString(),
//        firstCompanion = ChatUserEntity(idUser = firstCompanion.id, nameUser = firstCompanion.username),
//        secondCompanion = ChatUserEntity(idUser = secondCompanion.id, nameUser = secondCompanion.username)
//    )
//    getCollection().insertOne(document)
//    return document.idChat
//}

suspend fun MainDataBase.Chats.muteChat(
    idChat: String,
    idUser: String,
    nameField: ChatUserEntity_<ChatMainEntity>,
    time: String
) {
    getCollection()
        .findOneAndUpdate(
            ChatMainEntity::idChat eq idChat, setValue(
                nameField.notifiable, ""
            )
        )
}