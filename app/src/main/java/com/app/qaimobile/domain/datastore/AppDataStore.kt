package com.app.qaimobile.domain.datastore

import kotlinx.coroutines.flow.Flow

interface AppDataStore {
    suspend fun saveUserCredentials(email: String, password: String)
    val userCredentials: Flow<Pair<String?, String?>>
    suspend fun saveIsLoggedIn(isLoggedIn: Boolean)
    suspend fun isLoggedIn(): Boolean
}