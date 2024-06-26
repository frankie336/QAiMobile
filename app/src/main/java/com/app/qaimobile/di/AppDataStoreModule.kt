package com.app.qaimobile.di

import android.content.Context
import com.app.qaimobile.data.datastore.DataStoreManager
import com.app.qaimobile.domain.datastore.AppDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    abstract fun bindAppDataStore(dataStoreManager: DataStoreManager): AppDataStore

    companion object {
        @Provides
        @Singleton
        fun provideDataStoreManager(context: Context): DataStoreManager {
            return DataStoreManager(context)
        }

        @Provides
        @Singleton
        fun provideDataStore(dataStoreManager: DataStoreManager): DataStore<Preferences> {
            return dataStoreManager.dataStore
        }
    }
}
