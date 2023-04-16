package com.foggyskies.server.databases.mongo.testpacage.main.collections

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.getCollection
import com.foggyskies.server.databases.mongo.main.models.FriendDC
import com.foggyskies.server.databases.mongo.main.models.RequestFriendDC
import com.foggyskies.server.databases.mongo.main.models.UserMainEntity
import com.foggyskies.server.databases.mongo.main.models.UsersSearch
import com.foggyskies.server.extendfun.forEachSuspend
import org.bson.Document
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.aggregate
import org.litote.kmongo.coroutine.projection

suspend fun MainDataBase.Users.getUsers(): List<UserMainEntity> {
    return getCollection().find().toList()
}

suspend fun MainDataBase.Users.getUserByUsername(username: String): UserMainEntity? {
    return getCollection().findOne(UserMainEntity::username eq username)
}

suspend fun MainDataBase.Users.getUserByIdUser(idUser: String): UserMainEntity {
    return getCollection().findOne(UserMainEntity::idUser eq idUser)!!
}

suspend fun MainDataBase.Users.getChatsByIdUser(idUser: String): List<String> {
    return getCollection().findOne(UserMainEntity::idUser eq idUser)?.chats ?: emptyList()
}

//suspend fun createUser(registrationUserDC: RegistrationUserDC) {
//    val user = UserMainEntity(
//        username = registrationUserDC.username,
//        password = registrationUserDC.password,
//        e_mail = registrationUserDC.e_mail,
//        status = "Не в сети",
//    )
//    MainDataBase.Avatars.insertAvatar(idUser = user.idUser)
//    db.getCollection<UserMainEntity>("users").insertOne(user)
//}

suspend fun MainDataBase.Users.insertUser(user: UserMainEntity) {
    MainDataBase.Avatars.insertAvatar(idUser = user.idUser)
    getCollection().insertOne(user)
}

suspend fun MainDataBase.Users.searchUsers(idUser: String, username: String): List<UsersSearch> {
    val users = getCollection()
        .find(" { \"username\": { ${MongoOperator.regex}: '^$username.+|$username', ${MongoOperator.options}: 'i' } } ")
        .limit(10).toList()

    val listUsersSearch = mutableListOf<UsersSearch>()

    users.forEachSuspend { user ->
        val isFriend = db.getCollection<FriendDC>("friends").findOne(FriendDC::idUser eq idUser)?.friends?.contains(
            user.idUser
        ) ?: false
        var awaitAccept = false
        db.getCollection<RequestFriendDC>("requestsFriend")
            .findOne(RequestFriendDC::id eq user.idUser)?.requests?.forEach {
                if (it == idUser) {
                    awaitAccept = true
                    return@forEach
                }
            }
        listUsersSearch.add(
            UsersSearch(
                id = user.idUser,
                username = user.username,
                status = user.status,
                image = "user.image",
                isFriend = isFriend,
                awaitAccept = awaitAccept
            )
        )
    }

    return listUsersSearch
}

suspend fun MainDataBase.Users.addChatToUsersByIdUsers(idUserFirst: String, idUserSecond: String, idChat: String) {
    getCollection().updateMany(
        or(UserMainEntity::idUser eq idUserFirst, UserMainEntity::idUser eq idUserSecond),
        addToSet(UserMainEntity::chats, idChat)
    )
}

suspend fun MainDataBase.Users.setStatusUser(idUser: String, status: String) {
    getCollection()
        .findOneAndUpdate(UserMainEntity::idUser eq idUser, setValue(UserMainEntity::status, status))
}

suspend fun MainDataBase.Users.checkOnExistEmail(e_mail: String): Boolean {
    return getCollection().findOne(UserMainEntity::e_mail eq e_mail) != null
}

suspend fun MainDataBase.Users.checkOnExistUser(username: String): Boolean {
    return getCollection().findOne(UserMainEntity::username eq username) != null
}

suspend fun MainDataBase.Users.getStatusByIdUser(idUser: String): String {
    return getCollection().findOne(UserMainEntity::idUser eq idUser)?.status!!
}

suspend fun MainDataBase.Users.lockUser(idUser: String, value: Boolean) {
    getCollection()
        .updateOne(UserMainEntity::idUser eq idUser, setValue(UserMainEntity::isLocked, value))
}

suspend fun MainDataBase.Users.getUsername(idUser: String): String? {
    return getCollection().findOne(UserMainEntity::idUser eq idUser)?.username
}