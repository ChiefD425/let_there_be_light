package com.antigravity.lights.data.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class BleClient @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val bluetoothAdapter = context.getSystemService(BluetoothManager::class.java)?.adapter

    // Common Lotus Lantern / Triones Characteristic UUIDs for WRITE
    // Main candidates: 0000ffd9-..., 0000fff3-...
    private val WRITE_CHAR_UUIDS = listOf(
        UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb"), // ELK-BLEDOM common
        UUID.fromString("0000ffd9-0000-1000-8000-00805f9b34fb"), // Triones / HappyLighting
        UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")  // Generic UART
    )

    @SuppressLint("MissingPermission")
    suspend fun write(macAddress: String, data: ByteArray, onLog: (String) -> Unit = {}) = suspendCancellableCoroutine<Unit> { continuation ->
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            onLog("Bluetooth not enabled")
            continuation.resumeWithException(Exception("Bluetooth disabled"))
            return@suspendCancellableCoroutine
        }

        val device = bluetoothAdapter.getRemoteDevice(macAddress)
        onLog("Connecting to $macAddress...")
        
        var connectedGatt: BluetoothGatt? = null
        
        val gattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    onLog("Connected to GATT. Discovering services...")
                    connectedGatt = gatt
                    gatt.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    onLog("Disconnected from GATT.")
                    gatt.close()
                    if (continuation.isActive) {
                        continuation.resumeWithException(Exception("Disconnected"))
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    onLog("Services discovered.")
                    var targetChar: BluetoothGattCharacteristic? = null
                    
                    // Find a matching write characteristic
                    outer@ for (service in gatt.services) {
                        for (charUuid in WRITE_CHAR_UUIDS) {
                            val charac = service.getCharacteristic(charUuid)
                            if (charac != null) {
                                targetChar = charac
                                onLog("Found Write Characteristic: $charUuid")
                                break@outer
                            }
                        }
                    }

                    if (targetChar != null) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                             gatt.writeCharacteristic(targetChar, data, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
                        } else {
                            targetChar.value = data
                            targetChar.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
                            gatt.writeCharacteristic(targetChar)
                        }
                         onLog("Command written to characteristic.")
                         // We might want to wait for onCharacteristicWrite but often NO_RESPONSE is faster/easier for lights
                         // Resume success immediately for fire-and-forget
                         if (continuation.isActive) {
                             continuation.resume(Unit)
                         }
                         // Optional: Disconnect after command? Or keep connection? 
                         // For now, let's disconnect to free up the channel if we are simple
                         // But usually we want to keep it.
                         // Let's rely on Android closing it eventually or user action.
                         // Actually, for this simple impl, let's close after a short delay or just leave it.
                         // Ideally we cache the connection in a real app.
                         gatt.disconnect() 
                    } else {
                        onLog("No suitable write characteristic found!")
                        gatt.disconnect()
                        if (continuation.isActive) {
                            continuation.resumeWithException(Exception("No write characteristic"))
                        }
                    }
                } else {
                    onLog("Service discovery failed: $status")
                    gatt.disconnect()
                    if (continuation.isActive) {
                         continuation.resumeWithException(Exception("Service discovery failed"))
                    }
                }
            }
        }

        device.connectGatt(context, false, gattCallback)
    }
}
