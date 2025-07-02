package com.kaizen.matchtime.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteEventDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: FavoriteEventDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = db.favoriteEventDao()
    }

    @Test
    fun returns_inserted_ids() = runTest{
        dao.insertFavoriteEventId(FavoriteEventEntity("id1"))
        dao.insertFavoriteEventId(FavoriteEventEntity("id2"))

        val idListResult = dao.getFavoriteEventIdsFlow().first()
        assertEquals(listOf("id1", "id2"), idListResult)
    }

    @Test
    fun prevents_duplicate_insertion() = runTest {
        // Given
        val eventId = "id1"

        // When
        dao.insertFavoriteEventId(FavoriteEventEntity(eventId))
        dao.insertFavoriteEventId(FavoriteEventEntity(eventId))

        // Then
        val idListResult = dao.getFavoriteEventIdsFlow().first()
        assertEquals(listOf(eventId), idListResult)
    }

    @Test
    fun deletes_favorite_events() = runTest {
        // Given
        val eventId = "id1"

        // When
        dao.deleteById(eventId)

        // Then
        val idListResult = dao.getFavoriteEventIdsFlow().first()
        assertTrue(idListResult.isEmpty())
    }

    @Test
    fun emits_updates_on_insert_and_delete() = runTest {
        val id1 = "id1"
        val id2 = "id2"

        dao.getFavoriteEventIdsFlow().test {
            assertEquals(emptyList<String>(), awaitItem())

            dao.insertFavoriteEventId(FavoriteEventEntity(eventId = id1))
            assertEquals(listOf(id1), awaitItem())

            dao.insertFavoriteEventId(FavoriteEventEntity(eventId = id2))
            assertEquals(listOf(id1, id2), awaitItem())

            dao.deleteById(id1)
            assertEquals(listOf(id2), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

}