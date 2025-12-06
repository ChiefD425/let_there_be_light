package com.antigravity.lights.domain.model

import androidx.compose.ui.graphics.Color

data class LightPattern(
    val id: String,
    val name: String,
    val type: PatternType,
    val speed: Float = 1.0f,
    val colors: List<Color> = emptyList(),
    // For custom frames
    val frames: List<List<Color>> = emptyList()
)

enum class PatternType {
    STATIC,
    FADE,
    STROBE,
    WAVE,
    CUSTOM
}
