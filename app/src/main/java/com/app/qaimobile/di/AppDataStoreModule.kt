package com.app.qaimobile.di

import android.content.Context
import com.app.qaimobile.data.datastore.DataStoreManager
import com.app.qaimobile.domain.datastore.AppDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDataStoreModule {

    /**
     * Provides the user repository.
     */
    @Provides
    @Singleton
    fun provideAppDataStore(@ApplicationContext context: Context): AppDataStore {
        return DataStoreManager(context)
    }
}
