package com.example.data.repository

import com.example.data.SampleData
import com.example.data.local.AppDatabase
import com.example.data.model.EmailItem
import com.example.data.model.TaskItem
import com.example.data.model.VoiceLogItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AuraRepository(private val database: AppDatabase) {
    private val taskDao = database.taskDao()
    private val emailDao = database.emailDao()
    private val voiceLogDao = database.voiceLogDao()

    val allTasks: Flow<List<TaskItem>> = taskDao.getAllTasks()
    val allEmails: Flow<List<EmailItem>> = emailDao.getAllEmails()
    val urgentEmails: Flow<List<EmailItem>> = emailDao.getUrgentEmails()
    val voiceLogs: Flow<List<VoiceLogItem>> = voiceLogDao.getRecentVoiceLogs()

    suspend fun initializeIfEmpty() = withContext(Dispatchers.IO) {
        if (emailDao.getCount() == 0) {
            emailDao.insertEmails(SampleData.getInitialEmails())
            taskDao.insertTasks(SampleData.getInitialTasks())
        }
    }

    suspend fun insertTask(task: TaskItem): Long = withContext(Dispatchers.IO) {
        taskDao.insertTask(task)
    }

    suspend fun toggleTaskCompleted(id: Long, completed: Boolean) = withContext(Dispatchers.IO) {
        taskDao.setTaskCompleted(id, completed)
    }

    suspend fun deleteTask(task: TaskItem) = withContext(Dispatchers.IO) {
        taskDao.deleteTask(task)
    }

    suspend fun insertEmail(email: EmailItem): Long = withContext(Dispatchers.IO) {
        emailDao.insertEmail(email)
    }

    suspend fun toggleEmailStar(id: Long) = withContext(Dispatchers.IO) {
        emailDao.toggleStar(id)
    }

    suspend fun markEmailRead(id: Long, isRead: Boolean) = withContext(Dispatchers.IO) {
        emailDao.markRead(id, isRead)
    }

    suspend fun updateEmailAiInsights(id: Long, summary: String, action: String, reply: String) = withContext(Dispatchers.IO) {
        emailDao.updateAiInsights(id, summary, action, reply)
    }

    suspend fun logVoiceInteraction(log: VoiceLogItem): Long = withContext(Dispatchers.IO) {
        voiceLogDao.insertLog(log)
    }
}
