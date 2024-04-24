package com.app.qaimobile.data.repository

import com.app.qaimobile.data.model.User
import com.app.qaimobile.data.remote.ApiService
import com.app.qaimobile.domain.repository.UserRepository
import javax.inject.Inject
import com.app.qaimobile.domain.Result

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {

    override suspend fun login(user: User): Result<User> {
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
