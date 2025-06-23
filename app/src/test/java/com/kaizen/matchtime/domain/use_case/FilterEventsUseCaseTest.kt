package com.kaizen.matchtime.domain.use_case

import com.kaizen.matchtime.domain.model.Event
import com.kaizen.matchtime.domain.model.Sport
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterEventsUseCaseTest {

    private val useCase = FilterEventsUseCase()

    @Test
    fun `returns only favorite events when favoriteMap is true for sport`() {
        val sport = Sport(
            id = "FOOT",
            name = "SOCCER",
            activeEvents = listOf(
                Event(
                    "22911144",
                    "FOOT",
                    "AEK-Olympiakos",
                    "22911144",
                    false
                ),
                Event(
                    "33911144",
                    "FOOT",
                    "PAO-Xanthi",
                    "22911144",
                    true
                )
            )
        )

        val favoriteMap = mapOf("FOOT" to true)

        val result = useCase(sport, favoriteMap)

        assertEquals(1, result.size)
        assert(result.all { it.isFavorite })
    }

    @Test
    fun `returns all events when favoriteMap is false for sport`() {
        val sport = Sport(
            id = "FOOT",
            name = "SOCCER",
            activeEvents = listOf(
                Event(
                    "22911144",
                    "FOOT",
                    "AEK-Olympiakos",
                    "22911144",
                    false
                ),
                Event(
                    "33911144",
                    "FOOT",
                    "PAO-Xanthi",
                    "22911144",
                    true
                )
            )
        )
        val favoriteMap = mapOf("FOOT" to false)

        val result = useCase(sport, favoriteMap)

        assertEquals(2, result.size)
    }


    @Test
    fun `returns all events when sport is not in favoriteMap`() {
        val sport = Sport(
            id = "FOOT",
            name = "SOCCER",
            activeEvents = listOf(
                Event(
                    "22911144",
                    "FOOT",
                    "AEK-Olympiakos",
                    "22911144",
                    false
                ),
                Event(
                    "33911144",
                    "FOOT",
                    "PAO-Xanthi",
                    "22911144",
                    true
                )
            )
        )
        val favoriteMap = emptyMap<String, Boolean>()

        val result = useCase(sport, favoriteMap)

        assertEquals(2, result.size)
    }

}