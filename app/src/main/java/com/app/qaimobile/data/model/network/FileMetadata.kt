package com.app.qaimobile.data.model.network

import com.google.gson.annotations.SerializedName

data class FileMetadata(
    @SerializedName("id")
    val fileId: String,
    @SerializedName("filename")
    val fileName: String,
    @SerializedName("bytes")
    val size: Int,
    @SerializedName("created_at")
    val uploadDate: Long,
    @SerializedName("object")
    val objectType: String,
    @SerializedName("purpose")
    val purpose: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("status_details")
    val statusDetails: String?
)
