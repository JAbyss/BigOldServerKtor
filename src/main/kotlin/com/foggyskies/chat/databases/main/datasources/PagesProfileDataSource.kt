package com.foggyskies.chat.databases.main.datasources

import com.foggyskies.chat.data.model.PageProfileDC

interface PagesProfileDataSource {

    suspend fun addOnePage(item: PageProfileDC)

    suspend fun getPageById(idPage: String): PageProfileDC?

    suspend fun getAllPagesByList(listIds: List<String>): List<PageProfileDC>

    suspend fun deletePage(idPage: String)

}