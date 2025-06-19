package com.kaizen.matchtime.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteEventDao {

    @Query("SELECT eventId FROM favorite_events")
    fun getFavoriteEventIdsFlow(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavoriteEventId(entity: FavoriteEventEntity)

    @Query("DELETE FROM favorite_events WHERE eventId = :eventId")
    fun deleteById(eventId: String)
}