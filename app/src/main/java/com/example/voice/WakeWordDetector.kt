package com.example.voice

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class WakeWordSensitivity {
    LOW,
    MEDIUM,
    HIGH
}

class WakeWordDetector {

    private val _isWakeWordListening = MutableStateFlow(true)
    val isWakeWordListening: StateFlow<Boolean> = _isWakeWordListening.asStateFlow()

    private val _sensitivity = MutableStateFlow(WakeWordSensitivity.MEDIUM)
    val sensitivity: StateFlow<WakeWordSensitivity> = _sensitivity.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _lastWakeTimestamp = MutableStateFlow(0L)
    val lastWakeTimestamp: StateFlow<Long> = _lastWakeTimestamp.asStateFlow()

    fun setSensitivity(sensitivity: WakeWordSensitivity) {
        _sensitivity.value = sensitivity
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun setWakeWordListening(active: Boolean) {
        _isWakeWordListening.value = active
    }

    fun checkForWakeWord(transcript: String): Boolean {
        if (!_isWakeWordListening.value) return false
        val lower = transcript.lowercase(Locale.ROOT)

        // Wake phrases across English, Hindi, and Bengali
        val multilingualPhrases = listOf(
            // English
            "hey parul", "ok parul", "parul", "hey assistant",
            // Hindi
            "हे पारुल", "सुनो पारुल", "पारुल",
            // Bengali
            "হে পারুল", "শুনছো পারুল"
        )

        val detected = multilingualPhrases.any { lower.contains(it.lowercase(Locale.ROOT)) }
        if (detected) {
            _lastWakeTimestamp.value = System.currentTimeMillis()
        }
        return detected
    }
}
