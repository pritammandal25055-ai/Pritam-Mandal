package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Priority {
    HIGH,
    MEDIUM,
    LOW
}

enum class TaskCategory {
    MEETING,
    WORK,
    EMAIL_FOLLOWUP,
    PERSONAL,
    URGENT
}

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: String, // e.g. "Tuesday, Oct 14"
    val dueTime: String = "", // e.g. "10:00 AM"
    val priority: Priority = Priority.MEDIUM,
    val category: TaskCategory = TaskCategory.WORK,
    val isCompleted: Boolean = false,
    val source: String = "Voice Command", // "Voice Command (Android)", "Email Digest", etc.
    val originDevice: String = "Android Device",
    val rawCommand: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
