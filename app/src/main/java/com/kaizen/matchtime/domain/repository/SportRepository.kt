package com.kaizen.matchtime.domain.repository

import com.kaizen.matchtime.domain.model.Sport
import com.kaizen.matchtime.domain.util.DataError
import com.kaizen.matchtime.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface SportRepository {

    fun getSportsWithFavoriteEvents(): Flow<Result<List<Sport>, DataError.NetworkError>>

    suspend fun setFavoriteEvent(eventId: String, isFavorite: Boolean)

}