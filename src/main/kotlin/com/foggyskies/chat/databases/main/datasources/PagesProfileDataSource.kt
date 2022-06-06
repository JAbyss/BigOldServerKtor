package com.foggyskies.chat.databases.main.datasources

import com.foggyskies.chat.databases.main.models.PageProfileDC

interface PagesProfileDataSource {

    suspend fun addOnePage(item: PageProfileDC)

    suspend fun getPageById(idPage: String): PageProfileDC?

    suspend fun getAllPagesByList(listIds: List<String>): List<PageProfileDC>

    suspend fun deletePage(idPage: String)

    suspend fun getAvatarPageProfile(idPage: String): String

    suspend fun changeAvatarByIdPage(idPage: String, pathToImage: String): String
}