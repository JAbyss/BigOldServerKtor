package com.foggyskies.server.databases.mongo.testpacage.codes

import com.foggyskies.server.databases.mongo.codes.models.InfoLockDC
import com.foggyskies.server.databases.mongo.codes.models.LockCodeDC
import com.foggyskies.server.databases.mongo.codes.models.RecoveryCodeDC
import com.foggyskies.server.databases.mongo.codes.models.VerifyCodeDC
import com.foggyskies.server.databases.mongo.testpacage.codes.CodesDataBase.NameCollections.block
import com.foggyskies.server.databases.mongo.testpacage.codes.CodesDataBase.NameCollections.info
import com.foggyskies.server.databases.mongo.testpacage.codes.CodesDataBase.NameCollections.recovery
import com.foggyskies.server.databases.mongo.testpacage.codes.CodesDataBase.NameCollections.verify
import com.foggyskies.server.plugin.KClient
import org.litote.kmongo.coroutine.CoroutineCollection

object CodesDataBase {

    val db = KClient.getDatabase("codes_db")

    object NameCollections{

        const val block = "block_codes"
        const val info = "info"
        const val recovery = "recovery_codes"
        const val verify = "verify_codes"
    }

    suspend inline fun <reified T : Any> getCodeByID(id: String): T? {
        return com.foggyskies.server.databases.mongo.testpacage.codes.CodesDataBase.getCollections<T>().findOne("{ _id: '$id'}")
    }

    suspend inline fun <reified T : Any> getCodeByIdUser(idUser: String): T? {
        return com.foggyskies.server.databases.mongo.testpacage.codes.CodesDataBase.getCollections<T>().findOne("{ idUser: '$idUser'}")
    }

    suspend inline fun <reified T : Any> insertCodes(code: T) {
        com.foggyskies.server.databases.mongo.testpacage.codes.CodesDataBase.getCollections<T>().insertOne(code)
    }

    suspend inline fun <reified T : Any> deleteCode(id: String) {
        com.foggyskies.server.databases.mongo.testpacage.codes.CodesDataBase.getCollections<T>().deleteOne("{ _id: '$id'}")
    }

    inline fun <reified T : Any> getCollections(): CoroutineCollection<T> {
        return com.foggyskies.server.databases.mongo.testpacage.codes.CodesDataBase.db.getCollection(com.foggyskies.server.databases.mongo.testpacage.codes.CodesDataBase.nameColl<T>())
    }

    inline fun <reified T> nameColl(): String = when (T::class) {
        LockCodeDC::class -> {
            block
        }
        VerifyCodeDC::class -> {
            verify
        }
        InfoLockDC::class -> {
            info
        }
        RecoveryCodeDC::class -> {
            recovery
        }
        else -> {
            throw error("Нельзя использовать, данный тип")
        }
    }
}