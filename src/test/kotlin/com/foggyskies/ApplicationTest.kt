package com.foggyskies

import com.foggyskies.server.databases.mongo.main.models.IdUserReceiver
import com.foggyskies.server.databases.mongo.main.models.LoginUserDC
import com.foggyskies.server.plugin.SystemRouting
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testAcceptFriend() = testApplication {
        val response = client.get("/avatar")
        println(response)
    }

    @Test
    fun auth() = testApplication {

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post(SystemRouting.AuthRoute.auth) {
            contentType(ContentType.Application.Json)
            setBody(
                LoginUserDC(
                    "Mabys",
                    "z)<fT0T.v^8B>utruG\"HTP2u{;:\\T2[n-C-s%5:PZFsLT]h6v`;<P)xzU]DKFET4;s&rJq<.=gwH@5ST&^3m<\$\"Dl!:ch0^Ig%=(x1 ?^aFiKUHOgLjdBdeS-JVrtbjmQYiDY:p[?LqhCZ%"
                )
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(2, response.bodyAsText().split("|").size)
    }

    @Test
    fun addFriend() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post(SystemRouting.UserRoute.addFriend) {
            contentType(ContentType.Application.Json)
            headers["Auth"] = "62fcf87ab660ef3b406a33e2"
            setBody(
                IdUserReceiver(
                    id = "62ee819b3c91626938ae8938"
                )
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun acceptFriend() = testApplication {
        val response = client.post(SystemRouting.UserRoute.acceptRequestFriend) {
            headers["Auth"] = "62fd1abf75cfb66ae4241c3f"
            setBody(
                "62d6f66a3689f049b006e635"
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun getRequestFriend() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.get(SystemRouting.UserRoute.getRequestsFriends) {
            contentType(ContentType.Application.Json)
            headers["Auth"] = "62fd1abf75cfb66ae4241c3f"
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

}