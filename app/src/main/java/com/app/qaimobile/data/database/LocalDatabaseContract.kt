package com.app.qaimobile.data.local

object LocalDatabaseContract {
    const val DATABASE_NAME = "qai_mobile_db"
    const val DATABASE_VERSION = 1

    object ConversationSessionEntry {
        const val TABLE_NAME = "conversation_sessions"
        const val COLUMN_ID = "id"
        const val COLUMN_THREAD_ID = "thread_id"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_THREAD = "thread"
        const val COLUMN_SUMMARY = "summary"
        const val COLUMN_MESSAGES = "messages"
        const val COLUMN_APP_DESIGNATION = "app_designation"
    }

    object UserEntry {
        const val TABLE_NAME = "users"
        const val COLUMN_ID = "id"
    }
}
