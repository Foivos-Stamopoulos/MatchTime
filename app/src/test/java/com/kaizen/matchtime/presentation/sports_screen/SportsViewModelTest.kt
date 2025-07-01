package com.kaizen.matchtime.presentation.sports_screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.kaizen.matchtime.data.remote.dto.EventDto
import com.kaizen.matchtime.data.remote.dto.SportDto
import com.kaizen.matchtime.data.repository.FakeSportRepositoryImpl
import com.kaizen.matchtime.domain.use_case.FilterEventsUseCase
import com.kaizen.matchtime.domain.util.DataError
import com.kaizen.matchtime.domain.util.Result
import com.kaizen.matchtime.presentation.model.EventUI
import com.kaizen.matchtime.presentation.model.SportUI
import com.kaizen.matchtime.presentation.util.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SportsViewModelTest {

    private lateinit var sportRepository: FakeSportRepositoryImpl
    private lateinit var viewModel: SportsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        sportRepository = FakeSportRepositoryImpl()
        viewModel = SportsViewModel(
            repository = sportRepository,
            filterEventsUseCase = FilterEventsUseCase(),
            testDispatcher,
            SavedStateHandle()
        )
    }

    @Test
    fun sports_screen_initially_shows_loader_and_on_success_displays_data() = runTest {

        sportRepository.setSportsResult(Result.Success(sportDtoList))

        viewModel.uiState.test {
            assertEquals(SportsUiState(sports = emptyList(), isLoading = true, isError = false), awaitItem())
            assertEquals(SportsUiState(sports = expectedSportList, isLoading = false, isError = false), awaitItem())
            cancel()
        }
    }

    @Test
    fun sports_screen_shows_error_ui_and_snackBar_when_no_connection() = runTest {

        sportRepository.setSportsResult(Result.Error(DataError.NetworkError.NO_INTERNET))

        val uiStateJob = launch {
            viewModel.uiState
                .take(2) // take loading + error state
                .collect()
        }

        viewModel.events.test {
            val event = awaitItem()
            assertTrue(event is SportEvent.ShowSnackbar)
            cancelAndIgnoreRemainingEvents()
        }

        uiStateJob.cancel()
    }

    @Test
    fun on_toggle_filter_favorite_events_returns_no_favorite_events_when_not_any_marked_as_favorite()  = runTest{
        val sportDto = SportDto(
            sportId = "FOOT",
            sportName = "SOCCER",
            activeEvents = listOf(
                EventDto(eventId = "22911144", sportId = "FOOT", eventName = "AEK-Olympiakos", eventStartTime = getStartTimeInSeconds())))
        val sportDtoList = listOf(sportDto)

        sportRepository.setSportsResult(Result.Success(sportDtoList))
        viewModel.onAction(SportAction.OnToggleFilterFavoriteEvents(sportDtoList.first().sportId))

        viewModel.uiState.test {
            awaitItem() // loading state
            val stateToggledSportFilter = awaitItem()
            assertTrue(stateToggledSportFilter.sports.first().events.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun on_toggle_filter_favorite_events_returns_only_favorite_events()  = runTest{
        // Given
        val sportDto = SportDto(
            sportId = "FOOT",
            sportName = "SOCCER",
            activeEvents = listOf(
                EventDto(eventId = "22911144", sportId = "FOOT", eventName = "AEK-Olympiakos", eventStartTime = getStartTimeInSeconds()),
                EventDto(eventId = "33911144", sportId = "FOOT", eventName = "PAO-Xanthi", eventStartTime = getStartTimeInSeconds()))
        )
        val sportDtoList = listOf(sportDto)
        sportRepository.setSportsResult(Result.Success(sportDtoList))
        sportRepository.setFavoriteEventIds(listOf(sportDto.activeEvents.first().eventId))

        // When: Toggle filter to show only favorites
        viewModel.onAction(SportAction.OnToggleFilterFavoriteEvents(sportDto.sportId))

        // Then: Check that only favorite events are shown in uiState
        viewModel.uiState.test {
            awaitItem() // isLoading = true
            val state = awaitItem()
            val filteredEvents = state.sports.first { it.id == sportDto.sportId }.events
            assertEquals(1, filteredEvents.size)
            assertEquals("22911144", filteredEvents.first().id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refresh_button_triggers_data_loading() = runTest {
        // Given
        sportRepository.setSportsResult(Result.Success(sportDtoList))

        // When
        viewModel.onAction(SportAction.Refresh)

        // Then
        viewModel.uiState.test {
            assertEquals(SportsUiState(sports = emptyList(), isLoading = true, isError = false), awaitItem())
            assertEquals(SportsUiState(sports = expectedSportList, isLoading = false, isError = false), awaitItem())
        }
    }

    @Test
    fun on_toggle_expand_sets_sport_state_to_expanded() = runTest {
        // Given
        val sportSoccerDto = SportDto(
            sportId = "FOOT",
            sportName = "SOCCER",
            activeEvents = listOf(
                EventDto(eventId = "22911144", sportId = "FOOT", eventName = "AEK-Olympiakos", eventStartTime = getStartTimeInSeconds()))
        )
        val sportTennisDto = SportDto(
            sportId = "TENN",
            sportName = "TENNIS",
            activeEvents = listOf()
        )
        val sportDtoList = listOf(sportSoccerDto, sportTennisDto)
        sportRepository.setSportsResult(Result.Success(sportDtoList))

        // When
        viewModel.onAction(SportAction.OnToggleExpand(sportSoccerDto.sportId))

        // Then
        viewModel.uiState.test {
            awaitItem() // loading state
            val expandedSportState = awaitItem()
            assertTrue(expandedSportState.sports[0].isExpanded)
            assertFalse(expandedSportState.sports[1].isExpanded)
        }
    }

    @Test
    fun ensures_that_count_down_for_event_works_properly() = runTest {
        // Given
        val sportSoccerDto = SportDto(
            sportId = "FOOT",
            sportName = "SOCCER",
            activeEvents = listOf(
                EventDto(eventId = "22911144", sportId = "FOOT", eventName = "AEK-Olympiakos", eventStartTime = getStartTimeInSeconds()))
        )
        val sportDtoList = listOf(sportSoccerDto)
        sportRepository.setSportsResult(Result.Success(sportDtoList))

        viewModel.uiState.test {
            awaitItem() // loading state

            val state1 = awaitItem()
            assertEquals(UiText.DynamicString("00:00:02"), state1.sports.first().events.first().countdown)

            val nowInSeconds = System.currentTimeMillis() / 1000
            viewModel.updateNow(nowInSeconds + 1)

            val state2 = awaitItem()
            assertEquals(UiText.DynamicString("00:00:01"), state2.sports.first().events.first().countdown)

            viewModel.updateNow(nowInSeconds + 2)
            val state3 = awaitItem()
            assert(state3.sports.first().events.first().countdown is UiText.StringResource) // String resource for Started

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun on_event_favorite_click_sets_event_as_favorite() = runTest {
        // Given
        val sportDto = SportDto(
            sportId = "FOOT",
            sportName = "SOCCER",
            activeEvents = listOf(
                EventDto(eventId = "22911144", sportId = "FOOT", eventName = "AEK-Olympiakos", eventStartTime = getStartTimeInSeconds()),
                EventDto(eventId = "33911144", sportId = "FOOT", eventName = "PAO-Xanthi", eventStartTime = getStartTimeInSeconds()))
        )
        val sportDtoList = listOf(sportDto)
        sportRepository.setSportsResult(Result.Success(sportDtoList))

        // When
        viewModel.onAction(SportAction.OnEventFavoriteClick(sportDtoList.first().activeEvents.first().eventId, isNowFavorite = true))

        // Then
        viewModel.uiState.test {
            awaitItem() // loading state
            val stateWithFavoriteEvent = awaitItem()
            assertTrue(stateWithFavoriteEvent.sports.first().events[0].isFavorite)
            assertFalse(stateWithFavoriteEvent.sports.first().events[1].isFavorite)
        }
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    private val sportDtoList = listOf(
        SportDto(
            sportId = "FOOT",
            sportName = "SOCCER",
            activeEvents = listOf(
                EventDto(
                    eventId = "22911144",
                    sportId = "FOOT",
                    eventName = "AEK-Olympiakos",
                    eventStartTime = getStartTimeInSeconds()
                )
            )
        )
    )

    private val expectedSportList = listOf(
        SportUI(
            id = "FOOT",
            name = "SOCCER",
            isExpanded = false,
            showOnlyFavorites = false,
            icon = Icons.Default.SportsSoccer,
            events = listOf(
                EventUI(
                    id = "22911144",
                    sportId = "FOOT",
                    competitor1 = "AEK",
                    competitor2 = "Olympiakos",
                    isFavorite = false,
                    countdown = UiText.DynamicString("00:00:02")
                )
            )
        )
    )

    private fun getStartTimeInSeconds(): String {
        val currentTimeInSeconds = System.currentTimeMillis() / 1000
        return (currentTimeInSeconds + 2).toString()
    }

}