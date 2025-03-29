package com.tay.taysecurity.usecases.repository.network

import com.tay.taysecurity.model.TayLocationModel


interface ITayDataNetwork {
    suspend fun loadLocation( ): List<TayLocationModel>

}