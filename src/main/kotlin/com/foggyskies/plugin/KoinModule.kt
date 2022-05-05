package com.foggyskies.plugin

import com.foggyskies.DataBases
import com.foggyskies.chat.data.model.ImpAndDB
import com.foggyskies.chat.databases.content.ContentImpl
import com.foggyskies.chat.databases.main.AllCollectionImpl
import com.foggyskies.chat.databases.message.MessagesDBImpl
import com.foggyskies.chat.databases.newmessage.NewMessagesDBImpl
import com.foggyskies.chat.databases.subscribers.SubscribersImpl
import com.foggyskies.chat.newroom.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single(named(DataBases.MAIN)) {
        KMongo.createClient()
            .coroutine
            .getDatabase("petapp_db")
    }
    single(named(DataBases.MESSAGES)) {
        KMongo.createClient()
            .coroutine
            .getDatabase("messages_petapp_db")
    }
    single(named(DataBases.SUBSCRIBERS)) {
        KMongo.createClient()
            .coroutine
            .getDatabase("subscribers_petapp_db")
    }
    single(named(DataBases.CONTENT)) {
        KMongo.createClient()
            .coroutine
            .getDatabase("content_users_petapp_db")
    }
    single(named(DataBases.NEW_MESSAGE)) {
        KMongo.createClient()
            .coroutine
            .getDatabase("new_messages_db")
    }
//  ----------------------------------------------------------------------------

    single(named<ContentImpl>()) {
        ImpAndDB<ContentImpl>(db = get(named(DataBases.CONTENT)), impl = get())
    }
    single(named<MessagesDBImpl>()) {
        ImpAndDB<MessagesDBImpl>(db = get(named(DataBases.MESSAGES)), impl = get())
    }
    single(named<AllCollectionImpl>()) {
        ImpAndDB<AllCollectionImpl>(db = get(named(DataBases.MAIN)), impl = get())
    }
    single(named<SubscribersImpl>()) {
        ImpAndDB<SubscribersImpl>(db = get(named(DataBases.SUBSCRIBERS)), impl = get())
    }
    single(named<NewMessagesDBImpl>()) {
        ImpAndDB<NewMessagesDBImpl>(db = get(named(DataBases.NEW_MESSAGE)), impl = get())
    }
//  ----------------------------------------------------------------------------
    single {
        ContentImpl(get(named(DataBases.CONTENT)))
    }
    single {
        SubscribersImpl(get(named(DataBases.SUBSCRIBERS)))
    }
    single {
        MessagesDBImpl(get(named(DataBases.MESSAGES)))
    }
    single {
        AllCollectionImpl(get(named(DataBases.MAIN)))
    }
    single {
        NewMessagesDBImpl(get(named(DataBases.NEW_MESSAGE)))
    }
//  ----------------------------------------------------------------------------
    single {
        UserRoutController(
            content = get(named<ContentImpl>()),
            main = get(named<AllCollectionImpl>()),
            message = get(named<MessagesDBImpl>()),
            subscribers = get(named<SubscribersImpl>())
        )
    }
    single {
        CreateChatRoutController(
            allCollectionImpl = get(),
            messagesDB = get(named(DataBases.MESSAGES))
        )
    }
    single {
        NotifyRoutController(
            allCollectionImpl = get(),
            db = get(named(DataBases.MAIN))
        )
    }
    single {
        MessagesRoutController(
            main = get(named<AllCollectionImpl>()),
            message = get(named<MessagesDBImpl>()),
            new_message = get(named<NewMessagesDBImpl>())
        )
    }
    single {
        AuthRoutController(
            allCollectionImpl = get(),
            db = get(named(DataBases.MAIN))
        )
    }
    single {
        ContentRoutController(
            content = get(named<ContentImpl>()),
            main = get(named<AllCollectionImpl>())
        )
    }
}