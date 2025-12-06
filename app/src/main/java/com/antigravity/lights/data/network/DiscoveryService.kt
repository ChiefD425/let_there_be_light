package com.antigravity.lights.data.network

import com.antigravity.lights.domain.model.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

import com.antigravity.lights.data.ble.BleScanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Singleton
class DiscoveryService @Inject constructor(
    private val udpClient: UdpClient,
    private val bleScanner: BleScanner
) {
    private val _discoveredDevices = MutableStateFlow<List<Device>>(emptyList())
    val discoveredDevices: StateFlow<List<Device>> = _discoveredDevices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

    fun log(msg: String) {
        val current = _logs.value.toMutableList()
        if (current.size > 50) current.removeAt(0)
        current.add(msg)
        _logs.value = current
    }

    suspend fun startDiscovery() {
        if (_isScanning.value) return
        _isScanning.value = true
        
        try {
            log("Starting discovery...")
            val foundDevices = mutableListOf<Device>()
            
            // Start BLE Scan
            val bleJob = CoroutineScope(Dispatchers.IO).launch {
                log("Starting BLE scan...")
                try {
                    bleScanner.scan().collect { device ->
                        if (!foundDevices.any { it.id == device.id }) {
                            foundDevices.add(device)
                            log("Found: ${device.name} [${device.id}]")
                            updateDiscoveredList(foundDevices)
                        }
                    }
                } catch (e: Exception) {
                    log("BLE Scan error: ${e.message}")
                }
            }

            // Scan for 5 seconds then stop
            delay(5000)
            bleJob.cancel()
            
            if (foundDevices.isEmpty()) {
                log("No devices found after scan.")
            } else {
                log("Scan complete. Found ${foundDevices.size} devices.")
            }
        } finally {
            _isScanning.value = false
        }
    }
    
    private fun updateDiscoveredList(foundDevices: List<Device>) {
        val currentList = _discoveredDevices.value.toMutableList()
        foundDevices.forEach { newDevice ->
            if (currentList.none { it.id == newDevice.id }) {
                currentList.add(newDevice)
            } else {
                val index = currentList.indexOfFirst { it.id == newDevice.id }
                if (index != -1) currentList[index] = newDevice
            }
        }
        _discoveredDevices.value = currentList
    }

    fun mockDiscovery() {
        // For testing UI without devices
        _discoveredDevices.value = listOf(
            Device("00:11:22:33:44:55", "Living Room Strip", "192.168.1.101", true),
            Device("AA:BB:CC:DD:EE:FF", "Kitchen Cabinet", "192.168.1.102", true)
        )
    }
}
