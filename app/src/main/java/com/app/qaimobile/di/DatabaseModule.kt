package com.app.qaimobile.di

import android.content.Context
import androidx.room.Room
import com.app.qaimobile.data.local.ConversationSessionDao
import com.app.qaimobile.data.local.RoomDatabaseHelper
import com.app.qaimobile.data.local.LocalDatabaseContract
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): RoomDatabaseHelper {
        return Room.databaseBuilder(
            context.applicationContext,
            RoomDatabaseHelper::class.java,
            LocalDatabaseContract.DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideConversationSessionDao(database: RoomDatabaseHelper): ConversationSessionDao {
        return database.conversationSessionDao()
    }
}
