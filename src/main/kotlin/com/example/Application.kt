package com.example

import com.example.data.models.User
import com.example.data.models.UserType
import com.example.data.repository.user.UserRepository
import com.example.data.repository.user.UserRepositoryImpl
import com.example.plugins.*
import com.example.util.Constants.DATABASE_NAME
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.server.application.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}
@Suppress("unused")
@OptIn(DelicateCoroutinesApi::class)
fun Application.module() {

    configureKoin()
    configureAuth()
    configureRouting()
    configureSerialization()
    configureMonitoring()




}
