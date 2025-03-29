package com.tay.taysecurity.repository.network.api

import com.tay.taysecurity.repository.network.KmmService
import com.tay.taysecurity.model.TayLocationModel
import com.tay.taysecurity.repository.network.model.TayLocationResponse
import com.tay.taysecurity.usecases.repository.network.ITayDataNetwork

class TayDataNetwork : ITayDataNetwork {

    private val apiService: KmmService = KmmService()

    override suspend fun loadLocation() : List<TayLocationModel> {
        return TayLocationResponse.loadToLocations(apiService.getLocation())
    }

}