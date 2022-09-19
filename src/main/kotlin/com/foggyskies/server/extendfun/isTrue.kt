package com.foggyskies.server.extendfun

suspend fun isTrue(isTokenExist: Boolean, action: suspend () -> Unit){
    if (isTokenExist){
        action()
    }
}