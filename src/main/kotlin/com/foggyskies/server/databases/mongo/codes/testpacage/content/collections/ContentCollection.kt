package com.foggyskies.server.databases.mongo.codes.testpacage.content.collections

import com.foggyskies.server.databases.mongo.codes.testpacage.content.ContentDataBase
import com.foggyskies.server.databases.mongo.content.models.CommentDC
import com.foggyskies.server.databases.mongo.content.models.ContentPreviewDC
import com.foggyskies.server.databases.mongo.content.models.ContentUsersDC
import com.foggyskies.server.databases.mongo.getCollection
import com.mongodb.client.model.Aggregates.project
import org.bson.BsonDocument
import org.bson.BsonString
import org.bson.Document
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.aggregate
import org.litote.kmongo.coroutine.projection
import java.io.File

suspend fun ContentDataBase.Content.getFirstFiftyContent(idPageProfile: String): List<ContentUsersDC> {
    return getCollection(idPageProfile)
        .find(ContentUsersDC::type eq "Image")
        .limit(50).toList()
}

suspend fun ContentDataBase.Content.getAllAddressesContent(idPageProfile: String): List<String> {

    return getCollection(idPageProfile).withDocumentClass<Document>().aggregate<Document>(
        match(ContentUsersDC::id ne "system"),
        project(ContentUsersDC::address from ContentUsersDC::address, excludeId()),
    ).toList().map { it[ContentUsersDC::address.name].toString() }
}

suspend fun ContentDataBase.Content.getFirstFiftyContentPreview(idPageProfile: String): List<ContentPreviewDC> {
    return getCollection(idPageProfile)
        .find(ContentUsersDC::type eq "Image")
        .limit(50).toList().map { it.toContentPreview() }
}

suspend fun ContentDataBase.Content.addNewContent(idPageProfile: String, item: ContentUsersDC) {
    getCollection(idPageProfile).insertOne(item)
}

suspend fun ContentDataBase.Content.deleteContent(idPageProfile: String, idContent: String) {
    getCollection(idPageProfile).deleteOne(ContentUsersDC::id eq idContent)
}

suspend fun ContentDataBase.Content.deleteCollection(idPageProfile: String) {
    db.dropCollection(name + idPageProfile)
}

suspend fun ContentDataBase.Content.addNewComment(idPageProfile: String, idPost: String, comment: CommentDC) {
    getCollection(idPageProfile)
        .findOneAndUpdate(ContentUsersDC::id eq idPost, addToSet(ContentUsersDC::comments, comment))
}

suspend fun ContentDataBase.Content.getAllLikedUsers(idPageProfile: String, idPost: String): List<String> {
    return getCollection(idPageProfile).findOne(ContentUsersDC::id eq idPost)?.likes
        ?: emptyList()
}

suspend fun ContentDataBase.Content.getFiftyComments(idPageProfile: String, idPost: String): List<CommentDC> {
    return getCollection(idPageProfile).findAndCast<CommentDC>(ContentUsersDC::id eq idPost)
        .limit(50).toList()
}

suspend fun ContentDataBase.Content.getOnePostComments(idPageProfile: String, idPost: String): List<CommentDC> {

    val listComment = getCollection(idPageProfile)
        .findOne(ContentUsersDC::id eq idPost)?.comments

    return listComment ?: emptyList()
}

suspend fun ContentDataBase.Content.addLikeToPost(idPageProfile: String, idPost: String, userId: String) {
    getCollection(idPageProfile)
        .findOneAndUpdate(ContentUsersDC::id eq idPost, addToSet(ContentUsersDC::likes, userId))
}

suspend fun ContentDataBase.Content.delLikeToPost(idPageProfile: String, idPost: String, userId: String) {
    getCollection(idPageProfile)
        .findOneAndUpdate(ContentUsersDC::id eq idPost, pull(ContentUsersDC::likes, userId))
}

suspend fun ContentDataBase.Content.getInfoAboutOnePost(idPageProfile: String, idPost: String): ContentUsersDC? {
    return getCollection(idPageProfile).findOne(ContentUsersDC::id eq idPost)
}