package com.example.ui

import com.example.data.model.ConnectedDevice
import com.example.data.model.EmailItem
import com.example.data.model.TaskItem
import com.example.data.model.VoiceLogItem
import com.example.voice.VoiceState
import com.example.voice.WakeWordSensitivity

data class AuraUiState(
    val tasks: List<TaskItem> = emptyList(),
    val emails: List<EmailItem> = emptyList(),
    val voiceLogs: List<VoiceLogItem> = emptyList(),
    val devices: List<ConnectedDevice> = emptyList(),
    val voiceState: VoiceState = VoiceState.Idle,
    val lastSpokenResponse: String = "",
    val lastVisualResponse: String = "",
    val lastActionType: String = "",
    val isDailyDigestOpen: Boolean = false,
    val dailyDigestContent: String = "",
    val isGeneratingDigest: Boolean = false,
    val isCreateTaskDialogOpen: Boolean = false,
    val taskFilter: String = "ALL", // "ALL", "TODAY", "MEETING", "HIGH_PRIORITY", "COMPLETED"
    val emailFilter: String = "ALL", // "ALL", "UNREAD", "URGENT", "STARRED"
    val selectedTab: Int = 0, // 0: Assistant, 1: Tasks, 2: Emails, 3: Ecosystem
    val textInput: String = "",
    val isTtsMuted: Boolean = false,
    val isAssistantActive: Boolean = true,
    val currentLanguage: com.example.voice.AppLanguage = com.example.voice.AppLanguage.ENGLISH,
    val isWakeWordActive: Boolean = true,
    val wakeWordSensitivity: WakeWordSensitivity = WakeWordSensitivity.MEDIUM,
    val isProcessingCommand: Boolean = false,
    val soundWaveLevel: Float = 0f,
    val phoneControlState: com.example.phone.PhoneControlState = com.example.phone.PhoneControlState()
)
