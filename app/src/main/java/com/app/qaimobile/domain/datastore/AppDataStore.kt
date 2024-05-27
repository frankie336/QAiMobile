package com.app.qaimobile.domain.datastore

import kotlinx.coroutines.flow.Flow

interface AppDataStore {
    suspend fun saveUserCredentials(email: String, password: String)
    fun getUserCredentials(): Flow<Pair<String?, String?>>
    suspend fun saveIsLoggedIn(isLoggedIn: Boolean)
    fun getIsLoggedIn(): Flow<Boolean>
    suspend fun saveAccessToken(accessToken: String)
    fun getAccessToken(): Flow<String?>

    suspend fun saveUserId(userId: String)
    fun getUserId(): Flow<String?>





}
