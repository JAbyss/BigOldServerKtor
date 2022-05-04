package com.foggyskies.chat.data.model

import com.foggyskies.chat.newroom.SelectedPostWithIdPageProfile
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class ContentUsersDC(
    @BsonId
    var id: String,
    var type: String,
    var likes: List<String>,
    var comments: List<CommentDC>,
    var address: String,
    var description: String = ""
){
    fun toSelectedPostWithIdPageProfile(idPageProfile: String, idUser: String): SelectedPostWithIdPageProfile {
        return SelectedPostWithIdPageProfile(
            idPageProfile = idPageProfile,
            item = ContentPreviewDC(
                id = id,
                address = address
            ),
            image = "",
            author = "",
            countComets = comments.size.toString(),
            countLikes = likes.size.toString(),
            isLiked = likes.contains(idUser)
        )
    }
}

@Serializable
data class CommentDC(
//    @BsonId
    var id: String,
    var idUser: String,
    var message: String,
    var date: String
)

@Serializable
data class ContentPreviewDC(
    @BsonId
    val id: String,
    val address: String,
)

//@Serializable
//data class LikesDC(
//    var idUser: String
//)