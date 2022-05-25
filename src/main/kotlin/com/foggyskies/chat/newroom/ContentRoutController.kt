package com.foggyskies.chat.newroom

import com.foggyskies.chat.data.model.*
import com.foggyskies.chat.databases.content.ContentImpl
import com.foggyskies.chat.databases.main.MainDBImpl
import org.litote.kmongo.eq
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class ContentRoutController(
    private val content: ImpAndDB<ContentImpl>,
    private val main: ImpAndDB<MainDBImpl>
) : CheckTokenExist(main.db) {

    suspend fun getFirstFiftyContent(idPageProfile: String): List<ContentPreviewDC> {
        return content.impl.getFirstFiftyContent(idPageProfile)
    }

    suspend fun addNewContent(item: ContentRequestDC, callback: suspend (ContentPreviewDC) -> Unit) {
        val decodedString = Base64.getDecoder().decode(item.item.value)
        val countFiles = File("images/").list().size + 1
        val nameFile = "images/image_content_$countFiles.jpg"
        File(nameFile).writeBytes(decodedString)
        val newPostReady = item.item.toNewPost(nameFile)
        callback(newPostReady.toContentPreview())
        content.impl.addNewContent(item.idPageProfile, newPostReady)
    }

    suspend fun deleteContent(idPageProfile: String, idContent: String) {
        content.impl.deleteContent(idPageProfile, idContent)
    }

    suspend fun addNewComment(idPageProfile: String, idPost: String, comment: CommentDC) {
        content.impl.addNewComment(idPageProfile, idPost, comment)
    }

    suspend fun getAllLikedUsers(idPageProfile: String, idPost: String): List<UserIUSI> {
        val likesList = content.impl.getAllLikedUsers(idPageProfile, idPost)
        return likesList.map { idUser ->
            val image = main.impl.getAvatarByIdUser(idUser)
            main.impl.getUserByIdUser(idUser).toUserIUSI().copy(image = image)
        }
    }

    suspend fun getFiftyComments(idPageProfile: String, idPost: String): List<CommentDC> {
        return content.impl.getFiftyComments(idPageProfile, idPost)
    }

    suspend fun getPosts(token: String): List<SelectedPostWithIdPageProfile> {
        val listPosts = content.db.getCollection<ContentUsersDC>("content_6283b897d249eb7a90167baa").find().toList()
        val idUser = main.impl.getTokenByToken(token).idUser
        val newList = listPosts.map {
            SelectedPostWithIdPageProfile(
                idPageProfile = "6283b897d249eb7a90167baa",
                item = ContentPreviewDC(
                    id = it.id,
                    address = it.address
                ),
                image = "images/profiles_avatar/image_628a789c95044864e0b9e6b5.jpg",
                author = "Test",
                countComets = it.comments.size.toString(),
                countLikes = it.likes.size.toString(),
                isLiked = it.likes.contains(idUser)

            )
        }


        return newList
    }

    @kotlinx.serialization.Serializable
    data class FormattedCommentDC(
        val users: HashMap<String, UserIUSI>,
        val comments: List<CommentDC>
    )

//    data class FormattedCommentDC(
//        val id: String,
//        val idUser: String,
//        val message: String,
//        val date: String,
////        val image: String,
////        val username: String
//    )

    suspend fun getOnePostComments(idPageProfile: String, idPost: String): FormattedCommentDC {
        val idsAndUsername = hashMapOf<String, UserIUSI>()


        val listComments = content.impl.getOnePostComments(idPageProfile, idPost)
        listComments.forEach { comment ->
            if (!idsAndUsername.containsKey(comment.idUser)) {
                val user = main.impl.getUserByIdUser(comment.idUser).toUserIUSI()
                val image = main.impl.getAvatarByIdUser(comment.idUser)
                idsAndUsername[comment.idUser] = user.copy(image = image)
            }
        }
        val formattedCommentDC = FormattedCommentDC(
            users = idsAndUsername,
            comments = listComments
        )

        return formattedCommentDC
    }

    suspend fun addLikeToPost(idPageProfile: String, idPost: String, token: String): Boolean {
        val idUserLiker = main.impl.getTokenByToken(token).idUser
        val likesList = content.db.getCollection<ContentUsersDC>("content_$idPageProfile")
            .findOne(ContentUsersDC::id eq idPost)?.likes ?: emptyList()
        return if (!likesList.contains(idUserLiker)) {
            content.impl.addLikeToPost(idPageProfile, idPost, idUserLiker)
            true
        } else {
            content.impl.delLikeToPost(idPageProfile, idPost, idUserLiker)
            false
        }
    }

    suspend fun getInfoAboutOnePost(
        idPageProfile: String,
        idPost: String,
        token: String
    ): SelectedPostWithIdPageProfile? {
        val idUser = main.impl.getTokenByToken(token).idUser
        return content.impl.getInfoAboutOnePost(idPageProfile, idPost)
            ?.toSelectedPostWithIdPageProfile(idPageProfile, idUser)
    }
}

@kotlinx.serialization.Serializable
data class SelectedPostWithIdPageProfile(
    var idPageProfile: String,
    var item: ContentPreviewDC,
    var author: String,
    var image: String,
    var countLikes: String,
    var countComets: String,
    var isLiked: Boolean
)