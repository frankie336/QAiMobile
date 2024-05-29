// ConversationSessionDao.kt
package com.app.qaimobile.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(conversationSession: ConversationSession)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(conversationSessions: List<ConversationSession>)

    @Query("SELECT * FROM ${LocalDatabaseContract.ConversationSessionEntry.TABLE_NAME}")
    fun getAllConversationSessions(): Flow<List<ConversationSession>>

    @Query("SELECT * FROM ${LocalDatabaseContract.ConversationSessionEntry.TABLE_NAME} WHERE ${LocalDatabaseContract.ConversationSessionEntry.COLUMN_ID} = :sessionId")
    fun getConversationSessionById(sessionId: String): Flow<ConversationSession>

    @Update
    suspend fun update(conversationSession: ConversationSession)

    @Delete
    suspend fun delete(conversationSession: ConversationSession)
}
