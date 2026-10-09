package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emails")
data class EmailItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderName: String,
    val senderEmail: String,
    val subject: String,
    val preview: String,
    val body: String,
    val receivedTime: String, // e.g. "9:15 AM", "Yesterday"
    val isRead: Boolean = false,
    val isStarred: Boolean = false,
    val category: String = "Work", // "Urgent", "Client", "Team", "Billing"
    val urgencyScore: Int = 5, // 1 to 10
    val aiSummary: String = "",
    val suggestedAction: String = "",
    val suggestedTaskTitle: String = "",
    val suggestedTaskTime: String = "",
    val generatedQuickReply: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
