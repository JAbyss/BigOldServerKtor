import com.foggyskies.server.databases.mongo.content.models.ContentPreviewDC
import org.bson.codecs.pojo.annotations.BsonId

//package com.foggyskies.server.routes.content
//
//import com.foggyskies.server.data.model.*
//import com.foggyskies.server.databases.mongo.content.ContentImpl
//import com.foggyskies.server.databases.mongo.content.models.CommentDC
//import com.foggyskies.server.databases.mongo.content.models.ContentPreviewDC
//import com.foggyskies.server.databases.mongo.content.models.ContentUsersDC
//import com.foggyskies.server.databases.mongo.main.MainDBImpl
//import com.foggyskies.server.databases.mongo.main.models.UserIUSI
//import com.foggyskies.server.newroom.CheckTokenExist
//import org.bson.codecs.pojo.annotations.BsonId
//import org.litote.kmongo.eq
//import java.io.File
//import java.util.*
//import kotlin.collections.HashMap
//
//class ContentRoutController(
//    private val content: ImpAndDB<ContentImpl>,
//    private val main: ImpAndDB<MainDBImpl>
//) : CheckTokenExist(main.db) {
//
//    suspend fun getFirstFiftyContent(idPageProfile: String): List<ContentPreviewDC> {
//        return content.impl.getFirstFiftyContent(idPageProfile)
//    }
//
//    suspend fun addNewContent(item: ContentRequestDC, callback: suspend (ContentPreviewDC) -> Unit) {
//        val decodedString = Base64.getDecoder().decode(item.item.value)
//        val countFiles = File("images/").list().size + 1
//        val nameFile = "images/image_content_$countFiles.jpg"
//        File(nameFile).writeBytes(decodedString)
//        val newPostReady = item.item.toNewPost(nameFile)
//        callback(newPostReady.toContentPreview())
//        content.impl.addNewContent(item.idPageProfile, newPostReady)
//    }
//
//    suspend fun deleteContent(idPageProfile: String, idContent: String) {
//        content.impl.deleteContent(idPageProfile, idContent)
//    }
//
//    suspend fun addNewComment(idPageProfile: String, idPost: String, comment: CommentDC) {
//        content.impl.addNewComment(idPageProfile, idPost, comment)
//    }
//
//    suspend fun getAllLikedUsers(idPageProfile: String, idPost: String): List<UserIUSI> {
//        val likesList = content.impl.getAllLikedUsers(idPageProfile, idPost)
//        return likesList.map { idUser ->
//            val image = main.impl.getAvatarByIdUser(idUser)
//            main.impl.getUserByIdUser(idUser).toUserIUSI().copy(image = image)
//        }
//    }
//
//    suspend fun getFiftyComments(idPageProfile: String, idPost: String): List<CommentDC> {
//        return content.impl.getFiftyComments(idPageProfile, idPost)
//    }
//
//    suspend fun getPosts(token: String): List<SelectedPostWithIdPageProfile> {
//        val collections = content.db.listCollectionNames()
//        val mapPagesAndListPosts = hashMapOf<KeyPost, List<ContentUsersDC>>()
//        collections.forEach {
//            val listPost = content.db.getCollection<ContentUsersDC>(it).find("{_id: {\$ne: 'system'}}").toList()
//            val systemDoc = content.db.getCollection<SystemDoc>(it).findOne("{_id: {\$eq: 'system'}}")
//            val regex = "(?<=_).+".toRegex()
//            val key = regex.find(it)?.value!!
//            val keyPost = KeyPost(idPage = key)
//            systemDoc?.let {
//                val idUser = systemDoc.owner_id
//                keyPost.username = main.impl.getUserByIdUser(idUser).username
//                keyPost.avatar = main.impl.getAvatarByIdUser(idUser)
//
//            }
//
//            if (listPost.isNotEmpty())
//                if (mapPagesAndListPosts[keyPost] != null)
//                    mapPagesAndListPosts[keyPost] =
//                        mapPagesAndListPosts[keyPost]!! + listPost
//                else
//                    mapPagesAndListPosts[keyPost] = listPost
//        }
//
//        val idUser = main.impl.getTokenByToken(token).idUser
//        val mainFormattedListPosts = mutableListOf<SelectedPostWithIdPageProfile>()
//
//        mapPagesAndListPosts.forEachKeys { key, listPosts, index ->
//
//            val formattedListPosts = listPosts.map {
//                SelectedPostWithIdPageProfile(
//                    idPageProfile = key.idPage,
//                    item = ContentPreviewDC(
//                        id = it.id,
//                        address = it.address
//                    ),
//                    image = key.avatar,
//                    description = if (it.description == "Описание публикации...") "" else it.description,
//                    author = key.username,
//                    countComets = it.comments.size.toString(),
//                    countLikes = it.likes.size.toString(),
//                    isLiked = it.likes.contains(idUser)
//
//                )
//            }
//            mainFormattedListPosts.addAll(formattedListPosts)
//
//        }
//
//
//        return mainFormattedListPosts
//    }
//
//    @kotlinx.serialization.Serializable
//    data class FormattedCommentDC(
//        val users: HashMap<String, UserIUSI>,
//        val comments: List<CommentDC>
//    )
//
////    data class FormattedCommentDC(
////        val id: String,
////        val idUser: String,
////        val message: String,
////        val date: String,
//////        val image: String,
//////        val username: String
////    )
//
//    suspend fun getOnePostComments(idPageProfile: String, idPost: String): FormattedCommentDC {
//        val idsAndUsername = hashMapOf<String, UserIUSI>()
//
//
//        val listComments = content.impl.getOnePostComments(idPageProfile, idPost)
//        listComments.forEach { comment ->
//            if (!idsAndUsername.containsKey(comment.idUser)) {
//                val user = main.impl.getUserByIdUser(comment.idUser).toUserIUSI()
//                val image = main.impl.getAvatarByIdUser(comment.idUser)
//                idsAndUsername[comment.idUser] = user.copy(image = image)
//            }
//        }
//        val formattedCommentDC = FormattedCommentDC(
//            users = idsAndUsername,
//            comments = listComments
//        )
//
//        return formattedCommentDC
//    }
//
//    suspend fun addLikeToPost(idPageProfile: String, idPost: String, token: String): Boolean {
//        val idUserLiker = main.impl.getTokenByToken(token).idUser
//        val likesList = content.db.getCollection<ContentUsersDC>("content_$idPageProfile")
//            .findOne(ContentUsersDC::id eq idPost)?.likes ?: emptyList()
//        return if (!likesList.contains(idUserLiker)) {
//            content.impl.addLikeToPost(idPageProfile, idPost, idUserLiker)
//            true
//        } else {
//            content.impl.delLikeToPost(idPageProfile, idPost, idUserLiker)
//            false
//        }
//    }
//
//    suspend fun getInfoAboutOnePost(
//        idPageProfile: String,
//        idPost: String,
//        token: String
//    ): SelectedPostWithIdPageProfile? {
//        val idUser = main.impl.getTokenByToken(token).idUser
//        val systemDoc = content.db.getCollection<SystemDoc>("content_$idPageProfile").findOne("{_id: {\$eq: 'system'}}")
//        val keyPost = KeyPost(idPage = idPageProfile)
//        systemDoc?.let {
//            val _idUser = systemDoc.owner_id
//            keyPost.username = main.impl.getUserByIdUser(_idUser).username
//            keyPost.avatar = main.impl.getAvatarByIdUser(_idUser)
//
//        }
//
//        return content.impl.getInfoAboutOnePost(idPageProfile, idPost)
//            ?.toSelectedPostWithIdPageProfile(idPageProfile, idUser, keyPost.avatar, keyPost.username)
//    }
//}
//
@kotlinx.serialization.Serializable
data class SelectedPostWithIdPageProfile(
    var idPageProfile: String,
    var item: ContentPreviewDC,
    var author: String,
    var image: String,
    var description: String,
    var countLikes: String,
    var countComets: String,
    var isLiked: Boolean
)
@kotlinx.serialization.Serializable
data class SystemDoc(
    @BsonId
    val id: String = "system",
    val date_create: String,
    val owner_id: String,
)

data class KeyPost(
    var username: String = "",
    var idPage: String = "",
    var avatar: String = ""
)

fun <K, V> Map<K, V>.forEachKeys(action: (key: K, value: V, index: Int) -> Unit) {
    for (key in this.keys) action(key, this[key]!!, this.keys.indexOf(key))
}