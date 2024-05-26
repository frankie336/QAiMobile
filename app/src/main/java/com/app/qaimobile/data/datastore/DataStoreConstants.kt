package com.app.qaimobile.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey


object DataStoreConstants {
    const val USER_PREFERENCES = "user_prefs"
    const val EMAIL = "email"
    const val PASSWORD = "password"
    const val IS_LOGGED_IN = "is_logged_in"
    const val ACCESS_TOKEN = "access_token"

    const val DATASTORE_NAME = "app_datastore"

    // User Preferences
    val USER_ID = stringPreferencesKey("user_id")
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_TOKEN = stringPreferencesKey("user_token")
    val IS_LOGGED_IN_KEY = booleanPreferencesKey(IS_LOGGED_IN)

    // Other Preferences
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val LANGUAGE_CODE = stringPreferencesKey("language_code")
    val NOTIFICATION_ENABLED = stringPreferencesKey("notification_enabled")

    // Additional constants as needed
}