package com.app.qaimobile.data.remote

data class DeleteFileRequest(
    val userId: String,
    val threadId: String?,
    val tabName: String,
    val filename: String
)
