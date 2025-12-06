package com.antigravity.lights.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import javax.inject.Inject

class UdpClient @Inject constructor() {

    suspend fun sendBroadcast(port: Int, data: ByteArray) = withContext(Dispatchers.IO) {
        try {
            val socket = DatagramSocket()
            socket.broadcast = true
            val packet = DatagramPacket(
                data,
                data.size,
                InetAddress.getByName("255.255.255.255"),
                port
            )
            socket.send(packet)
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun listen(port: Int, onReceive: (String, ByteArray) -> Unit) = withContext(Dispatchers.IO) {
        try {
            val socket = DatagramSocket(port)
            val buffer = ByteArray(1024)
            val packet = DatagramPacket(buffer, buffer.size)

            while (true) { // TODO: Add proper cancellation
                socket.receive(packet)
                val ip = packet.address.hostAddress
                val data = packet.data.copyOf(packet.length)
                if (ip != null) {
                    onReceive(ip, data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
