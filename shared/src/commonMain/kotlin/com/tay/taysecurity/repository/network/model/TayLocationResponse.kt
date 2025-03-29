package com.tay.taysecurity.repository.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.tay.taysecurity.model.TayLocationModel

@Serializable
data class TayLocationResponse(
    @SerialName("id")
    var id : String?,
    @SerialName("title")
    var latitude : String?,
    @SerialName("description")
    var longitude : String?
){
    companion object{
        fun loadToLocations(response : List<TayLocationResponse>) = response.map {
            TayLocationModel(id = it.id?:"",
                latitude = it.latitude?:"0.0",
                longitude = it.longitude?:"0.0")
        }
    }
}