package com.app.qaimobile.domain.repository

import com.app.qaimobile.data.model.User
import com.app.qaimobile.domain.Result

/**
 * Repository for user related operations.
 */
interface UserRepository {
    suspend fun login(user: User): Result<User>
}