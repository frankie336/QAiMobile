package com.app.qaimobile.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.qaimobile.domain.datastore.AppDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppDataStoreImpl @Inject constructor(private val context: Context) : AppDataStore {

    private val Context.dataStore by preferencesDataStore(name = DataStoreConstants.DATASTORE_NAME)

    override suspend fun saveUserCredentials(email: String, password: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(DataStoreConstants.EMAIL)] = email
            preferences[stringPreferencesKey(DataStoreConstants.PASSWORD)] = password
        }
    }

    override val userCredentials: Flow<Pair<String?, String?>> =
        context.dataStore.data.map { preferences ->
            Pair(
                preferences[stringPreferencesKey(DataStoreConstants.EMAIL)],
                preferences[stringPreferencesKey(DataStoreConstants.PASSWORD)]
            )
        }

    override suspend fun saveIsLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(DataStoreConstants.IS_LOGGED_IN)] = isLoggedIn
        }
    }

    override val isLoggedIn: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(DataStoreConstants.IS_LOGGED_IN)] ?: false
        }

    override suspend fun saveAccessToken(accessToken: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(DataStoreConstants.ACCESS_TOKEN)] = accessToken
        }
    }

    override val accessToken: Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(DataStoreConstants.ACCESS_TOKEN)] ?: ""
        }
}