package com.kaizen.matchtime.presentation.sports_screen

import app.cash.turbine.test
import com.kaizen.matchtime.R
import com.kaizen.matchtime.presentation.mapper.formatCountdown
import com.kaizen.matchtime.presentation.util.UiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CountdownTimerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private fun fakeTickerFlow(): Flow<Long> = flow {
        var now = 0L
        while (true) {
            emit(now++)
            delay(1000)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `countdown updates every second`() = testScope.runTest {
        val startTimeSeconds = 3L

        val countdownFlow = fakeTickerFlow().map { nowSeconds ->
            formatCountdown(startTimeSeconds, nowSeconds)
        }

        countdownFlow.test {
            assertEquals(UiText.DynamicString("00:00:03"), awaitItem())
            advanceTimeBy(1000)
            assertEquals(UiText.DynamicString("00:00:02"), awaitItem())
            advanceTimeBy(1000)
            assertEquals(UiText.DynamicString("00:00:01"), awaitItem())
            advanceTimeBy(1000)
            assertEquals(R.string.label_started, (awaitItem() as UiText.StringResource).id)

            cancelAndIgnoreRemainingEvents()
        }
    }

}