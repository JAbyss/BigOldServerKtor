package com.foggyskies.server.plugin

import com.foggyskies.DataBases
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val uriMongo =
    "mongodb://testPetApp:563214789Qq@192.168.0.53:27017/?authSource=admin&replicaSet=rs1&directConnection=true"

val KClient = KMongo.createClient(uriMongo)
    .coroutine

val mainModule = module {
    single(named(DataBases.MAIN)) {
        KClient
            .getDatabase("petapp_db")
    }
    single(named(DataBases.MESSAGES)) {
        KClient
            .getDatabase("messages_petapp_db")
    }
    single(named(DataBases.SUBSCRIBERS)) {
        KClient
            .getDatabase("subscribers_petapp_db")
    }
    single(named(DataBases.CONTENT)) {
        KClient
            .getDatabase("content_users_petapp_db")
    }
    single(named(DataBases.NEW_MESSAGE)) {
        KClient
            .getDatabase("new_messages_db")
    }
    single(named(DataBases.CODES)) {
        KClient
            .getDatabase("codes_db")
    }
//  ----------------------------------------------------------------------------

//    single(named<ContentImpl>()) {
//        ImpAndDB<ContentImpl>(db = get(named(DataBases.CONTENT)), impl = get())
//    }
//    single(named<MessagesDBImpl>()) {
//        ImpAndDB<MessagesDBImpl>(db = get(named(DataBases.MESSAGES)), impl = get())
//    }
//    single(named<MainDBImpl>()) {
//        ImpAndDB<MainDBImpl>(db = get(named(DataBases.MAIN)), impl = get())
//    }
//    single(named<SubscribersImpl>()) {
//        ImpAndDB<SubscribersImpl>(db = get(named(DataBases.SUBSCRIBERS)), impl = get())
//    }
//    single(named<NewMessagesDBImpl>()) {
//        ImpAndDB<NewMessagesDBImpl>(db = get(named(DataBases.NEW_MESSAGE)), impl = get())
//    }
//    single(named<CodesDBImpl>()) {
//        ImpAndDB<CodesDBImpl>(db = get(named(DataBases.CODES)), impl = get())
//    }
//  ----------------------------------------------------------------------------
//    single {
//        ContentImpl(get(named(DataBases.CONTENT)))
//    }
//    single {
//        SubscribersImpl(get(named(DataBases.SUBSCRIBERS)))
//    }
//    single {
//        MessagesDBImpl(get(named(DataBases.MESSAGES)))
//    }
//    single {
//        MainDBImpl(get(named(DataBases.MAIN)))
//    }
//    single {
//        NewMessagesDBImpl(get(named(DataBases.NEW_MESSAGE)))
//    }
//    single {
//        CodesDBImpl(get(named(DataBases.CODES)))
//    }
//  ----------------------------------------------------------------------------
//    single {
//        UserRoutController(
//            content = get(named<ContentImpl>()),
//            main = get(named<MainDBImpl>()),
//            message = get(named<MessagesDBImpl>()),
//            subscribers = get(named<SubscribersImpl>()),
//            new_messages = get(named<NewMessagesDBImpl>())
//        )
//    }
//    single {
//        CreateChatRoutController(
//            mainDBImpl = get(),
//            messagesDB = get(named(DataBases.MESSAGES))
//        )
//    }
//    single {
//        NotifyRoutController(
//            mainDBImpl = get(),
//            db = get(named(DataBases.MAIN))
//        )
//    }
//    single {
//        MessagesRoutController(
//            main = get(named<MainDBImpl>()),
//            message = get(named<MessagesDBImpl>()),
//            new_message = get(named<NewMessagesDBImpl>())
//        )
//    }
//    single {
//        AuthRoutController(
//            mainDBImpl = get(),
//            db = get(named(DataBases.MAIN))
//        )
//    }
//    single {
//        ContentRoutController(
//            content = get(named<ContentImpl>()),
//            main = get(named<MainDBImpl>())
//        )
//    }
//    single {
//        CodesRoutController(
//            codes = get(named<CodesDBImpl>()),
//            main = get(named<MainDBImpl>())
//        )
//    }
}

//val contextMap = ConcurrentHashMap<Int, String>()
//
//var PipelineContext<Unit, ApplicationCall>.idRequest: String
//    get() = this.hashCode()
//    set(value) = { value }