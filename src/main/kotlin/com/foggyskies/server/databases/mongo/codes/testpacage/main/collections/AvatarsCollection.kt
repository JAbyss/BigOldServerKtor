package com.foggyskies.server.databases.mongo.codes.testpacage.main.collections

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.getCollection
import com.foggyskies.server.databases.mongo.main.models.AvatarDC
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

suspend fun MainDataBase.Avatars.getAvatarByIdUser(idUser: String): String =
    getCollection().findOne(AvatarDC::idUser eq idUser)?.image ?: ""


suspend fun MainDataBase.Avatars.changeAvatarByUserId(idUser: String, pathToImage: String): String {
    val path = getCollection()
        .findOneAndUpdate(AvatarDC::idUser eq idUser, setValue(AvatarDC::image, pathToImage))?.image
    return path ?: run {
        insertAvatar(AvatarDC(idUser, pathToImage))
        pathToImage
    }
}

suspend fun MainDataBase.Avatars.insertAvatar(idUser: String) =
    getCollection().insertOne(AvatarDC(idUser = idUser))

suspend fun MainDataBase.Avatars.insertAvatar(avatarDC: AvatarDC) =
    getCollection().insertOne(avatarDC)