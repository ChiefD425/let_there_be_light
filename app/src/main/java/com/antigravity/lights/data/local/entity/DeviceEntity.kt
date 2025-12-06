package com.antigravity.lights.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val id: String, // MAC address or unique ID
    val name: String,
    val ipAddress: String,
    val type: String, // "wl.smartled", etc.
    val isOnline: Boolean = false,
    val lastSeen: Long = 0,
    val groupId: String? = null
)
