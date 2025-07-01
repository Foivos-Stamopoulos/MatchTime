package com.kaizen.matchtime.data.repository

import com.kaizen.matchtime.data.mapper.toDomain
import com.kaizen.matchtime.data.remote.dto.SportDto
import com.kaizen.matchtime.domain.model.Sport
import com.kaizen.matchtime.domain.repository.SportRepository
import com.kaizen.matchtime.domain.util.DataError
import com.kaizen.matchtime.domain.util.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class FakeSportRepositoryImpl: SportRepository {

    private var sportsResult: Result<List<SportDto>, DataError.NetworkError> = Result.Success(emptyList())
    private var favoriteEventIdsFlow: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSportsWithFavoriteEvents(): Flow<Result<List<Sport>, DataError.NetworkError>> {
        return flowOf(sportsResult)
            .onStart { delay(500) }
            .flatMapLatest { result ->
                when (result) {
                    is Result.Error -> {
                        flowOf(Result.Error(result.error))
                    }
                    is Result.Success -> {
                        favoriteEventIdsFlow.map { favoriteEventIds ->
                            val favoriteEventIdSet = favoriteEventIds.toSet()
                            val domainSports = result.data.map { it.toDomain(favoriteEventIdSet) }
                            Result.Success(domainSports)
                        }
                    }
                }
            }
    }

    override suspend fun setFavoriteEvent(eventId: String, isFavorite: Boolean) {
        val current = favoriteEventIdsFlow.value.toMutableList()
        if (isFavorite && !current.contains(eventId)) {
            current.add(eventId)
        } else if (!isFavorite) {
            current.remove(eventId)
        }
        favoriteEventIdsFlow.value = current
    }

    fun setSportsResult(result: Result<List<SportDto>, DataError.NetworkError>) {
        sportsResult = result
    }

    fun setFavoriteEventIds(ids: List<String>) {
        favoriteEventIdsFlow.value = ids
    }

}