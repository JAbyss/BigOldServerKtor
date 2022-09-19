package com.foggyskies.server.databases.mongo.main.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@kotlinx.serialization.Serializable
data class Token(
    @BsonId
    var id: String = ObjectId().toString(),
    var idUser: String
) {

    companion object {
        val Empty: Token = Token(
            "",
            ""
        )
    }
}