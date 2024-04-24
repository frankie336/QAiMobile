package com.app.qaimobile.data.remote

import com.app.qaimobile.data.model.User
import javax.inject.Inject
import com.app.qaimobile.domain.Result

/**
 * Data source for user related operations.
 */
class UserDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun login(user: User): Result<User> {
        return try {
            val response = apiService.login(user)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}