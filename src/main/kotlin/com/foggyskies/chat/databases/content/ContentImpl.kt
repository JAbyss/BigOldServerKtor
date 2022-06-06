package com.foggyskies.chat.databases.content

import com.foggyskies.chat.databases.content.datasources.ContentCollectionDataSource
import com.foggyskies.chat.databases.content.models.CommentDC
import com.foggyskies.chat.databases.content.models.ContentPreviewDC
import com.foggyskies.chat.databases.content.models.ContentUsersDC
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class ContentImpl(
    private val contentDB: CoroutineDatabase
) : ContentCollectionDataSource {

    private val BASE_END_POINT = "content_"

    private fun CoroutineDatabase.getCollection(
        basename: String = BASE_END_POINT,
        component: String = ""
    ): CoroutineCollection<ContentUsersDC> {
        return contentDB.getCollection<ContentUsersDC>(basename + component)
    }

    override suspend fun getFirstFiftyContent(idPageProfile: String): List<ContentPreviewDC> {
        return contentDB.getCollection<ContentPreviewDC>(BASE_END_POINT + idPageProfile)
            .find(ContentUsersDC::type eq "image")
            .limit(50).toList()
    }

    override suspend fun addNewContent(idPageProfile: String, item: ContentUsersDC) {
        contentDB.getCollection(component = idPageProfile).insertOne(item)
    }

    override suspend fun deleteContent(idPageProfile: String, idContent: String) {
        contentDB.getCollection(component = idPageProfile).deleteOne(ContentUsersDC::id eq idContent)
    }

    override suspend fun addNewComment(idPageProfile: String, idPost: String, comment: CommentDC) {
        contentDB.getCollection(component = idPageProfile)
            .findOneAndUpdate(ContentUsersDC::id eq idPost, addToSet(ContentUsersDC::comments, comment))
    }

    override suspend fun getAllLikedUsers(idPageProfile: String, idPost: String): List<String> {
        return contentDB.getCollection(component = idPageProfile).findOne(ContentUsersDC::id eq idPost)?.likes
            ?: emptyList()
    }

    override suspend fun getFiftyComments(idPageProfile: String, idPost: String): List<CommentDC> {
        return contentDB.getCollection(component = idPageProfile).findAndCast<CommentDC>(ContentUsersDC::id eq idPost)
            .limit(50).toList()
    }

    override suspend fun getOnePostComments(idPageProfile: String, idPost: String): List<CommentDC> {

        val listComment = contentDB.getCollection(component = idPageProfile)
            .findOne(ContentUsersDC::id eq idPost)?.comments

        return listComment ?: emptyList()
    }

    override suspend fun addLikeToPost(idPageProfile: String, idPost: String, userId: String) {
        contentDB.getCollection(component = idPageProfile)
            .findOneAndUpdate(ContentUsersDC::id eq idPost, addToSet(ContentUsersDC::likes, userId))
    }

    override suspend fun delLikeToPost(idPageProfile: String, idPost: String, userId: String) {
        contentDB.getCollection(component = idPageProfile)
            .findOneAndUpdate(ContentUsersDC::id eq idPost, pull(ContentUsersDC::likes, userId))
    }

    override suspend fun getInfoAboutOnePost(idPageProfile: String, idPost: String): ContentUsersDC? {
        return contentDB.getCollection(component = idPageProfile).findOne(ContentUsersDC::id eq idPost)
    }
}