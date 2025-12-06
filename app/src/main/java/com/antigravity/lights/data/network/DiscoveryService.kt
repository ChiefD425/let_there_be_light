package com.antigravity.lights.data.network

import com.antigravity.lights.domain.model.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscoveryService @Inject constructor(
    private val udpClient: UdpClient
) {
    private val _discoveredDevices = MutableStateFlow<List<Device>>(emptyList())
    val discoveredDevices: StateFlow<List<Device>> = _discoveredDevices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

    private fun log(msg: String) {
        val current = _logs.value.toMutableList()
        if (current.size > 50) current.removeAt(0)
        current.add(msg)
        _logs.value = current
    }

    suspend fun startDiscovery() {
        if (_isScanning.value) return
        _isScanning.value = true
        
        try {
            // Common ports for LED controllers
            val targetPorts = listOf(48899, 5577)
            // Common discovery payloads
            val payloads = listOf(
                "HF-A11ASSISTHREAD".toByteArray(), // MagicHome / Flux
            )

            val foundDevices = mutableListOf<Device>()
            
            log("Starting discovery on ports: $targetPorts")

            targetPorts.forEach { port ->
                payloads.forEach { payload ->
                    log("Sending broadcast to $port...")
                    val responses = udpClient.sendAndListen(port, payload, 2000, 
                        onLog = { msg -> log(msg) }
                    )
                    log("Received ${responses.size} responses on port $port")
                    responses.forEach { (ip, data) ->
                        val deviceStr = String(data)
                        log("Response from $ip: $deviceStr")
                        
                        val components = deviceStr.split(",")
                        val id = if (components.size > 1) components[1] else ip
                        val name = if (components.size > 2) components[2] else "Light ($ip)"
                        
                        foundDevices.add(Device(
                            id = id,
                            name = name,
                            ipAddress = ip,
                            isOnline = true
                        ))
                    }
                }
            }
            
            // Allow Mock for testing if no real devices found
            if (foundDevices.isEmpty()) {
                 // Un-comment to fake it for the user if they have no hardware yet
                 // foundDevices.addAll(listOf(
                 //    Device("MOCK-01", "Mock Light 1", "192.168.1.50", true),
                 //    Device("MOCK-02", "Mock Light 2", "192.168.1.51", true)
                 // ))
            }
            
            // Update flow with NEW unique devices
            val currentList = _discoveredDevices.value.toMutableList()
            foundDevices.forEach { newDevice ->
                if (currentList.none { it.id == newDevice.id }) {
                    currentList.add(newDevice)
                } else {
                    // Update existing?
                    val index = currentList.indexOfFirst { it.id == newDevice.id }
                    if (index != -1) currentList[index] = newDevice
                }
            }
            _discoveredDevices.value = currentList
            
        } finally {
            _isScanning.value = false
        }
    }
    
    fun mockDiscovery() {
        // For testing UI without devices
        _discoveredDevices.value = listOf(
            Device("00:11:22:33:44:55", "Living Room Strip", "192.168.1.101", true),
            Device("AA:BB:CC:DD:EE:FF", "Kitchen Cabinet", "192.168.1.102", true)
        )
    }
}
