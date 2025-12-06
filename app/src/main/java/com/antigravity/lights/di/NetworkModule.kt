package com.antigravity.lights.di

import com.antigravity.lights.data.network.LightProtocol
import com.antigravity.lights.data.network.SmartLedProtocol
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindLightProtocol(
        smartLedProtocol: SmartLedProtocol
    ): LightProtocol
}
