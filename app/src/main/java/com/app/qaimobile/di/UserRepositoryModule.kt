package com.app.qaimobile.di

import com.app.qaimobile.data.remote.ApiService
import com.app.qaimobile.data.repository.UserRepositoryImpl
import com.app.qaimobile.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserRepositoryModule {

    /**
     * Provides the user repository.
     */
    @Provides
    @Singleton
    fun provideUserRepository(apiService: ApiService): UserRepository {
        return UserRepositoryImpl(apiService)
    }
}
