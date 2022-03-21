package com.foggyskies.chat.data

import com.foggyskies.chat.data.model.UserMainEntity
import com.jetbrains.handson.chat.server.chat.data.model.Token
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class AuthDataSourceImpl(
    private val db: CoroutineDatabase
) : AuthDataSource {

    override suspend fun insertUser(user: UserMainEntity) {
        db.getCollection<UserMainEntity>("users").insertOne(user)
    }

    override suspend fun getUser(username: String): UserMainEntity {
        val userString = db.getCollection<UserMainEntity>("users").find(UserMainEntity::username eq username).toString()

        val user = Json.decodeFromString<UserMainEntity>(userString)

        return user
    }

    override suspend fun checkOnExistUser(username: String): Boolean {
       val isUserExist = db.getCollection<UserMainEntity>("users").find(UserMainEntity::username eq username).toList().isNotEmpty()

        return isUserExist
    }

    override suspend fun checkOnExistToken(username: String): Boolean {
        val isTokenExist = db.getCollection<Token>("tokens").find(Token::username eq username).toList().isNotEmpty()

        return isTokenExist
    }

    override suspend fun createToken(username: String): String {
        val token = ObjectId().toString()
        val idUser = db.getCollection<UserMainEntity>("users").findOne(UserMainEntity::username eq username)?.idUser!!
        db.getCollection<Token>("tokens").insertOne(Token(id = token, username = username, idUser = idUser))
        return  token
    }

    override suspend fun getToken(username: String): String {
        val token =  db.getCollection<Token>("tokens").find(Token::username eq username).toList()[0]
        return token.id
    }

    override suspend fun checkPasswordOnCorrect(password: String): Boolean {
        val isPasswordCorrect = db.getCollection<UserMainEntity>("users").find(UserMainEntity::password eq password).toList().isNotEmpty()

        return isPasswordCorrect
    }

    override suspend fun checkOnExistEmail(e_mail: String): Boolean {
        val isEmailExist = db.getCollection<UserMainEntity>("users").find(UserMainEntity::e_mail eq e_mail).toList().isNotEmpty()

        return isEmailExist
    }

}