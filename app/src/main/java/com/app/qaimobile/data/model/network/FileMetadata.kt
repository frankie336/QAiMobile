package com.app.qaimobile.data.model.network

import com.google.gson.annotations.SerializedName

data class FileMetadata(
    @SerializedName("file_id")
    val fileId: String,
    @SerializedName("file_name")
    val fileName: String,
    @SerializedName("upload_date")
    val uploadDate: String,
    @SerializedName("size")
    val size: Long
)
