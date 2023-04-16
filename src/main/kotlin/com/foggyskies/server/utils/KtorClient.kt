package com.foggyskies.server.utils

import io.ktor.client.*
import io.ktor.client.engine.cio.*

val KtorClient
    get() = HttpClient(CIO)