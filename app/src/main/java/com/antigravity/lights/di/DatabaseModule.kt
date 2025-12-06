package com.antigravity.lights.di

import android.content.Context
import androidx.room.Room
import com.antigravity.lights.data.local.LightDatabase
import com.antigravity.lights.data.local.dao.DeviceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideLightDatabase(@ApplicationContext context: Context): LightDatabase {
        return Room.databaseBuilder(
            context,
            LightDatabase::class.java,
            "light_controller.db"
        ).build()
    }

    @Provides
    fun provideDeviceDao(database: LightDatabase): DeviceDao {
        return database.deviceDao()
    }
}
