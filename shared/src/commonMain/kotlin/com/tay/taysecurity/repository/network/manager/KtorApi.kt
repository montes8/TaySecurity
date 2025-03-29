package com.tay.taysecurity.repository.network.manager


import io.ktor.client.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

const val BASE_URL = "669e-38-25-16-168.ngrok-free.app/service"

abstract class KtorApi {
    val client = HttpClient()
    fun HttpRequestBuilder.pathUrlGet(urlSecond: String){
        url {
            protocol = URLProtocol.HTTPS
            host = BASE_URL
            path(urlSecond)
           // parameters.append("token", "abc123")
            //headers { append(HttpHeaders.Authorization, "abc123")}
            contentType(ContentType.Application.Json)
        }
    }

    public inline fun <reified T>HttpRequestBuilder.pathUrlPost(path: String, body : T){
        url {
            protocol = URLProtocol.HTTPS
            host = BASE_URL
            path(path)
            // parameters.append("token", "abc123")
            //headers { append(HttpHeaders.Authorization, "abc123")}
            contentType(ContentType.Application.Json)//tipo de dato
            setBody(body) //metodos post body
        }
    }
}