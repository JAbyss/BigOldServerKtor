package com.foggyskies.server.plugin

object SystemRouting {

    const val ipServer = "http://109.195.147.44:2525"

    object Files{

        const val baseDir = "/serverData"

        const val chats = "/chats"
        const val profilesAvatars = "/profilesAvatars"
        const val profilesContent = "/profilesContent"


        const val userAvatars = "/userAvatars"
    }

    object Images {
        const val BASE_DIR = "images"
        const val profiles_avatars = "profiles_avatar"
        const val AVATARS = "avatars"
    }

    object Recovery{
        const val blockAccount = "/blockAccount?code="
    }

    object UserRoute {
        const val addFriend = "/addFriend"
        const val createMainSocket = "/createMainSocket"
        const val muteChat = "/muteChat"
        const val getChat = "/getChats"
        const val getFriends = "/getFriends"
        const val getAvatar = "/avatar"
        const val changeAvatar = "/changeAvatar"
        const val getPagesProfileByIdUser = "/getPagesProfileByIdUser{idUser}"
        const val getPagesProfile = "/getPagesProfile"
        const val changeAvatarProfile = "/changeAvatarProfile"
        const val getRequestsFriends = "/getRequestsFriends"
        const val getNewMessages = "/getNewMessages"
        const val logOut = "/logOut"
        const val acceptRequestFriend = "/acceptRequestFriend"
        const val searchUser = "/searchUser"
        const val deletePageProfile = "/deletePageProfile"
    }

    object AuthRoute {
        const val generateCode = "/generateCode"
        const val registration = "/registration"
        const val checkToken = "/checkToken"
        const val auth = "/auth"
        const val blockAccount = "/blockAccount{code}"
    }

    object ContentRoute{
        const val addPostImage = "/addPostImage"
        const val getContentPreview = "/getContentPreview{idPageProfile}"
        const val addCommentToPost = "/addCommentToPost"
        const val getPosts = "/getPosts"
        const val getComments = "/getComments"
        const val getLikedUsers = "/getLikedUsers"
        const val addLikeToPost = "/addLikeToPost"
        const val getInfoAboutOnePost = "/getInfoAboutOnePost"
    }

    object CloudRoute{
        const val fileTree = "/fileTree"
    }

    object ChatRoute{
        const val createChat = "/createChat"
        const val createChatSession = "/createChatSession"
        const val deleteMessage = "/deleteMessage"
        const val editMessage = "/editMessage"
        const val sendMessageWithContent = "/sendMessageWithContent"
        const val getNewMessages = "/getNewMessages"

        const val chatSessions = "/chatSessions"
    }
}