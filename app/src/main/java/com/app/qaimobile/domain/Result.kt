package com.app.qaimobile.domain

/**
 * A generic class that holds a value with its loading status.
 * @param <T> The type of the value.
 *     This class is used to represent the result of an operation.
 *     It can be in one of the following states:
 *     - Success: The operation was successful and the result is available.
 *     - Failure: The operation failed with an exception.
 */

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}