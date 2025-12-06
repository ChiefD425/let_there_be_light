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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import com.antigravity.lights.data.network.DiscoveryService
import kotlinx.coroutines.flow.combine

@HiltViewModel
class PatternCreatorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val patternGenerator: PatternGenerator,
    private val commandExecutor: LightCommandExecutor,
    private val discoveryService: DiscoveryService
) : ViewModel() {

    private val _deviceId: String = checkNotNull(savedStateHandle["deviceId"])
    
    private val _internalState = MutableStateFlow(PatternCreatorUiState())
    
    val uiState: StateFlow<PatternCreatorUiState> = combine(
        _internalState,
        discoveryService.logs
    ) { state, logs ->
        state.copy(logs = logs)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PatternCreatorUiState()
    )

    init {
        // Start preview loop
        startPreviewSimulation()
    }

    fun updateColor(color: Color) {
        val currentPattern = _internalState.value.currentPattern
        val newColors = if (currentPattern.colors.isEmpty()) listOf(color) else currentPattern.colors + color
        _internalState.value = _internalState.value.copy(
            currentPattern = currentPattern.copy(colors = newColors)
        )
    }

    fun updateSpeed(speed: Float) {
        _internalState.value = _internalState.value.copy(
            currentPattern = _internalState.value.currentPattern.copy(speed = speed)
        )
    }
    
    fun setPatternType(type: PatternType) {
        _internalState.value = _internalState.value.copy(
            currentPattern = _internalState.value.currentPattern.copy(type = type)
        )
    }

    private fun startPreviewSimulation() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (isActive) {
                val time = System.currentTimeMillis() - startTime
                val colors = patternGenerator.generateNextFrame(
                    _internalState.value.currentPattern, 
                    time, 
                    ledCount = 10 // Preview 10 LEDs
                )
                _internalState.value = _internalState.value.copy(previewColors = colors)
                delay(32) // ~30 FPS
            }
        }
    }
    
    fun applyPattern() {
        discoveryService.log("Applying pattern to $_deviceId...")
        viewModelScope.launch {
            // Convert current pattern to byte array (via Protocol, ideally)
            // For now, sending a placeholder test command to verify connectivity
            // In real app, we need Pattern -> ByteArray mapper in Protocol
            val dummyCommand = byteArrayOf(0x7e, 0x05, 0x03, 0x01, 0x02, 0x03, 0xef.toByte()) 
            commandExecutor.setPattern(_deviceId, dummyCommand)
        }
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
    val previewColors: List<Color> = emptyList(),
    val logs: List<String> = emptyList()
)
