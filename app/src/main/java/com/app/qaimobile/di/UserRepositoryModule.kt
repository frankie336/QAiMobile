package com.app.qaimobile.di

import android.content.Context
import com.app.qaimobile.data.remote.UserDataSource
import com.app.qaimobile.data.repository.UserRepositoryImpl
import com.app.qaimobile.domain.datastore.AppDataStore
import com.app.qaimobile.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserRepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(dataSource: UserDataSource, appDataStore: AppDataStore): UserRepository {
        return UserRepositoryImpl(dataSource, appDataStore)
    }
}
