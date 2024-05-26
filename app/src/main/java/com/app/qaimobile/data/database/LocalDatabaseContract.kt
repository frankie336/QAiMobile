package com.app.qaimobile.data.local
import android.provider.BaseColumns

object LocalDatabaseContract {
    const val DATABASE_NAME = "local_database.db"
    const val DATABASE_VERSION = 1

    object ConversationSessionEntry : BaseColumns {
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

    object UserEntry : BaseColumns {
        const val TABLE_NAME = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD_HASH = "password_hash"
        const val COLUMN_FIRST_NAME = "first_name"
        const val COLUMN_LAST_NAME = "last_name"
        const val COLUMN_ACCOUNT_CREATED_AT = "account_created_at"
        const val COLUMN_LAST_LOGIN = "last_login"
        const val COLUMN_IS_ACTIVE = "is_active"
        const val COLUMN_CONFIRMED = "confirmed"
        const val COLUMN_IS_ADMIN = "is_admin"
        const val COLUMN_MOBILE_NUMBER = "mobile_number"
        const val COLUMN_REGISTRATION_SOURCE = "registration_source"
        const val COLUMN_IS_CONFIRMED = "is_confirmed"
        const val COLUMN_EMAIL_VERIFICATION_PIN = "email_verification_pin"
        const val COLUMN_EMAIL_PIN_EXPIRES_AT = "email_pin_expires_at"
        const val COLUMN_INVITATION_CODE = "invitation_code"
        const val COLUMN_LAST_UPDATED = "last_updated"
        const val COLUMN_ROLE = "role"
        const val COLUMN_INTERNAL_ROLE = "internal_role"
    }

    // Add more table entries for other models like Media, etc.
}