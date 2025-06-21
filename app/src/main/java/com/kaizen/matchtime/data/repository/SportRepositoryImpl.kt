package com.kaizen.matchtime.data.repository

import com.kaizen.matchtime.data.mapper.toDomain
import com.kaizen.matchtime.domain.LocalFavoriteEventDataSource
import com.kaizen.matchtime.domain.RemoteSportDataSource
import com.kaizen.matchtime.domain.model.Sport
import com.kaizen.matchtime.domain.repository.SportRepository
import com.kaizen.matchtime.domain.util.DataError
import com.kaizen.matchtime.domain.util.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class SportRepositoryImpl @Inject constructor(
    private val remoteSportDataSource: RemoteSportDataSource,
    private val localFavoriteEventDataSource: LocalFavoriteEventDataSource
) : SportRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSportsWithFavoriteEvents(): Flow<Result<List<Sport>, DataError.NetworkError>> {
        return flow {
            val result = remoteSportDataSource.fetchSports()
            emit(result)
        }.flatMapLatest { result ->
            when (result) {
                is Result.Error -> {
                    flowOf(Result.Error(result.error))
                }
                is Result.Success -> {
                    localFavoriteEventDataSource.getFavoriteEventIdsFlow().map { favoriteEventIds ->
                        val favoriteEventIdSet = favoriteEventIds.toSet()
                        val domainSports = result.data.map { it.toDomain(favoriteEventIdSet) }
                        Result.Success(domainSports)
                    }
                }
            }
        }
    }

    override suspend fun setFavoriteEvent(eventId: String, isFavorite: Boolean) {
        if (isFavorite) {
            localFavoriteEventDataSource.insertFavoriteEvent(eventId)
        } else {
            localFavoriteEventDataSource.deleteFavoriteEvent(eventId)
        }
    }
}