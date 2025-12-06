package com.antigravity.lights.ui.scheduler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.lights.data.local.LightDatabase
import com.antigravity.lights.data.local.entity.ScheduleEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchedulerViewModel @Inject constructor(
    private val database: LightDatabase
) : ViewModel() {

    // Simplified: No repository for Schedules yet
    val schedules = MutableStateFlow<List<ScheduleEntity>>(emptyList())

    init {
        // Load some dummy or all schedules
        viewModelScope.launch {
            // In a real app we'd observe a Flow from DAO
        }
    }

    fun addSchedule() {
        viewModelScope.launch {
            database.scheduleDao().insertSchedule(
                ScheduleEntity(
                    deviceId = "all", // Global schedule for now
                    timeInMillis = System.currentTimeMillis() + 60000,
                    action = "ON",
                    daysOfWeek = 127
                )
            )
        }
    }
}
