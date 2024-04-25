package com.app.qaimobile.data.remote

import com.app.qaimobile.data.model.network.auth.LoginRequest
import com.app.qaimobile.data.model.network.auth.LoginResponse
import javax.inject.Inject
import com.app.qaimobile.domain.Result
import retrofit2.HttpException
import java.io.IOException

/**
 * Data source for user related operations.
 */
class UserDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return try {
            val response = apiService.login(loginRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Failure(HttpException(response))
            }
        } catch (e: HttpException) {
            Result.Failure(e)
        } catch (e: IOException) {
            Result.Failure(e)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}