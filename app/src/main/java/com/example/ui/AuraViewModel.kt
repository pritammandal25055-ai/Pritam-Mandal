package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiAiManager
import com.example.data.model.EmailItem
import com.example.data.model.Priority
import com.example.data.model.TaskCategory
import com.example.data.model.TaskItem
import com.example.data.model.VoiceLogItem
import com.example.data.repository.AuraRepository
import com.example.voice.CrossPlatformVoiceEngine
import com.example.voice.VoiceInteractionManager
import com.example.voice.VoiceState
import com.example.voice.WakeWordSensitivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuraViewModel(
    private val repository: AuraRepository,
    val voiceManager: VoiceInteractionManager,
    val phoneControlManager: com.example.phone.PhoneControlManager,
    private val aiManager: GeminiAiManager = GeminiAiManager(),
    private val crossPlatformEngine: CrossPlatformVoiceEngine = CrossPlatformVoiceEngine()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuraUiState())
    val uiState: StateFlow<AuraUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeIfEmpty()
        }

        // Combine repository flows, voice states, and phone control
        viewModelScope.launch {
            combine(
                repository.allTasks,
                repository.allEmails,
                repository.voiceLogs,
                crossPlatformEngine.devices,
                voiceManager.voiceState
            ) { tasks, emails, logs, devices, voiceState ->
                _uiState.value.copy(
                    tasks = tasks,
                    emails = emails,
                    voiceLogs = logs,
                    devices = devices,
                    voiceState = voiceState,
                    isTtsMuted = voiceManager.isTtsMuted.value,
                    isWakeWordActive = voiceManager.wakeWordDetector.isWakeWordListening.value,
                    wakeWordSensitivity = voiceManager.wakeWordDetector.sensitivity.value,
                    phoneControlState = phoneControlManager.controlState.value
                )
            }.collect { combinedState ->
                _uiState.value = combinedState
            }
        }

        // Collect phone control state updates
        viewModelScope.launch {
            phoneControlManager.controlState.collect { phoneState ->
                _uiState.value = _uiState.value.copy(phoneControlState = phoneState)
            }
        }

        // Collect microphone audio wave levels for visual waveform animation
        viewModelScope.launch {
            voiceManager.soundWaveLevel.collect { waveLevel ->
                _uiState.value = _uiState.value.copy(soundWaveLevel = waveLevel)
            }
        }
    }

    fun onMicrophoneClick() {
        val currentVoiceState = _uiState.value.voiceState
        if (currentVoiceState is VoiceState.ListeningCommand) {
            voiceManager.stopListening()
        } else if (currentVoiceState is VoiceState.Speaking) {
            voiceManager.stopSpeaking()
        } else {
            voiceManager.startListening()
        }
    }

    fun executeCommand(
        commandText: String,
        isWakeWord: Boolean = false,
        originDevice: String = "Android Device",
        platform: String = "Android"
    ) {
        if (commandText.isBlank()) return

        if (!_uiState.value.isAssistantActive && !isWakeWord && !commandText.lowercase().contains("parul") && !commandText.lowercase().contains("turn on")) {
            val standbyMsg = when (_uiState.value.currentLanguage) {
                com.example.voice.AppLanguage.HINDI -> "पारुल अभी स्टैंडबाय पर है। शुरू करने के लिए 'हे पारुल' बोलिए।"
                com.example.voice.AppLanguage.BENGALI -> "পারুল এখন স্ট্যান্ডবাই-তে আছে। চালু করতে 'হে পারুল' বলুন।"
                else -> "Parul is currently turned off. Say 'Hey Parul' to turn on."
            }
            _uiState.value = _uiState.value.copy(
                isProcessingCommand = false,
                lastSpokenResponse = standbyMsg,
                lastVisualResponse = "Parul is in Standby mode. Say 'Hey Parul' to turn on.",
                textInput = ""
            )
            voiceManager.speak(standbyMsg)
            return
        }

        _uiState.value = _uiState.value.copy(isProcessingCommand = true)

        viewModelScope.launch {
            val currentTasks = _uiState.value.tasks
            val currentEmails = _uiState.value.emails

            val result = aiManager.processVoiceOrTextCommand(
                rawCommand = commandText,
                originDevice = originDevice,
                existingTasks = currentTasks,
                existingEmails = currentEmails,
                language = _uiState.value.currentLanguage
            )

            // If a task was extracted, insert into Room database
            var createdTaskId: Long? = null
            result.createdTask?.let { task ->
                createdTaskId = repository.insertTask(task)
            }

            // Log voice command in database
            repository.logVoiceInteraction(
                VoiceLogItem(
                    commandText = commandText,
                    originDevice = originDevice,
                    platformType = platform,
                    intentDetected = result.actionType,
                    aiResponseText = result.spokenResponse,
                    isWakeWordTriggered = isWakeWord,
                    isSuccess = true
                )
            )

            // If emails were summarized, generate or show digest
            if (result.actionType == "EMAILS_SUMMARIZED") {
                val digest = aiManager.generateDailyBriefing(currentEmails)
                _uiState.value = _uiState.value.copy(
                    dailyDigestContent = digest,
                    isDailyDigestOpen = true
                )
            }

            // If phone action requested, route to PhoneControlManager
            if (result.actionType.startsWith("PHONE_ACTION_")) {
                val actionName = result.actionType.removePrefix("PHONE_ACTION_")
                val action = try {
                    com.example.phone.PhoneActionType.valueOf(actionName)
                } catch (e: Exception) {
                    com.example.phone.PhoneActionType.NONE
                }
                if (action != com.example.phone.PhoneActionType.NONE) {
                    phoneControlManager.requestActionExecution(action, result.visualResponse) {}
                }
            }

            val newAssistantActive = when {
                result.actionType == "ASSISTANT_TURNED_OFF" -> false
                result.actionType == "ASSISTANT_TURNED_ON" -> true
                isWakeWord || commandText.lowercase().contains("parul") -> true
                else -> _uiState.value.isAssistantActive
            }

            _uiState.value = _uiState.value.copy(
                isProcessingCommand = false,
                isAssistantActive = newAssistantActive,
                lastSpokenResponse = result.spokenResponse,
                lastVisualResponse = result.visualResponse,
                lastActionType = result.actionType,
                textInput = ""
            )

            // Speak response via Android TextToSpeech
            voiceManager.speak(result.spokenResponse) {
                if (result.actionType == "ASSISTANT_TURNED_OFF") {
                    voiceManager.stopListening()
                }
            }
        }
    }

    fun generateDailyEmailDigest() {
        _uiState.value = _uiState.value.copy(isGeneratingDigest = true)
        viewModelScope.launch {
            val digest = aiManager.generateDailyBriefing(_uiState.value.emails)
            _uiState.value = _uiState.value.copy(
                isGeneratingDigest = false,
                dailyDigestContent = digest,
                isDailyDigestOpen = true
            )
            voiceManager.speak("Generated your executive email briefing. You have ${_uiState.value.emails.size} messages with immediate action items highlighted.")
        }
    }

    fun dismissDailyDigest() {
        _uiState.value = _uiState.value.copy(isDailyDigestOpen = false)
        voiceManager.stopSpeaking()
    }

    fun addTaskFromEmail(email: EmailItem) {
        viewModelScope.launch {
            val title = email.suggestedTaskTitle.ifBlank { "Follow up on: ${email.subject}" }
            val time = email.suggestedTaskTime.ifBlank { "Today, 5:00 PM" }
            val task = TaskItem(
                title = title,
                description = email.suggestedAction.ifBlank { email.preview },
                dueDate = if (time.contains(",")) time.substringBefore(",").trim() else "Today",
                dueTime = if (time.contains(",")) time.substringAfter(",").trim() else "5:00 PM",
                priority = if (email.urgencyScore >= 8) Priority.HIGH else Priority.MEDIUM,
                category = TaskCategory.EMAIL_FOLLOWUP,
                isCompleted = false,
                source = "Email: ${email.senderName}",
                originDevice = "Inbox Intelligence",
                rawCommand = "Auto-extracted from email: ${email.subject}"
            )
            repository.insertTask(task)
            repository.markEmailRead(email.id, true)
            voiceManager.speak("Added '$title' to your schedule.")
        }
    }

    fun toggleTaskCompleted(task: TaskItem) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun createManualTask(title: String, date: String, time: String, priority: Priority, category: TaskCategory, description: String) {
        viewModelScope.launch {
            val newTask = TaskItem(
                title = title,
                description = description,
                dueDate = date.ifBlank { "Today" },
                dueTime = time.ifBlank { "10:00 AM" },
                priority = priority,
                category = category,
                isCompleted = false,
                source = "Manual Creation",
                originDevice = "Android Phone"
            )
            repository.insertTask(newTask)
            _uiState.value = _uiState.value.copy(isCreateTaskDialogOpen = false)
            voiceManager.speak("Created task: $title.")
        }
    }

    fun toggleEmailStar(email: EmailItem) {
        viewModelScope.launch {
            repository.toggleEmailStar(email.id)
        }
    }

    fun toggleEmailRead(email: EmailItem) {
        viewModelScope.launch {
            repository.markEmailRead(email.id, !email.isRead)
        }
    }

    fun simulateCrossDeviceCommand(deviceName: String, platform: String, commandText: String) {
        executeCommand(
            commandText = commandText,
            isWakeWord = true,
            originDevice = deviceName,
            platform = platform
        )
    }

    fun setSelectedTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }

    fun setTaskFilter(filter: String) {
        _uiState.value = _uiState.value.copy(taskFilter = filter)
    }

    fun setEmailFilter(filter: String) {
        _uiState.value = _uiState.value.copy(emailFilter = filter)
    }

    fun setTextInput(text: String) {
        _uiState.value = _uiState.value.copy(textInput = text)
    }

    fun setCreateTaskDialogOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isCreateTaskDialogOpen = open)
    }

    fun toggleMuteTts() {
        voiceManager.toggleMuteTts()
        _uiState.value = _uiState.value.copy(isTtsMuted = voiceManager.isTtsMuted.value)
    }

    fun setLanguage(language: com.example.voice.AppLanguage) {
        voiceManager.setLanguage(language)
        _uiState.value = _uiState.value.copy(currentLanguage = language)
        val feedback = when (language) {
            com.example.voice.AppLanguage.HINDI -> "नमस्ते, मैं पारुल हूँ। भाषा हिन्दी में सेट हो गई है।"
            com.example.voice.AppLanguage.BENGALI -> "নমস্কার, আমি পারুল। ভাষা বাংলায় সেট করা হয়েছে।"
            else -> "Language set to English."
        }
        voiceManager.speak(feedback)
    }

    fun toggleWakeWordListening() {
        val current = _uiState.value.isWakeWordActive
        voiceManager.wakeWordDetector.setWakeWordListening(!current)
        _uiState.value = _uiState.value.copy(isWakeWordActive = !current)
    }

    fun setWakeWordSensitivity(sensitivity: WakeWordSensitivity) {
        voiceManager.wakeWordDetector.setSensitivity(sensitivity)
        _uiState.value = _uiState.value.copy(wakeWordSensitivity = sensitivity)
    }

    fun toggleDeviceWakeWord(deviceId: String) {
        crossPlatformEngine.toggleDeviceWakeWord(deviceId)
    }

    fun setPhoneControlPermission(granted: Boolean) {
        phoneControlManager.setPermissionGranted(granted)
        val msg = if (granted) {
            when (_uiState.value.currentLanguage) {
                com.example.voice.AppLanguage.HINDI -> "फ़ोन नियंत्रण की अनुमति दी गई।"
                com.example.voice.AppLanguage.BENGALI -> "ফোন নিয়ন্ত্রণের অনুমতি দেওয়া হয়েছে।"
                else -> "Phone control permission granted. Parul can now perform system actions on your command."
            }
        } else {
            "Phone control disabled."
        }
        voiceManager.speak(msg)
    }

    fun confirmPendingPhoneAction() {
        phoneControlManager.confirmPendingAction()
    }

    fun dismissPhoneActionDialog() {
        phoneControlManager.dismissConsentDialog()
    }

    fun executePhoneActionDirect(action: com.example.phone.PhoneActionType) {
        phoneControlManager.executeAction(action)
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.cleanup()
    }
}
