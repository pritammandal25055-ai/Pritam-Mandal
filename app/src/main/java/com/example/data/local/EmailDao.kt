package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.EmailItem
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDao {
    @Query("SELECT * FROM emails ORDER BY timestamp DESC")
    fun getAllEmails(): Flow<List<EmailItem>>

    @Query("SELECT * FROM emails WHERE isRead = 0 ORDER BY timestamp DESC")
    fun getUnreadEmails(): Flow<List<EmailItem>>

    @Query("SELECT * FROM emails WHERE urgencyScore >= 7 ORDER BY urgencyScore DESC")
    fun getUrgentEmails(): Flow<List<EmailItem>>

    @Query("SELECT * FROM emails WHERE id = :id")
    suspend fun getEmailById(id: Long): EmailItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmail(email: EmailItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmails(emails: List<EmailItem>)

    @Update
    suspend fun updateEmail(email: EmailItem)

    @Query("UPDATE emails SET isRead = :isRead WHERE id = :id")
    suspend fun markRead(id: Long, isRead: Boolean)

    @Query("UPDATE emails SET isStarred = NOT isStarred WHERE id = :id")
    suspend fun toggleStar(id: Long)

    @Query("UPDATE emails SET aiSummary = :summary, suggestedAction = :action, generatedQuickReply = :reply WHERE id = :id")
    suspend fun updateAiInsights(id: Long, summary: String, action: String, reply: String)

    @Query("SELECT COUNT(*) FROM emails")
    suspend fun getCount(): Int
}
