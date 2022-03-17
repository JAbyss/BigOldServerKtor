package com.foggyskies.chat.extendfun

suspend fun isFalse(isTokenExist: Boolean, action: suspend () -> Unit){
    if (!isTokenExist){
        action()
    }
}