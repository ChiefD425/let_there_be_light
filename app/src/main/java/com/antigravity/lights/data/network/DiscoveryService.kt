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

    suspend fun startDiscovery() {
        // Common ports for LED controllers
        val targetPorts = listOf(48899, 5577)
        // Common discovery payloads
        val payloads = listOf(
            "HF-A11ASSISTHREAD".toByteArray(), // MagicHome / Flux
            byteArrayOf(0x00) // Generic empty probe
        )

        targetPorts.forEach { port ->
            payloads.forEach { payload ->
                udpClient.sendBroadcast(port, payload)
            }
        }
        
        // Start listening (simplified for valid port)
        // In real app we need separate listener threads for specific ports
    }
    
    fun mockDiscovery() {
        // For testing UI without devices
        _discoveredDevices.value = listOf(
            Device("00:11:22:33:44:55", "Living Room Strip", "192.168.1.101", true),
            Device("AA:BB:CC:DD:EE:FF", "Kitchen Cabinet", "192.168.1.102", true)
        )
    }
}
