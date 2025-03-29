package com.tay.taysecurity.usecases.repository

import com.tay.taysecurity.repository.network.api.TayDataNetwork
import com.tay.taysecurity.usecases.repository.network.ITayDataNetwork

class TayDataUseCase  {

    private val iDataNetwork : ITayDataNetwork = TayDataNetwork()

    suspend fun loadLocations() = iDataNetwork.loadLocation()

}