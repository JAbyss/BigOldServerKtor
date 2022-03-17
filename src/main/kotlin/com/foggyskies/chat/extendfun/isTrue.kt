package com.foggyskies.chat.extendfun

suspend fun isTrue(isTokenExist: Boolean, action: suspend () -> Unit){
    if (isTokenExist){
        action()
    }
}