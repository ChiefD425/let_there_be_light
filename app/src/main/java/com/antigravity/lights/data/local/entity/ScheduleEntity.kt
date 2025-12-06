package com.antigravity.lights.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val timeInMillis: Long, // Time of day relative to midnight or absolute timestamp
    val action: String, // "ON", "OFF", "PATTERN"
    val daysOfWeek: Int, // Bitmask: 1=Sun, 2=Mon...
    val isEnabled: Boolean = true
)
