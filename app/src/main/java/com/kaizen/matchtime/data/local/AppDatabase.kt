package com.kaizen.matchtime.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteEventEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteEventDao(): FavoriteEventDao
}