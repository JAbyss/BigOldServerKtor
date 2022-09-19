package com.foggyskies.server.plugin

import com.foggyskies.server.databases.mongo.main.models.LocationByIPDC
import com.foggyskies.client
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.util.network.*
import java.net.SocketAddress

suspend fun SocketAddress.getLocation(): LocationByIPDC {
    val response: LocationByIPDC = client.get("http://ipwho.is/${this.hostname}").body()
    client.close()
    return response
}