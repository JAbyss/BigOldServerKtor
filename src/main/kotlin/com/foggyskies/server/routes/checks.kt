package com.foggyskies.server.routes

import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.checkOnExistEmail
import com.foggyskies.server.databases.mongo.codes.testpacage.main.collections.checkOnExistUser

inline fun checks(conditions: () -> Unit) = conditions()

suspend inline fun checkOnExistUser(username: String): Unit? {
    return if (MainDataBase.Users.checkOnExistUser(username)) null else Unit
}

suspend fun checkOnExistEmail(e_mail: String): Unit? {
    return if (MainDataBase.Users.checkOnExistEmail(e_mail)) null else Unit
}