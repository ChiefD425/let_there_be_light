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

    suspend fun sendAndListen(port: Int, data: ByteArray, timeoutMs: Int = 2000): List<Pair<String, ByteArray>> = withContext(Dispatchers.IO) {
        val responses = mutableListOf<Pair<String, ByteArray>>()
        try {
            val socket = DatagramSocket()
            socket.broadcast = true
            socket.soTimeout = timeoutMs

            val packet = DatagramPacket(
                data,
                data.size,
                InetAddress.getByName("255.255.255.255"),
                port
            )
            socket.send(packet)

            val buffer = ByteArray(2048)
            val receivePacket = DatagramPacket(buffer, buffer.size)

            val endTime = System.currentTimeMillis() + timeoutMs
            while (System.currentTimeMillis() < endTime) {
                try {
                    socket.receive(receivePacket)
                    val ip = receivePacket.address.hostAddress
                    val responseData = receivePacket.data.copyOf(receivePacket.length)
                    if (ip != null) {
                        responses.add(ip to responseData)
                    }
                } catch (e: java.net.SocketTimeoutException) {
                    break // Timeout reached
                } catch (e: Exception) {
                    e.printStackTrace()
                    break
                }
            }
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        responses
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
