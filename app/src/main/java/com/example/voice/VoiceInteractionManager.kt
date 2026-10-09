package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

sealed class VoiceState {
    object Idle : VoiceState()
    object ListeningWakeWord : VoiceState()
    data class ListeningCommand(val partialText: String = "", val soundLevel: Float = 0f) : VoiceState()
    data class Processing(val recognizedText: String) : VoiceState()
    data class Speaking(val text: String) : VoiceState()
    data class Error(val message: String) : VoiceState()
}

class VoiceInteractionManager(
    private val context: Context,
    private val onCommandReceived: (String, Boolean) -> Unit
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false

    private val _voiceState = MutableStateFlow<VoiceState>(VoiceState.Idle)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _isTtsMuted = MutableStateFlow(false)
    val isTtsMuted: StateFlow<Boolean> = _isTtsMuted.asStateFlow()

    private val _soundWaveLevel = MutableStateFlow(0f)
    val soundWaveLevel: StateFlow<Float> = _soundWaveLevel.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    val wakeWordDetector = WakeWordDetector()

    init {
        initTts()
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                applyVoiceConfigForLanguage(_currentLanguage.value)
                isTtsInitialized = true
            }
        }
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        wakeWordDetector.setLanguage(language)
        if (isTtsInitialized) {
            applyVoiceConfigForLanguage(language)
        }
    }

    private fun applyVoiceConfigForLanguage(language: AppLanguage) {
        try {
            textToSpeech?.language = language.locale
            // Distinct warm, feminine pitch
            textToSpeech?.setPitch(1.22f)
            textToSpeech?.setSpeechRate(1.02f)

            val availableVoices = textToSpeech?.voices
            if (availableVoices != null) {
                val femaleVoice = availableVoices.firstOrNull { voice ->
                    val name = voice.name.lowercase(Locale.ROOT)
                    val isFemale = name.contains("female") ||
                            name.contains("f0") ||
                            name.contains("eva") ||
                            name.contains("samantha") ||
                            name.contains("zira") ||
                            name.contains("kore") ||
                            name.contains("sfg") ||
                            name.contains("female_1") ||
                            voice.features.any { it.contains("gender=female", ignoreCase = true) }
                    isFemale && (voice.locale.language == language.locale.language)
                }
                if (femaleVoice != null) {
                    textToSpeech?.voice = femaleVoice
                }
            }
        } catch (e: Exception) {
            // Keep default locale
        }
    }

    fun toggleMuteTts() {
        _isTtsMuted.value = !_isTtsMuted.value
        if (_isTtsMuted.value) {
            stopSpeaking()
        }
    }

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            _voiceState.value = VoiceState.Error("Speech recognition not available on this device.")
            return
        }

        stopSpeaking()
        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    val prompt = when (_currentLanguage.value) {
                        AppLanguage.ENGLISH -> "Listening for your command..."
                        AppLanguage.HINDI -> "पारुल सुन रही है..."
                        AppLanguage.BENGALI -> "পারুল শুনছে..."
                    }
                    _voiceState.value = VoiceState.ListeningCommand(partialText = prompt, soundLevel = 0f)
                }

                override fun onBeginningOfSpeech() {
                    _voiceState.value = VoiceState.ListeningCommand(partialText = "Hearing voice...", soundLevel = 0.5f)
                }

                override fun onRmsChanged(rmsdB: Float) {
                    val normalized = (rmsdB.coerceIn(0f, 10f) / 10f)
                    _soundWaveLevel.value = normalized
                    val current = _voiceState.value
                    if (current is VoiceState.ListeningCommand) {
                        _voiceState.value = current.copy(soundLevel = normalized)
                    }
                }

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    val current = _voiceState.value
                    val lastText = if (current is VoiceState.ListeningCommand) current.partialText else ""
                    _voiceState.value = VoiceState.Processing(lastText)
                }

                override fun onError(error: Int) {
                    _voiceState.value = VoiceState.Idle
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val spoken = matches?.firstOrNull() ?: ""
                    if (spoken.isNotBlank()) {
                        val isWake = wakeWordDetector.checkForWakeWord(spoken)
                        _voiceState.value = VoiceState.Processing(spoken)
                        onCommandReceived(spoken, isWake)
                    } else {
                        _voiceState.value = VoiceState.Idle
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val partial = matches?.firstOrNull() ?: ""
                    if (partial.isNotBlank()) {
                        _voiceState.value = VoiceState.ListeningCommand(partialText = partial, soundLevel = _soundWaveLevel.value)
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        val langCode = _currentLanguage.value.code
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, langCode)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langCode)
            putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("en-US", "hi-IN", "bn-IN"))
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

        try {
            speechRecognizer?.startListening(intent)
            _voiceState.value = VoiceState.ListeningCommand(partialText = "Parul is listening...", soundLevel = 0f)
        } catch (e: Exception) {
            _voiceState.value = VoiceState.Error("Could not start listening: ${e.message}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            // Ignore
        }
        if (_voiceState.value is VoiceState.ListeningCommand) {
            _voiceState.value = VoiceState.Idle
        }
    }

    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        if (_isTtsMuted.value) {
            _voiceState.value = VoiceState.Idle
            onComplete?.invoke()
            return
        }

        if (isTtsInitialized && text.isNotBlank()) {
            _voiceState.value = VoiceState.Speaking(text)
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "PARUL_VOICE_UTTERANCE")
        }
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
        if (_voiceState.value is VoiceState.Speaking) {
            _voiceState.value = VoiceState.Idle
        }
    }

    fun cleanup() {
        try {
            speechRecognizer?.destroy()
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        } catch (e: Exception) {
            // Ignore
        }
    }
}
