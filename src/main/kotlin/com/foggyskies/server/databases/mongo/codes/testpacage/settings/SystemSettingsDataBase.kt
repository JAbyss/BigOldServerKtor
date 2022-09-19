package com.foggyskies.server.databases.mongo.codes.testpacage.settings

import com.foggyskies.server.databases.mongo.MongoCollection
import com.foggyskies.server.databases.mongo.codes.testpacage.Logger
import com.foggyskies.server.databases.mongo.codes.testpacage.settings.SystemSettingsDataBase.NameCollections.logs
import com.foggyskies.server.plugin.KClient

object SystemSettingsDataBase {

    val db = KClient.getDatabase("system_settings")

    object NameCollections {
        const val logs = "logs"
    }

    object Logs : MongoCollection<Logger.LoggBody>(db, logs)

}

suspend fun SystemSettingsDataBase.Logs.insertLog(document: Logger.LoggBody) {
    db.getCollection<Logger.LoggBody>("logs").insertOne(document)
}