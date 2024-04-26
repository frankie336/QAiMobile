package com.app.qaimobile.di

import android.content.Context
import androidx.annotation.StringRes
import com.app.qaimobile.util.isInternetAvailable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceProvider @Inject constructor(@ApplicationContext val context: Context){

    fun issNetworkAvailable(): Boolean {
        return context.isInternetAvailable()
    }

    fun getString(
        @StringRes stringResId: Int,
    ): String {
        return context.getString(stringResId)
    }
}