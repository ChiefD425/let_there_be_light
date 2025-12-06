package com.antigravity.lights.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.antigravity.lights.data.local.dao.DeviceDao
import com.antigravity.lights.data.local.dao.ScheduleDao
import com.antigravity.lights.data.local.entity.DeviceEntity
import com.antigravity.lights.data.local.entity.ScheduleEntity

@Database(
    entities = [DeviceEntity::class, ScheduleEntity::class],
    version = 2,
    exportSchema = false
)
abstract class LightDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun scheduleDao(): ScheduleDao
}
