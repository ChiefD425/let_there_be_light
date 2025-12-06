package com.antigravity.lights.domain.command

import com.antigravity.lights.data.ble.BleClient
import com.antigravity.lights.data.network.DiscoveryService
import com.antigravity.lights.data.network.LightProtocol
import com.antigravity.lights.data.network.UdpClient
import javax.inject.Inject

class LightCommandExecutor @Inject constructor(
    private val udpClient: UdpClient,
    private val bleClient: BleClient,
    private val discoveryService: DiscoveryService,
    private val protocol: LightProtocol
) {
    suspend fun turnOn(id: String) {
        if (id.contains(":")) { // Simple MAC check
             try {
                discoveryService.log("Turning ON $id...")
                bleClient.write(id, protocol.turnOn()) { msg -> discoveryService.log(msg) }
             } catch (e: Exception) {
                 discoveryService.log("Error turning ON: ${e.message}")
                 e.printStackTrace()
             }
        } else {
            udpClient.sendBroadcast(5577, protocol.turnOn())
        }
    }
    
    suspend fun turnOff(id: String) {
        if (id.contains(":")) {
             try {
                discoveryService.log("Turning OFF $id...")
                bleClient.write(id, protocol.turnOff()) { msg -> discoveryService.log(msg) }
             } catch (e: Exception) {
                 discoveryService.log("Error turning OFF: ${e.message}")
                 e.printStackTrace()
             }
        } else {
            udpClient.sendBroadcast(5577, protocol.turnOff())
        }
    }
    
    suspend fun setPattern(id: String, patternId: Int, speed: Int) {
        if (id.contains(":")) {
             try {
                discoveryService.log("Sending Pattern $patternId (Speed $speed) to $id...")
                bleClient.write(id, protocol.setPattern(patternId, speed)) { msg -> discoveryService.log(msg) }
             } catch (e: Exception) {
                 discoveryService.log("Error sending pattern: ${e.message}")
                 e.printStackTrace()
             }
        }
    }
}
