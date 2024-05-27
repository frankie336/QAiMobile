package com.app.qaimobile.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.qaimobile.util.generateUuid

@Entity(tableName = LocalDatabaseContract.ConversationSessionEntry.TABLE_NAME)
data class ConversationSession(
    @PrimaryKey
    @ColumnInfo(name = LocalDatabaseContract.ConversationSessionEntry.COLUMN_ID)
    val id: String = generateUuid(),

    @ColumnInfo(name = LocalDatabaseContract.ConversationSessionEntry.COLUMN_THREAD_ID)
    val threadId: String? = null,

    @ColumnInfo(name = LocalDatabaseContract.ConversationSessionEntry.COLUMN_CREATED_AT)
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = LocalDatabaseContract.ConversationSessionEntry.COLUMN_USER_ID)
    val userId: String,

    @ColumnInfo(name = LocalDatabaseContract.ConversationSessionEntry.COLUMN_THREAD)
    val thread: String? = null,

    @ColumnInfo(name = LocalDatabaseContract.ConversationSessionEntry.COLUMN_SUMMARY)
    val summary: String? = null,

    @ColumnInfo(name = LocalDatabaseContract.ConversationSessionEntry.COLUMN_MESSAGES)
    val messages: String = "[]",

    @ColumnInfo(name = LocalDatabaseContract.ConversationSessionEntry.COLUMN_APP_DESIGNATION)
    val appDesignation: String? = null
)
