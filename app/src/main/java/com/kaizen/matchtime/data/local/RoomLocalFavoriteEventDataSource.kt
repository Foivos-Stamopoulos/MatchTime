package com.kaizen.matchtime.data.local

import com.kaizen.matchtime.domain.LocalFavoriteEventDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomLocalFavoriteEventDataSource @Inject constructor(
    private val dao: FavoriteEventDao
) : LocalFavoriteEventDataSource {

    override fun getFavoriteEventIdsFlow(): Flow<List<String>> {
        return dao.getFavoriteEventIdsFlow()
    }

    override suspend fun insertFavoriteEvent(eventId: String) {
        dao.insertFavoriteEventId(FavoriteEventEntity(eventId))
    }

    override suspend fun deleteFavoriteEvent(eventId: String) {
        dao.deleteById(eventId = eventId)
    }

}