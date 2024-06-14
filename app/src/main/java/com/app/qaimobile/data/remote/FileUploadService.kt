package com.app.qaimobile.data.remote

import com.app.qaimobile.data.remote.UploadFilesResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileUploadService {
    @Multipart
    @POST("/bp_files/q-file-upload")
    fun uploadFiles(
        @Part files: List<MultipartBody.Part>,
        @Part("tabNames") tabNames: List<String>,
        @Part("userId") userId: String,
        @Part("threadId") threadId: String?
    ): Call<UploadFilesResponse>
}
