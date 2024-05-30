package com.app.qaimobile.di

import com.app.qaimobile.data.local.ConversationSessionDao
import com.app.qaimobile.data.remote.ApiService
import com.app.qaimobile.domain.repository.ConversationRepository
import com.app.qaimobile.repository.ConversationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConversationRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindConversationRepository(
        conversationRepositoryImpl: ConversationRepositoryImpl
    ): ConversationRepository

    companion object {
        @Provides
        @Singleton
        fun provideConversationRepositoryImpl(
            conversationSessionDao: ConversationSessionDao,
            apiService: ApiService
        ): ConversationRepositoryImpl {
            return ConversationRepositoryImpl(conversationSessionDao, apiService)
        }
    }
}
