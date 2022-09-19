package com.foggyskies.server.routes.content.models

import com.foggyskies.server.databases.mongo.content.models.CommentDC
import com.foggyskies.server.databases.mongo.main.models.UserIUSI

@kotlinx.serialization.Serializable
data class FormattedCommentDC(
    val users: HashMap<String, UserIUSI>,
    val comments: List<CommentDC>
)