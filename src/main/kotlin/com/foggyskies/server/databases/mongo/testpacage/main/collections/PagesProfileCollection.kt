package com.foggyskies.server.databases.mongo.testpacage.main.collections

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.getCollection
import com.foggyskies.server.databases.mongo.main.models.PageProfileDC
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

suspend fun MainDataBase.PagesProfile.addOnePage(item: PageProfileDC) {
    getCollection().insertOne(item)
}

suspend fun MainDataBase.PagesProfile.getPageById(idPage: String): PageProfileDC? {
    return getCollection().findOne(PageProfileDC::id eq idPage)
}

suspend fun MainDataBase.PagesProfile.getAllPagesByList(listIds: List<String>): List<PageProfileDC> {
    return getCollection().find().toList()
}

suspend fun MainDataBase.PagesProfile.deletePage(idPage: String) {
    getCollection().deleteOne(PageProfileDC::id eq idPage)
}

suspend fun MainDataBase.PagesProfile.getAvatarPageProfile(idPage: String): String {
    return getCollection().findOne(PageProfileDC::id eq idPage)?.image ?: ""
}

suspend fun MainDataBase.PagesProfile.changeAvatarByIdPage(idPage: String, pathToImage: String): String {
    getCollection()
        .findOneAndUpdate(PageProfileDC::id eq idPage, setValue(PageProfileDC::image, pathToImage))?.image
    return pathToImage
}