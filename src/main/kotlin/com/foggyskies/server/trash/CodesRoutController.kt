//package com.foggyskies.server.routes.auth
//
//import com.foggyskies.ServerDate
//import com.foggyskies.server.data.model.ImpAndDB
//import com.foggyskies.server.databases.mongo.codes.CodesDB.deleteCode
//import com.foggyskies.server.databases.mongo.codes.CodesDB.insertCodes
//import com.foggyskies.server.databases.mongo.codes.CodesDBImpl
//import com.foggyskies.server.databases.mongo.codes.models.LockCodeDC
//import com.foggyskies.server.databases.mongo.codes.models.VerifyCodeDC
//import com.foggyskies.server.databases.mongo.main.MainDBImpl
//import com.foggyskies.server.databases.mongo.main.models.BlockUserDC
//
//class CodesRoutController(
//    val codes: ImpAndDB<CodesDBImpl>,
//    private val main: ImpAndDB<MainDBImpl>
//) {
//
//    suspend fun getLockCode(code: String): LockCodeDC? {
//        return codes.impl.getLockCode(code)
//    }
//
//    suspend fun blockUser(lockCode: LockCodeDC) {
//
//        val infoLock = codes.impl.getLockById(lockCode.lock_code)!!
//        val block = BlockUserDC(
//            lock_code = infoLock.id,
//            time_lock = ServerDate.fullDate,
//            time_unlock = infoLock.time_to_block
//        )
//        main.impl.giveBlockByIdUser(lockCode.idUser, block)
//        main.impl.lockUser(lockCode.idUser, true)
//        deleteCode<LockCodeDC>(lockCode.id)
//    }
//
//    suspend fun unauthorizeUser(idUser: String) {
//        main.impl.deleteTokenByIdUser(idUser)
//    }
//
//    suspend fun getVerifyCode(email: String): String? {
//        val code = codes.impl.getVerifyCode(email)
//        code?.let {
//            deleteVerifyCode(email)
//        }
//        return code
//    }
//
//    suspend fun deleteVerifyCode(email: String) {
//        deleteCode<VerifyCodeDC>(email)
//    }
//
//    suspend fun insertVerifyCode(verifyCode: VerifyCodeDC) {
//        insertCodes(verifyCode)
//    }
//
//    suspend fun insertLockCode(lockCode: LockCodeDC) {
//        insertCodes(lockCode)
//    }
//}