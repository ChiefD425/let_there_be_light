package com.antigravity.lights.domain.command

import com.antigravity.lights.data.network.LightProtocol
import com.antigravity.lights.data.network.UdpClient
import javax.inject.Inject

class LightCommandExecutor @Inject constructor(
    private val udpClient: UdpClient,
    private val protocol: LightProtocol
) {
    suspend fun turnOn(ip: String, port: Int = 5577) {
        udpClient.sendBroadcast(port, protocol.turnOn()) // Currently using broadcast for simplicity, should change to unicast
    }
    
    suspend fun turnOff(ip: String, port: Int = 5577) {
        udpClient.sendBroadcast(port, protocol.turnOff())
    }
    
    // In real app we would use TCP socket for persistent connection
}
