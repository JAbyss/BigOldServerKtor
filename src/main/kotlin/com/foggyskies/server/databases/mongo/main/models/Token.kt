package com.foggyskies.server.databases.mongo.main.models

import org.bson.codecs.pojo.annotations.BsonId

//@kotlinx.serialization.Serializable
//data class Token(
//    @BsonId
//    var id: String = ObjectId().toString(),
//    var idUser: String
//) {
//
//    companion object {
//        val Empty: Token = Token(
//            "",
//            ""
//        )
//    }
//}
@kotlinx.serialization.Serializable
data class Token(
    @BsonId
    var token: String,
    var idUser: String
) {
    companion object {
        val Empty: Token = Token(
            "",
            ""
        )
    }
}