package com.antigravity.lights.domain.repository

import com.antigravity.lights.domain.model.Device
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun getAllDevices(): Flow<List<Device>>
    suspend fun addDevice(device: Device)
    suspend fun deleteDevice(device: Device)
    suspend fun updateDeviceStatus(id: String, isOnline: Boolean)
}
