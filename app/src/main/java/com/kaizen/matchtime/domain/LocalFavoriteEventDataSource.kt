package com.kaizen.matchtime.domain

import kotlinx.coroutines.flow.Flow

interface LocalFavoriteEventDataSource {

    fun getFavoriteEventIdsFlow(): Flow<List<String>>

    suspend fun insertFavoriteEvent(eventId: String)

    suspend fun deleteFavoriteEvent(eventId: String)
}