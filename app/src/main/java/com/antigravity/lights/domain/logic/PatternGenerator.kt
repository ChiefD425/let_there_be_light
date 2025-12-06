package com.antigravity.lights.domain.logic

import androidx.compose.ui.graphics.Color
import com.antigravity.lights.domain.model.LightPattern
import com.antigravity.lights.domain.model.PatternType
import javax.inject.Inject
import kotlin.math.sin

class PatternGenerator @Inject constructor() {

    fun generateNextFrame(pattern: LightPattern, timeMillis: Long, ledCount: Int): List<Color> {
        return when (pattern.type) {
            PatternType.STATIC -> List(ledCount) { pattern.colors.firstOrNull() ?: Color.Black }
            PatternType.FADE -> generateFade(pattern, timeMillis, ledCount)
            PatternType.WAVE -> generateWave(pattern, timeMillis, ledCount)
            else -> List(ledCount) { Color.Black }
        }
    }

    private fun generateFade(pattern: LightPattern, timeMillis: Long, ledCount: Int): List<Color> {
        if (pattern.colors.isEmpty()) return List(ledCount) { Color.Black }
        
        // Simple Sine wave fade
        val speed = pattern.speed * 0.005f
        val phase = (timeMillis * speed).toFloat()
        val sine = (sin(phase) + 1) / 2 // 0..1
        
        // Interpolate between colors or fade to black
        val color = pattern.colors.first()
        return List(ledCount) { 
            color.copy(alpha = sine) 
        }
    }
    
    private fun generateWave(pattern: LightPattern, timeMillis: Long, ledCount: Int): List<Color> {
         // Moving rainbow or color wave
         return List(ledCount) { index ->
             val hue = ((timeMillis / 10 + index * 10) % 360).toFloat()
             Color.hsv(hue, 1f, 1f)
         }
    }
}
