package com.app.qaimobile.data.repository

import com.app.qaimobile.data.model.network.auth.LoginRequest
import com.app.qaimobile.data.model.network.auth.LoginResponse
import com.app.qaimobile.data.remote.UserDataSource
import com.app.qaimobile.domain.Result
import com.app.qaimobile.domain.datastore.AppDataStore
import com.app.qaimobile.domain.repository.UserRepository
import retrofit2.Response
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dataSource: UserDataSource,
    private val appDataStore: AppDataStore // Inject the AppDataStore
) : UserRepository {

    override suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return try {
            val response: Response<LoginResponse> = dataSource.login(loginRequest)
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    // Save the token in a coroutine scope
                    appDataStore.saveAccessToken(loginResponse.accessToken)
                    return Result.Success(loginResponse)
                } ?: Result.Failure(Exception("Empty response body"))
            } else {
                Result.Failure(Exception("Login failed: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}
