package com.app.qaimobile.di

import com.app.qaimobile.data.repository.ConversationRepositoryImpl
import com.app.qaimobile.domain.repository.ConversationRepository
import dagger.Binds
import dagger.Module
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
}
