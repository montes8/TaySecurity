package com.tay.taysecurity.repository.network


import com.tay.taysecurity.repository.network.manager.KtorApi
import com.tay.taysecurity.repository.network.model.TayLocationResponse
import com.tay.taysecurity.utils.uiTayJsonToObjet
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

class KmmService: KtorApi() {
    //val client = HttpClient()
  /*  suspend fun getLocation(): List<TayLocationResponse> = client.get{
        pathUrlGet("config/location")
    }.body()*/


    suspend fun getLocation(): List<TayLocationResponse> {
        var response = client.get {
            pathUrlGet("config/location")
        }
        return uiTayJsonToObjet(json = response.bodyAsText())
    }


}