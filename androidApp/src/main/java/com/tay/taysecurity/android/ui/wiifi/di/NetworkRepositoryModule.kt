package com.tay.taysecurity.android.ui.wiifi.di

import com.tay.taysecurity.android.ui.wiifi.repository.NetworkScannerRepository
import com.tay.taysecurity.android.ui.wiifi.repository.NetworkScannerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNetworkScannerRepository(
        impl: NetworkScannerRepositoryImpl
    ): NetworkScannerRepository
}
