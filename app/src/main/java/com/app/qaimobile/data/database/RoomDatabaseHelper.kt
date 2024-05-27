package com.app.qaimobile.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context

@Database(entities = [ConversationSession::class], version = LocalDatabaseContract.DATABASE_VERSION, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RoomDatabaseHelper : RoomDatabase() {

    abstract fun conversationSessionDao(): ConversationSessionDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDatabaseHelper? = null

        fun getDatabase(context: Context): RoomDatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDatabaseHelper::class.java,
                    LocalDatabaseContract.DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
