package com.app.qaimobile.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.qaimobile.domain.datastore.AppDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(context: Context) : AppDataStore {
    private val dataStore = context.dataStore

    companion object {
        val EMAIL_KEY = stringPreferencesKey("username")
        val PASSWORD_KEY = stringPreferencesKey("password")
        val IS_LOGGED_IN_KEY = stringPreferencesKey("is_logged_in")
    }

    override suspend fun saveUserCredentials(email: String, password: String) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = email
            preferences[PASSWORD_KEY] = password
        }
    }

    override val userCredentials: Flow<Pair<String?, String?>> = dataStore.data.map { preferences ->
        preferences[EMAIL_KEY] to preferences[PASSWORD_KEY]
    }

    override suspend fun saveIsLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = isLoggedIn.toString()
        }
    }

    override suspend fun isLoggedIn(): Boolean = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_KEY]?.toBoolean() ?: false
    }.first()
}
