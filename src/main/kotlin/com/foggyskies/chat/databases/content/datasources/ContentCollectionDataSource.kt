package com.foggyskies.chat.databases.content.datasources

import com.foggyskies.chat.databases.content.models.CommentDC
import com.foggyskies.chat.databases.content.models.ContentPreviewDC
import com.foggyskies.chat.databases.content.models.ContentUsersDC

interface ContentCollectionDataSource {

    suspend fun getFirstFiftyContent(idPageProfile: String): List<ContentPreviewDC>

    suspend fun addNewContent(idPageProfile: String, item: ContentUsersDC)

    suspend fun deleteContent(idPageProfile: String, idContent: String)

    suspend fun addNewComment(idPageProfile: String, idPost: String, comment: CommentDC)

    suspend fun getAllLikedUsers(idPageProfile: String, idPost: String): List<String>

    suspend fun getFiftyComments(idPageProfile: String, idPost: String): List<CommentDC>

    suspend fun getOnePostComments(idPageProfile: String, idPost: String): List<CommentDC>

    suspend fun addLikeToPost(idPageProfile: String, idPost: String, userId: String)

    suspend fun delLikeToPost(idPageProfile: String, idPost: String, userId: String)

    suspend fun getInfoAboutOnePost(idPageProfile: String, idPost: String): ContentUsersDC?
}