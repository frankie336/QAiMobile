package com.app.qaimobile.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class LocalDatabaseHelper(context: Context) : SQLiteOpenHelper(context, LocalDatabaseContract.DATABASE_NAME, null, LocalDatabaseContract.DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create the conversation_sessions table
        val createConversationSessionsTable = """
            CREATE TABLE ${LocalDatabaseContract.ConversationSessionEntry.TABLE_NAME} (
                ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${LocalDatabaseContract.ConversationSessionEntry.COLUMN_ID} TEXT NOT NULL,
                ${LocalDatabaseContract.ConversationSessionEntry.COLUMN_THREAD_ID} TEXT,
                ${LocalDatabaseContract.ConversationSessionEntry.COLUMN_CREATED_AT} INTEGER NOT NULL,
                ${LocalDatabaseContract.ConversationSessionEntry.COLUMN_USER_ID} TEXT NOT NULL,
                ${LocalDatabaseContract.ConversationSessionEntry.COLUMN_THREAD} TEXT,
                ${LocalDatabaseContract.ConversationSessionEntry.COLUMN_SUMMARY} TEXT,
                ${LocalDatabaseContract.ConversationSessionEntry.COLUMN_MESSAGES} TEXT NOT NULL DEFAULT '[]',
                ${LocalDatabaseContract.ConversationSessionEntry.COLUMN_APP_DESIGNATION} TEXT,
                FOREIGN KEY (${LocalDatabaseContract.ConversationSessionEntry.COLUMN_USER_ID}) REFERENCES ${LocalDatabaseContract.UserEntry.TABLE_NAME}(${LocalDatabaseContract.UserEntry.COLUMN_ID})
            )
        """
        db.execSQL(createConversationSessionsTable)

        // Create other tables like users, media, etc.
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }
}
