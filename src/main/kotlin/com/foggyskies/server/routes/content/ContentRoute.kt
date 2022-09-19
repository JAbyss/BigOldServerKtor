package com.foggyskies.server.routes.content

import com.foggyskies.server.routes.content.requests.*
import io.ktor.server.routing.*

fun Route.contentRoute() {
//    val routController by inject<ContentRoutController>()



    route("/content") {

        addPostImage(true)
        getContentPreview(true)
        addCommentToPost(true)

        // FIXME TEST request
        getPosts(false)
        /////////////////////////////
        getComments(true)
        getLikedUsers(true)
        addLikeToPost(true)
        getInfoAboutOnePost(true)

//        post("/addPostImage") {
//            val post = call.receive<ContentRequestDC>()
//
//            val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
//            val isTokenExist = routController.checkOnExistToken(token.toString())
//            if (isTokenExist) {
//                routController.addNewContent(post, callback = {
//                    call.respond(HttpStatusCode.OK, it)
//                })
//            } else {
//                call.respondText(status = HttpStatusCode.BadRequest, text = "Токен не существует.")
//            }
//        }

//        get("/getContentPreview{idPageProfile}") {
//            val idPageProfile =
//                call.parameters["idPageProfile"] ?: call.respond(HttpStatusCode.BadRequest, "Id не получен.")
//
//            val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
//            val isTokenExist = routController.checkOnExistToken(token.toString())
//            if (isTokenExist) {
//                val content = routController.getFirstFiftyContent(idPageProfile.toString())
//                call.respond(HttpStatusCode.OK, content)
//            } else {
//                call.respondText(status = HttpStatusCode.BadRequest, text = "Токен не существует.")
//            }
//        }

//        post("/addCommentToPost{idPageProfile}{idPost}") {
//            val comment = call.receive<CommentDC>() ?: call.respond(HttpStatusCode.BadRequest, "Comment не получен.")
//
//            val idPageProfile =
//                call.parameters["idPageProfile"] ?: call.respond(HttpStatusCode.BadRequest, "IdPageProfile не получен.")
//            val idPost = call.parameters["idPost"] ?: call.respond(HttpStatusCode.BadRequest, "IdPost не получен.")
//
//            val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
//            val isTokenExist = routController.checkOnExistToken(token.toString())
//            if (isTokenExist) {
//                val sdf = SimpleDateFormat("hh:mm")
//                val currentDate = sdf.format(Date())
//                routController.addNewComment(
//                    idPageProfile.toString(),
//                    idPost.toString(),
//                    (comment as CommentDC).copy(id = ObjectId().toString(), date = currentDate)
//                )
//                call.respond(HttpStatusCode.OK)
//            }
//        }

//        get("/getPosts") {
//
//            val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
//            val isTokenExist = routController.checkOnExistToken(token.toString())
//            println(token)
//            if (isTokenExist) {
//                val posts = listOf(
//                routController.getInfoAboutOnePost(idPageProfile = "629256b71372bb3eb6256391", idPost = "629257e01372bb3eb6256394", token = token.toString())?.apply {
//                    description = if (description == "Описание публикации...") "" else description
//                },
//                routController.getInfoAboutOnePost(idPageProfile = "629256b71372bb3eb6256391", idPost = "62925d3b1372bb3eb6256395", token = token.toString())?.apply {
//                    description = if (description == "Описание публикации...") "" else description
//                },
//                routController.getInfoAboutOnePost(idPageProfile = "62914107cc47483b16951b0b", idPost = "62accc9120936e5fbab1ca66", token = token.toString())?.apply {
//                    description = if (description == "Описание публикации...") "" else description
//                })
////                println(posts)
////                val posts = routController.getPosts(token.toString())
//                call.respond(HttpStatusCode.OK, posts)
//            } else {
//                call.respondText(status = HttpStatusCode.OK, text = token.toString())
//            }
//        }
//        get("/getComments{idPageProfile}{idPost}") {
//            val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
//
//            val idPageProfile =
//                call.parameters["idPageProfile"] ?: call.respond(HttpStatusCode.BadRequest, "IdPageProfile не получен.")
//            val idPost = call.parameters["idPost"] ?: call.respond(HttpStatusCode.BadRequest, "IdPost не получен.")
//
//            val isTokenExist = routController.checkOnExistToken(token.toString())
//            if (isTokenExist) {
//                val comments = routController.getOnePostComments(idPageProfile.toString(), idPost.toString())
//                call.respond(comments)
//            }
//        }
//        get("/getLikedUsers{idPageProfile}{idPost}") {
//            val token = call.request.headers["Auth"] ?: call.respondText(
//                status = HttpStatusCode.BadRequest,
//                text = "Токен не получен."
//            )
//
//            val idPageProfile =
//                call.parameters["idPageProfile"] ?: call.respondText(
//                    status = HttpStatusCode.BadRequest,
//                    text = "IdPageProfile не получен."
//                )
//            val idPost = call.parameters["idPost"] ?: call.respondText(
//                status = HttpStatusCode.BadRequest,
//                text = "IdPost не получен."
//            )
//
//            val isTokenExist = routController.checkOnExistToken(token.toString())
//            if (isTokenExist) {
//                val likedUsers = routController.getAllLikedUsers(idPageProfile.toString(), idPost.toString())
//                call.respond(likedUsers)
//            }
//        }
//        get("/addLikeToPost{idPageProfile}{idPost}") {
//            val token = call.request.headers["Auth"] ?: call.respond(HttpStatusCode.BadRequest, "Токен не получен.")
//
//            val idPageProfile =
//                call.parameters["idPageProfile"] ?: call.respondText(
//                    status = HttpStatusCode.BadRequest,
//                    text = "IdPageProfile не получен."
//                )
//            val idPost = call.parameters["idPost"] ?: call.respondText(
//                status = HttpStatusCode.BadRequest,
//                text = "IdPost не получен."
//            )
//
//            val isTokenExist = routController.checkOnExistToken(token.toString())
//            if (isTokenExist) {
//                val isLiked =
//                    routController.addLikeToPost(idPageProfile.toString(), idPost.toString(), token.toString())
//                call.respond(HttpStatusCode.OK, isLiked)
//            } else {
//                call.respondText(status = HttpStatusCode.BadRequest, text = "Token не существует.")
//            }
//        }
//        get("/getInfoAboutOnePost{idPageProfile}{idPost}") {
//            val token = call.request.headers["Auth"] ?: call.respondText(
//                status = HttpStatusCode.BadRequest,
//                text = "Токен не получен."
//            )
//
//            val idPageProfile =
//                call.parameters["idPageProfile"] ?: call.respondText(
//                    status = HttpStatusCode.BadRequest,
//                    text = "IdPageProfile не получен."
//                )
//            val idPost = call.parameters["idPost"] ?: call.respondText(
//                status = HttpStatusCode.BadRequest,
//                text = "IdPost не получен."
//            )
//
//            val isTokenExist = routController.checkOnExistToken(token.toString())
//            if (isTokenExist) {
//                val info =
//                    routController.getInfoAboutOnePost(idPageProfile.toString(), idPost.toString(), token.toString())
//                call.respond(HttpStatusCode.OK, info!!)
//            } else {
//                call.respondText(status = HttpStatusCode.BadRequest, text = "Token не существует.")
//            }
//        }

    }
}

//suspend fun main() {
//    CoroutineScope(Dispatchers.Default).launch {
//        var counter = 0
//        val list = ConcurrentList<Deferred<Int>>()
//        while (true) {
//            delay(5)
////            list.add(async {
//            async {
//                HttpClient(CIO) {
//                    install(JsonFeature) {
//                        serializer = KotlinxSerializer()
//                    }
//                    install(HttpTimeout) {
//                        requestTimeoutMillis = 30000
//                    }
//                }.use {
//                    val a = it.get<HttpResponse>("http://94.41.84.183:2526/content/getPosts") {
//                        this.headers["Auth"] = "6294b2095b296c7b5d2d171c"
//                    }
////                return@async a.status.value
//                }
//
//            }
//        }
////            if (list.size > 3000){
////                break
////            }
//    }.join()
////        list.awaitAll()
////        list.forEach {
////            println(it.getCompleted())
////        }
//}