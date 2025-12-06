package com.antigravity.lights.data.repository

import com.antigravity.lights.data.local.dao.DeviceDao
import com.antigravity.lights.data.local.entity.DeviceEntity
import com.antigravity.lights.domain.model.Device
import com.antigravity.lights.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val dao: DeviceDao
) : DeviceRepository {

    override fun getAllDevices(): Flow<List<Device>> {
        return dao.getAllDevices().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addDevice(device: Device) {
        dao.insertDevice(device.toEntity())
    }

    override suspend fun deleteDevice(device: Device) {
        dao.deleteDevice(device.toEntity())
    }
    
    override suspend fun updateDeviceStatus(id: String, isOnline: Boolean) {
        val entity = dao.getDeviceById(id)
        entity?.let {
            dao.updateDevice(it.copy(isOnline = isOnline, lastSeen = System.currentTimeMillis()))
        }
    }

    // Mappers
    private fun DeviceEntity.toDomain(): Device {
        return Device(
            id = id,
            name = name,
            ipAddress = ipAddress,
            isOnline = isOnline
        )
    }

    private fun Device.toEntity(): DeviceEntity {
        return DeviceEntity(
            id = id,
            name = name,
            ipAddress = ipAddress,
            isOnline = isOnline,
            type = "wl.smartled" // Default
        )
    }
}
