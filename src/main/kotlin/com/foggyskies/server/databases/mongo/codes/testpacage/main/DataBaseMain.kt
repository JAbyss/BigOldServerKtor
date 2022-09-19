package com.foggyskies.server.databases.mongo.codes.testpacage.main

import com.foggyskies.server.databases.mongo.MongoCollection
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase.NameCollections.avatars
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase.NameCollections.chats
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase.NameCollections.friends
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase.NameCollections.pagesProfile
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase.NameCollections.requestFriends
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase.NameCollections.tokens
import com.foggyskies.server.databases.mongo.codes.testpacage.main.MainDataBase.NameCollections.users
import com.foggyskies.server.databases.mongo.main.models.*
import com.foggyskies.server.plugin.KClient

object MainDataBase {

    val db = KClient.getDatabase("petapp_db")

    object NameCollections {
        const val tokens = "tokens"
        const val users = "users"
        const val avatars = "avatars"
        const val friends = "friends"
        const val requestFriends = "requestsFriend"
        const val pagesProfile = "pages_profile"
        const val chats = "chats"
    }

    object TokenCol : MongoCollection<Token>(db, tokens)
    object Users : MongoCollection<UserMainEntity>(db, users)
    object Avatars : MongoCollection<AvatarDC>(db, avatars)
    object Friends: MongoCollection<FriendDC>(db, friends)
    object Requests: MongoCollection<RequestFriendDC>(db, requestFriends)
    object PagesProfile: MongoCollection<PageProfileDC>(db, pagesProfile)
    object Chats: MongoCollection<ChatMainEntity>(db, chats)
}