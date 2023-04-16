package com.foggyskies.server.databases.mongo.testpacage.settings

import com.foggyskies.server.databases.mongo.MongoCollection
import com.foggyskies.server.databases.mongo.testpacage.Logger
import com.foggyskies.server.databases.mongo.codes.testpacage.settings.SystemSettingsDataBase.NameCollections.logs
import com.foggyskies.server.plugin.KClient

object SystemSettingsDataBase {

    val db = KClient.getDatabase("system_settings")

    object NameCollections {
        const val logs = "logs"
    }

    object Logs : MongoCollection<com.foggyskies.server.databases.mongo.testpacage.Logger.LoggBody>(db, logs)

}

suspend fun SystemSettingsDataBase.Logs.insertLog(document: com.foggyskies.server.databases.mongo.testpacage.Logger.LoggBody) {
    db.getCollection<com.foggyskies.server.databases.mongo.testpacage.Logger.LoggBody>("logs").insertOne(document)
}