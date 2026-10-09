package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.VoiceLogItem
import kotlinx.coroutines.flow.Flow

@Dao
interface VoiceLogDao {
    @Query("SELECT * FROM voice_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentVoiceLogs(): Flow<List<VoiceLogItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: VoiceLogItem): Long

    @Query("DELETE FROM voice_logs")
    suspend fun clearAll()
}
