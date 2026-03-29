package com.readyaid.data.profile

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserProfile::class, ChatMessageEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun chatMessageDao(): ChatMessageDao
}
