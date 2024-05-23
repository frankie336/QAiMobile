// com/app/qaimobile/di/NetworkModule.kt
package com.app.qaimobile.di

import com.app.qaimobile.util.Constants
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing network dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides base URL for the Retrofit instance.
     */
    @Provides
    fun provideBaseUrl(): String = "http://localhost:5000/" // Base URL set to your development server

    @Singleton
    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Accept", "application/json")
                    .method(original.method, original.body)
                val request = requestBuilder.build()
                chain.proceed(request)
            }.build()

    /**
     * Provides HttpLoggingInterceptor for logging network requests and responses.
     * @return HttpLoggingInterceptor instance.
     */
    @Singleton
    @Provides
    fun provideHttpInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    // Removed provideGson to avoid duplicate bindings
}