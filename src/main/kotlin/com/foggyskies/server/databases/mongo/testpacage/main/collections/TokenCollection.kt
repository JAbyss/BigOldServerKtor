//package com.foggyskies.server.databases.mongo.codes.testpacage.main.collections
//
//
//import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
//import com.foggyskies.server.databases.mongo.getCollection
//import com.foggyskies.server.databases.mongo.main.models.Token
//import org.litote.kmongo.eq
//
//suspend fun MainDataBase.TokenCol.createToken(idUser: String): Token {
//    val token = Token(
//        idUser = idUser
//    )
//    getCollection().insertOne(token)
//    return token
//}
//
//suspend fun MainDataBase.TokenCol.delTokenByTokenId(idToken: String) {
//    getCollection().deleteOne(Token::token eq idToken)
//}
//
//suspend fun MainDataBase.TokenCol.checkOnExistToken(token: String): Boolean {
//    return getCollection().findOne(Token::token eq token) != null
//}
//
//suspend fun MainDataBase.TokenCol.getIfExistToken(token: String): Token? {
//    return getCollection().findOne(Token::token eq token)
//}
//
//suspend fun MainDataBase.TokenCol.checkOnExistTokenById(idUser: String): Boolean {
//    return getCollection().findOne(Token::idUser eq idUser) != null
//}
//
//suspend fun MainDataBase.TokenCol.getTokenByToken(token: String): Token {
//    return getCollection().findOneById(token)!!
//}
//
//suspend fun MainDataBase.TokenCol.getTokenById(idUser: String): Token {
//    return getCollection().findOne(Token::idUser eq idUser)!!
//}
//
//suspend fun MainDataBase.TokenCol.deleteTokenByIdUser(idUser: String) {
//    getCollection().deleteOne(Token::idUser eq idUser)
//}
//TODO На помойку