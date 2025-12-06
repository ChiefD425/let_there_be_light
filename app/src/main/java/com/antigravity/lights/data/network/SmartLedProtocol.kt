package com.antigravity.lights.data.network

import androidx.compose.ui.graphics.Color
import javax.inject.Inject

class SmartLedProtocol @Inject constructor() : LightProtocol {
    // ELK-BLEDOM Protocol (Lotus Lantern)
    // ON: 7E 00 04 F0 00 01 FF 00 EF
    // OFF: 7E 00 04 00 00 00 FF 00 EF
    // COLOR: 7E 00 05 03 R G B 00 EF

    override fun turnOn(): ByteArray {
        return byteArrayOf(0x7E, 0x00, 0x04, 0xF0.toByte(), 0x00, 0x01, 0xFF.toByte(), 0x00, 0xEF.toByte())
    }

    override fun turnOff(): ByteArray {
        return byteArrayOf(0x7E, 0x00, 0x04, 0x00, 0x00, 0x00, 0xFF.toByte(), 0x00, 0xEF.toByte())
    }

    override fun setColor(color: Color): ByteArray {
        val r = (color.red * 255).toInt().toByte()
        val g = (color.green * 255).toInt().toByte()
        val b = (color.blue * 255).toInt().toByte()
        return byteArrayOf(0x7E, 0x00, 0x05, 0x03, r, g, b, 0x00, 0xEF.toByte())
    }

    override fun setBrightness(brightness: Float): ByteArray {
        // 7E 00 01 [brightness 0-100] 00 00 00 00 EF
        val bb = (brightness * 100).toInt().toByte()
        return byteArrayOf(0x7E, 0x00, 0x01, bb, 0x00, 0x00, 0x00, 0x00, 0xEF.toByte()) 
    }
    
    override fun setPattern(patternId: Int): ByteArray {
         // 7E 00 03 [patternId] 03 00 00 00 EF (Guessing based on structure)
         // Need to verify exact pattern structure, but this is a safer start
         return byteArrayOf(0x7E, 0x00, 0x03, patternId.toByte(), 0x03, 0x00, 0x00, 0x00, 0xEF.toByte())
    }
}
