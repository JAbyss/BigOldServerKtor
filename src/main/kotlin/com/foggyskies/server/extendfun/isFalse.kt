package com.foggyskies.server.extendfun

suspend fun isFalse(isTokenExist: Boolean, action: suspend () -> Unit){
    if (!isTokenExist){
        action()
    }
}