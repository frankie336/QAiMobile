// java/com/qai/qaimobile/data/remote/ApiService.kt
package com.qai.qaimobile.data.remote

import com.qai.qaimobile.data.model.LoginRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}