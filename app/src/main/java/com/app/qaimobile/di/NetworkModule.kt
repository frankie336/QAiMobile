package com.app.qaimobile.di

import com.app.qaimobile.data.remote.ApiService
import com.app.qaimobile.util.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    fun provideBaseUrl() = Constants.BASE_URL

    /**
     * Provides Gson instance for JSON serialization and deserialization.
     * @return Gson instance.
     */
    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    /**
     * Provides HttpLoggingInterceptor for logging network requests and responses.
     * @return HttpLoggingInterceptor instance.
     */
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
                val requestBuilder =
                    original.newBuilder()
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
    fun provideHttpInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * Provides Retrofit instance for making network requests.
     * @param baseUrl Base URL for the Retrofit instance.
     * @param gson Gson instance for JSON serialization and deserialization.
     * @param okHttpClient OkHttpClient instance for configuring network requests.
     * @return Retrofit instance.
     */
    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, gson: Gson, okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

    /**
     * Provides ApiService instance for making network requests.
     * @param retrofit Retrofit instance for making network requests.
     * @return ApiService instance.
     */
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}
