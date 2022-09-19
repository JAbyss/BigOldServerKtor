package com.foggyskies.server.databases.mongo.main.models

@kotlinx.serialization.Serializable
data class ConnectionDC(
    val asn: String,
    val org: String,
    val isp: String,
    val domain: String
)

@kotlinx.serialization.Serializable
data class TimeZoneDC(
    val id: String,
    val abbr: String,
    val is_dst: Boolean,
    val offset: String,
    val utc: String,
    val current_time: String
)

@kotlinx.serialization.Serializable
data class LocationByIPDC(
    val ip: String,
    val country: String,
    val region: String,
    val city: String,
    val connection: ConnectionDC,
    val timezone: TimeZoneDC
)
