package com.readyaid.di

import android.content.Context
import androidx.room.Room
import com.readyaid.data.profile.AppDatabase
import com.readyaid.data.profile.ChatMessageDao
import com.readyaid.data.profile.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "readyaid_profile_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(appDatabase: AppDatabase): UserProfileDao {
        return appDatabase.userProfileDao()
    }

    @Provides
    @Singleton
    fun provideChatMessageDao(appDatabase: AppDatabase): ChatMessageDao {
        return appDatabase.chatMessageDao()
    }

    @Provides
    @Singleton
    fun provideRagClient(): com.readyaid.data.rag.RagClient {
        return com.readyaid.data.rag.ServerRagClient()
    }
}
