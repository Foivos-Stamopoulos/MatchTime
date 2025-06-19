package com.kaizen.matchtime.app.di

import com.kaizen.matchtime.data.local.FavoriteEventDao
import com.kaizen.matchtime.data.local.RoomLocalFavoriteEventDataSource
import com.kaizen.matchtime.data.remote.RetrofitRemoteSportDataSource
import com.kaizen.matchtime.data.remote.api.SportApi
import com.kaizen.matchtime.data.repository.SportRepositoryImpl
import com.kaizen.matchtime.domain.LocalFavoriteEventDataSource
import com.kaizen.matchtime.domain.RemoteSportDataSource
import com.kaizen.matchtime.domain.repository.SportRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SportRepositoryModule {

    @Provides
    @Singleton
    fun provideLocalFavoriteEventDataSource(dao: FavoriteEventDao): LocalFavoriteEventDataSource {
        return RoomLocalFavoriteEventDataSource(dao)
    }

    @Provides
    @Singleton
    fun provideRemoteSportDataSource(api: SportApi): RemoteSportDataSource {
        return RetrofitRemoteSportDataSource(api)
    }

    @Provides
    @Singleton
    fun provideSportRepository(
        remoteSportDataSource: RemoteSportDataSource,
        localFavoriteEventDataSource: LocalFavoriteEventDataSource
    ): SportRepository {
        return SportRepositoryImpl(remoteSportDataSource, localFavoriteEventDataSource)
    }

}