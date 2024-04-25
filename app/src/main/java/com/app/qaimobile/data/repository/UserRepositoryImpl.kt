package com.app.qaimobile.data.repository

import com.app.qaimobile.data.model.network.auth.LoginRequest
import com.app.qaimobile.data.model.network.auth.LoginResponse
import com.app.qaimobile.data.remote.UserDataSource
import com.app.qaimobile.domain.Result
import com.app.qaimobile.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dataSource: UserDataSource
) : UserRepository {

    override suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return try {
           return dataSource.login(loginRequest)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}
