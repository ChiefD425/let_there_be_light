package com.antigravity.lights.data.network

import com.antigravity.lights.ui.theme.AccentColor // Just using Color class
import androidx.compose.ui.graphics.Color

interface LightProtocol {
    fun turnOn(): ByteArray
    fun turnOff(): ByteArray
    fun setColor(color: Color): ByteArray
    fun setBrightness(brightness: Float): ByteArray // 0.0 - 1.0
    fun setPattern(patternId: Int, speed: Int): ByteArray
}
