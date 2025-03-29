package com.tay.taysecurity.repository.network.manager

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

const val BASE_URL = "https://w7b1f7dp-8080.brs.devtunnels.ms/service/"

abstract class KtorApi {
    val client = HttpClient {
        install(ContentNegotiation){
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }
    }


    fun HttpRequestBuilder.pathUrlGet(urlSecond: String){
        url {
            takeFrom(BASE_URL+urlSecond)
            //path(path)
            parameter("api_key", "API_KEY")//query
            header("Authorization", "Bearer abc123")//header
            contentType(ContentType.Application.Json)//tipo de dato
        }
    }

    public inline fun <reified T>HttpRequestBuilder.pathUrlPost(path: String, body : T){
        url {
            takeFrom(BASE_URL)
            path(path)
            parameter("api_key", "API_KEY")//query
            header("Authorization", "Bearer abc123")//header
            contentType(ContentType.Application.Json)//tipo de dato
            setBody(body) //metodos post body
        }
    }
}