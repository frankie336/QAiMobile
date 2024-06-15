package com.app.qaimobile.data.remote

import com.app.qaimobile.data.remote.UploadFilesResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface FileUploadService {
    @Multipart
    @POST("/bp_files_mobile/q-file-upload")
    fun uploadFiles(
        @Part files: List<MultipartBody.Part>,
        @Part("tabNames") tabNames: List<String>,
        @Part("userId") userId: String,
        @Part("threadId") threadId: String?
    ): Call<UploadFilesResponse>

    @POST("/bp_files_mobile/q-file-delete")
    suspend fun deleteFile(
        @Body deleteFileRequest: DeleteFileRequest
    ): Response<Unit>
}
