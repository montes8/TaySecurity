package com.tay.taysecurity.repository.network


import com.tay.taysecurity.repository.network.manager.KtorApi
import com.tay.taysecurity.repository.network.model.TayLocationResponse
import io.ktor.client.call.body
import io.ktor.client.request.get

class KmmService : KtorApi() {

    suspend fun getLocation(): List<TayLocationResponse> = client.get{
        pathUrlGet("config/location")
    }.body()
}