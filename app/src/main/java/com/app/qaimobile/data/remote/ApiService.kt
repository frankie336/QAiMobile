package com.app.qaimobile.data.remote

import com.app.qaimobile.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * ApiService is an interface that defines the endpoints of the API.
 */
interface ApiService {

    /**
     * This function is used to login the user.
     * @param user The user object containing the user credentials.
     * @return The response containing the user object.
     */
    @POST("login")
    suspend fun login(@Body user: User): Response<User>
}