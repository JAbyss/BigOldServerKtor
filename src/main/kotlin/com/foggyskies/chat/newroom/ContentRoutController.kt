package com.foggyskies.chat.newroom

import com.foggyskies.chat.data.model.*
import com.foggyskies.chat.databases.content.ContentImpl
import com.foggyskies.chat.databases.main.AllCollectionImpl
import org.litote.kmongo.eq
import java.io.File
import java.util.*

class ContentRoutController(
    private val content: ImpAndDB<ContentImpl>,
    private val main: ImpAndDB<AllCollectionImpl>
) : CheckTokenExist(main.db) {

    suspend fun getFirstFiftyContent(idPageProfile: String): List<ContentPreviewDC> {
        return content.impl.getFirstFiftyContent(idPageProfile)
    }

    suspend fun addNewContent(item: ContentRequestDC) {
        val decodedString = Base64.getDecoder().decode(item.item.value)
        val countFiles = File("images/").list().size + 1
        val nameFile = "images/image_content_$countFiles.png"
        File(nameFile).writeBytes(decodedString)

        content.impl.addNewContent(item.idPageProfile, item.item.toNewPost(nameFile))
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
            main.impl.getUserByIdUser(idUser).toUserIUSI()
        }
    }

    suspend fun getFiftyComments(idPageProfile: String, idPost: String): List<CommentDC> {
        return content.impl.getFiftyComments(idPageProfile, idPost)
    }

    suspend fun getPosts(token: String): List<SelectedPostWithIdPageProfile> {
        val listPosts = content.db.getCollection<ContentUsersDC>("content_6255505f294a832807980ce6").find().toList()
        val idUser = main.impl.getTokenByToken(token).idUser
        val newList = listPosts.map {
            SelectedPostWithIdPageProfile(
                idPageProfile = "6255505f294a832807980ce6",
                item = ContentPreviewDC(
                    id = it.id,
                    address = it.address
                ),
                image = "images/image_profile_4.png",
                author = "Test",
                countComets = it.comments.size.toString(),
                countLikes = it.likes.size.toString(),
                isLiked = it.likes.contains(idUser)

            )
        }


        return newList
    }

    suspend fun getOnePostComments(idPageProfile: String, idPost: String): List<CommentDC> {
        return content.impl.getOnePostComments(idPageProfile, idPost)
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

    suspend fun getInfoAboutOnePost(idPageProfile: String, idPost: String, token: String): SelectedPostWithIdPageProfile?{
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