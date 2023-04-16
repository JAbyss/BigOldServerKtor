//package com.foggyskies.server.routes.auth
//
//import com.foggyskies.PasswordCoder
//import com.foggyskies.server.databases.mongo.main.models.RegistrationUserDC
//import com.foggyskies.server.databases.mongo.main.models.UserMainEntity
//import com.foggyskies.server.databases.mongo.main.models.UserNameID
//import com.foggyskies.server.databases.mongo.main.MainDBImpl
//import com.foggyskies.server.databases.mongo.main.models.Token
//import com.foggyskies.server.newroom.CheckTokenExist
//import org.litote.kmongo.coroutine.CoroutineDatabase
//
//class AuthRoutController(
//    private val mainDBImpl: MainDBImpl,
//    private val db: CoroutineDatabase
//): CheckTokenExist(db) {
//
//    suspend fun getUserByUsername(username: String): UserMainEntity? {
//        return mainDBImpl.getUserByUsername(username)
//    }
//
//    suspend fun checkOnExistUser(username: String): Boolean? {
//        return if (mainDBImpl.checkOnExistUser(username)) null else false
//    }
//
//    suspend fun checkOnExistEmail(e_mail: String): Boolean? {
//        return if (mainDBImpl.checkOnExistEmail(e_mail)) null else false
//    }
//
//    suspend fun createUser(user: RegistrationUserDC) {
//        mainDBImpl.createUser(user)
//    }
//
//    suspend fun checkOnExistTokenByUsername(username: String): Boolean {
//        return mainDBImpl.checkOnExistTokenByUsername(username)
//    }
//
//    suspend fun createToken(user: UserNameID): Token {
//        mainDBImpl.createToken(user)
//        return  mainDBImpl.getTokenByUsername(user.username)
//    }
//
//    suspend fun getToken(username: String): Token {
//        return mainDBImpl.getTokenByUsername(username)
//    }
//
//    suspend fun checkPasswordOnCorrect(userPassword: String, password: String): Boolean? {
////        val user = mainDBImpl.getUserByUsername(username)
//        return if (PasswordCoder.decodeStringFS(password) == PasswordCoder.decodeStringFS(userPassword)) true else null
//    }
//}