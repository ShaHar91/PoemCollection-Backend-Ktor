package com.poemcollection.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            encodeDefaults = true // Will make sure that every field will be returned (as long as it has a default value)
            ignoreUnknownKeys = true // Will make sure that unsupported field that are in a request will not trigger an error and will just be ignored
        })

//        register(FormUrlEncoded, CustomFormUrlEncodedConverter)
    }
}

//object CustomFormUrlEncodedConverter : ContentConverter {
//    override suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Any? {
//
//        val stringContent = content.toInputStream().reader(charset).readText()
//
//        println(stringContent)
//
//        return null
//    }
//}