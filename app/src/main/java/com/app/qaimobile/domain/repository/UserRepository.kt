package com.app.qaimobile.domain.repository

import com.app.qaimobile.data.model.network.auth.LoginRequest
import com.app.qaimobile.data.model.network.auth.LoginResponse
import com.app.qaimobile.domain.Result

/**
 * Repository for user related operations.
 */
interface UserRepository {
    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse>
}