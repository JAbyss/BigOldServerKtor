package com.foggyskies.chat.data.model

import org.bson.codecs.pojo.annotations.BsonId

@kotlinx.serialization.Serializable
data class PageProfileDC(
    @BsonId
    var id: String,
    var title: String,
    var description: String,
    var image: String
){

    fun withCountSubsAndContents(countSubscribers: String, countContents: String): PageProfileFormattedDC {
        return PageProfileFormattedDC(
            id = this.id,
            title = title,
            description = description,
            image = image,
            countSubscribers = countSubscribers,
            countContents = countContents
        )
    }
}

@kotlinx.serialization.Serializable
data class PageProfileFormattedDC(
    @BsonId
    var id: String,
    var title: String,
    var description: String,
    var image: String,
    var countSubscribers: String,
    var countContents: String
)