package com.antigravity.lights.ui.pattern

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.lights.domain.command.LightCommandExecutor
import com.antigravity.lights.domain.logic.PatternGenerator
import com.antigravity.lights.domain.model.LightPattern
import com.antigravity.lights.domain.model.PatternType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatternCreatorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val patternGenerator: PatternGenerator,
    private val commandExecutor: LightCommandExecutor
) : ViewModel() {

    private val _deviceId: String = checkNotNull(savedStateHandle["deviceId"])
    
    private val _uiState = MutableStateFlow(PatternCreatorUiState())
    val uiState: StateFlow<PatternCreatorUiState> = _uiState.asStateFlow()

    init {
        // Start preview loop
        startPreviewSimulation()
    }

    fun updateColor(color: Color) {
        val currentPattern = _uiState.value.currentPattern
        val newColors = if (currentPattern.colors.isEmpty()) listOf(color) else currentPattern.colors + color
        _uiState.value = _uiState.value.copy(
            currentPattern = currentPattern.copy(colors = newColors)
        )
    }

    fun updateSpeed(speed: Float) {
        _uiState.value = _uiState.value.copy(
            currentPattern = _uiState.value.currentPattern.copy(speed = speed)
        )
    }
    
    fun setPatternType(type: PatternType) {
        _uiState.value = _uiState.value.copy(
            currentPattern = _uiState.value.currentPattern.copy(type = type)
        )
    }

    private fun startPreviewSimulation() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (isActive) {
                val time = System.currentTimeMillis() - startTime
                val colors = patternGenerator.generateNextFrame(
                    _uiState.value.currentPattern, 
                    time, 
                    ledCount = 10 // Preview 10 LEDs
                )
                _uiState.value = _uiState.value.copy(previewColors = colors)
                delay(32) // ~30 FPS
            }
        }
    }
    
    fun applyPattern() {
        // Send to device logic here
    }
}

data class PatternCreatorUiState(
    val currentPattern: LightPattern = LightPattern(
        id = "new", 
        name = "My Pattern", 
        type = PatternType.FADE,
        colors = listOf(Color.Red, Color.Blue),
        speed = 10f
    ),
    val previewColors: List<Color> = emptyList()
)
