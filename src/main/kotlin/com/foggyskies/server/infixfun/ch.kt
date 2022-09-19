package com.foggyskies.server.infixfun

import com.foggyskies.server.databases.mongo.main.models.Token
import kotlin.reflect.KSuspendFunction0

suspend infix fun Boolean.ch(value: KSuspendFunction0<Token?>) = if (this) value() else Token.Empty