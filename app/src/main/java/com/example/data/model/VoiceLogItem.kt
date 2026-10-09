package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice_logs")
data class VoiceLogItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val commandText: String,
    val originDevice: String = "Android Phone", // "Android Phone", "MacBook Pro", "iPhone 16", "Windows PC"
    val platformType: String = "Android", // "Android", "iOS", "Desktop", "Web"
    val intentDetected: String = "SCHEDULE_TASK", // "SCHEDULE_TASK", "SUMMARIZE_EMAILS", "SCHEDULE_QUERY", "SYSTEM"
    val aiResponseText: String,
    val isWakeWordTriggered: Boolean = false,
    val isSuccess: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

data class ConnectedDevice(
    val id: String,
    val name: String,
    val platform: String, // "Android", "iOS", "macOS", "Windows"
    val iconName: String,
    val isCurrentDevice: Boolean = false,
    val isOnline: Boolean = true,
    val wakeWordActive: Boolean = true,
    val lastSyncText: String = "Just now"
)
