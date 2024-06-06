package com.app.qaimobile.data.remote

import com.app.qaimobile.data.model.network.auth.LoginRequest
import com.app.qaimobile.data.model.network.auth.LoginResponse
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.IOException
import javax.inject.Inject


/**
 * Data source for user related operations.
 */
class UserDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun login(loginRequest: LoginRequest): Response<LoginResponse> {
        return try {
            apiService.login(loginRequest)
        } catch (e: HttpException) {
            throw e // Re-throw HttpException to handle it in the repository
        } catch (e: IOException) {
            throw e // Re-throw IOException to handle it in the repository
        } catch (e: Exception) {
            throw e // Re-throw any other exceptions to handle it in the repository
        }
    }
}
