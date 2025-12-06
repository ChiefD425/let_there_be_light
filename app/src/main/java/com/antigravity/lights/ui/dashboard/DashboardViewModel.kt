package com.antigravity.lights.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.lights.data.network.DiscoveryService
import com.antigravity.lights.domain.command.LightCommandExecutor
import com.antigravity.lights.domain.model.Device
import com.antigravity.lights.domain.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val discoveryService: DiscoveryService,
    private val commandExecutor: LightCommandExecutor
) : ViewModel() {

    // Merge local devices and network discovered devices
    val uiState: StateFlow<DashboardUiState> = combine(
        deviceRepository.getAllDevices(),
        discoveryService.discoveredDevices,
        discoveryService.isScanning,
        discoveryService.logs
    ) { local, network, scanning, logs ->
        DashboardUiState.Success(
            knownDevices = local,
            discoveredDevices = network.filter { n -> local.none { it.id == n.id } },
            isScanning = scanning,
            logs = logs
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState.Loading
    )

    fun scanForDevices() {
        viewModelScope.launch {
            discoveryService.startDiscovery()
            // discoveryService.mockDiscovery() // For testing
        }
    }
    
    fun addDevice(device: Device) {
        viewModelScope.launch {
            deviceRepository.addDevice(device)
        }
    }

    fun toggleDevice(device: Device) {
        viewModelScope.launch {
            // Optimistic update
            deviceRepository.updateDeviceStatus(device.id, !device.isOnline) 
            // Send command
             if (device.isOnline) { // Was online, turning off
                 commandExecutor.turnOff(device.ipAddress)
             } else {
                 commandExecutor.turnOn(device.ipAddress)
             }
        }
    }
}

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(
        val knownDevices: List<Device>,
        val discoveredDevices: List<Device>,
        val isScanning: Boolean = false,
        val logs: List<String> = emptyList()
    ) : DashboardUiState()
}
