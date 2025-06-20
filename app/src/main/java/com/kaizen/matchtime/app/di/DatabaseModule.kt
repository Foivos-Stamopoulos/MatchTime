package com.kaizen.matchtime.app.di

import android.content.Context
import androidx.room.Room
import com.kaizen.matchtime.data.local.AppDatabase
import com.kaizen.matchtime.data.local.FavoriteEventDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }


    @Provides
    fun provideFavoriteEventDao(db: AppDatabase): FavoriteEventDao {
        return db.favoriteEventDao()
    }

}