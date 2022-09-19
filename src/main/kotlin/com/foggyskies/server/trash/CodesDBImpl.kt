//package com.foggyskies.server.databases.mongo.codes
//
//import com.foggyskies.server.databases.mongo.codes.datasources.BlockCollectionDataSource
//import com.foggyskies.server.databases.mongo.codes.datasources.InfoCollectionDataSource
//import com.foggyskies.server.databases.mongo.codes.datasources.RecoveryCollectionDataSource
//import com.foggyskies.server.databases.mongo.codes.datasources.VerifyCollectionDataSource
//import com.foggyskies.server.databases.mongo.codes.models.InfoLockDC
//import com.foggyskies.server.databases.mongo.codes.models.LockCodeDC
//import com.foggyskies.server.databases.mongo.codes.models.VerifyCodeDC
//import org.litote.kmongo.coroutine.CoroutineDatabase
//import org.litote.kmongo.eq
//
//
//class CodesDBImpl(
//    val db: CoroutineDatabase
//) : BlockCollectionDataSource, InfoCollectionDataSource, VerifyCollectionDataSource, RecoveryCollectionDataSource {
//
//
//
//    override suspend fun insertLockCode(lockCode: LockCodeDC) {
//        db.getCollection<LockCodeDC>("block_codes").insertOne(lockCode)
//    }
//
//    override suspend fun getLockCode(code: String): LockCodeDC? {
//        return db.getCollection<LockCodeDC>("block_codes").findOne(LockCodeDC::id eq code)
//    }
//
//    override suspend fun deleteLockCode(code: String) {
//        db.getCollection<LockCodeDC>("block_codes").deleteOne(LockCodeDC::id eq code)
//    }
//
//    override suspend fun getLockById(idLock: String): InfoLockDC? {
//        return db.getCollection<InfoLockDC>("info").findOne(InfoLockDC::id eq idLock)
//    }
//
//    override suspend fun insertVerifyCode(verifyCode: VerifyCodeDC) {
//        db.getCollection<VerifyCodeDC>("verify_codes").insertOne(verifyCode)
//    }
//
//    override suspend fun getVerifyCode(email: String): String? {
//        return db.getCollection<VerifyCodeDC>("verify_codes").findOne(VerifyCodeDC::email eq email)?.code
//    }
//
//    override suspend fun deleteVerifyCode(email: String) {
//        db.getCollection<VerifyCodeDC>("verify_codes").deleteOne(VerifyCodeDC::email eq email)
//    }
//}