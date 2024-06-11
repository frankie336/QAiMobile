package com.app.qaimobile.data.model.network

import com.google.gson.annotations.SerializedName

data class LocationUpdateRequest(
    @SerializedName("permissionStatus")
    val permissionStatus: String,
    @SerializedName("locationType")
    val locationType: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("altitude")
    val altitude: Double
)
