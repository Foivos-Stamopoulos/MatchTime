package com.kaizen.matchtime.presentation.sports_screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.lifecycle.SavedStateHandle
import androidx.test.platform.app.InstrumentationRegistry
import com.kaizen.matchtime.HiltTestActivity
import com.kaizen.matchtime.R
import com.kaizen.matchtime.data.remote.dto.EventDto
import com.kaizen.matchtime.data.remote.dto.SportDto
import com.kaizen.matchtime.data.repository.FakeSportRepositoryImpl
import com.kaizen.matchtime.domain.use_case.FilterEventsUseCase
import com.kaizen.matchtime.domain.util.DataError
import com.kaizen.matchtime.domain.util.Result
import com.kaizen.matchtime.presentation.design_system.MatchTimeTheme
import com.kaizen.matchtime.presentation.util.TestTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class SportsScreenIntegrationWithViewModelTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    private val filterEventsUseCase = FilterEventsUseCase()
    private val savedStateHandle = SavedStateHandle()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var sportRepository: FakeSportRepositoryImpl
    private lateinit var viewModel: SportsViewModel

    @Before
    fun setup() {
        hiltRule.inject()
        sportRepository = FakeSportRepositoryImpl()
        viewModel = SportsViewModel(sportRepository, filterEventsUseCase, testDispatcher, savedStateHandle)
    }

    @Test
    fun sportsList_displays_loader_when_fetching_data() = runTest {
        sportRepository.apply {
            setSportsResult(Result.Success(sportDtoList))
            setFavoriteEventIds(emptyList())
        }

        val testDispatcher = StandardTestDispatcher(testScheduler)
        viewModel = SportsViewModel(sportRepository, filterEventsUseCase, testDispatcher, savedStateHandle)

        composeTestRule.setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            MatchTimeTheme {
                SportsScreenRoot(
                    viewModel, snackbarHostState
                )
            }
        }

        composeTestRule.onNodeWithTag(TestTags.LOADER).assertIsDisplayed()
    }

    @Test
    fun sportsList_rendersCorrectly_when_sport_result_is_success() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        viewModel = SportsViewModel(sportRepository, filterEventsUseCase, testDispatcher, savedStateHandle)

        sportRepository.apply {
            setSportsResult(Result.Success(sportDtoList))
            setFavoriteEventIds(emptyList())
        }

        composeTestRule.setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            MatchTimeTheme {
                SportsScreenRoot(
                    viewModel, snackbarHostState
                )
            }
        }

        advanceUntilIdle()

        composeTestRule.onRoot(useUnmergedTree = true).printToLog("currentLabelExists")

        sportDtoList.map { it.sportName }.forEach { sportName ->
            composeTestRule.onNodeWithText(sportName).assertIsDisplayed()
            composeTestRule.onNodeWithTag("${TestTags.SWITCH}_$sportName").assertIsDisplayed()
            composeTestRule.onNodeWithTag("${TestTags.EXPAND_COLLAPSE_BUTTON}_$sportName", useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun sportsList_displays_error_ui_and_snackBar_when_fetching_data_fails() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        sportRepository.setSportsResult(Result.Error(DataError.NetworkError.NO_INTERNET))
        val viewModel = SportsViewModel(sportRepository, filterEventsUseCase, testDispatcher, savedStateHandle)

        val snackbarHostState = SnackbarHostState()
        composeTestRule.setContent {
            MatchTimeTheme {
                SportsScreenRoot(
                    viewModel, snackbarHostState
                )
            }
        }

        advanceUntilIdle()

        composeTestRule.onNodeWithTag(TestTags.ERROR_UI).assertIsDisplayed()

        // Then the first message received in the Snackbar is an error message
        runBlocking {
            // snapshotFlow converts a State to a Kotlin Flow so we can observe it
            // wait for the first a non-null `currentSnackbarData`
            val actualSnackbarText = snapshotFlow { snackbarHostState.currentSnackbarData }
                .filterNotNull().first().visuals.message
            val expectedSnackbarText = InstrumentationRegistry.getInstrumentation()
                .targetContext.resources.getString(R.string.error_no_internet)
            Assert.assertEquals(expectedSnackbarText, actualSnackbarText)
        }

        // Alternatively could use
        composeTestRule.onNodeWithTag(TestTags.SNACKBAR_HOST).assertExists()
        composeTestRule.onNodeWithText("No connection").assertIsDisplayed()
    }

    @Test
    fun sport_displays_related_events_when_expand_button_is_pressed() = runTest {
        val favoriteEventId = "22925144"
        sportRepository.setSportsResult(Result.Success(sportDtoList))
        sportRepository.setFavoriteEventIds(listOf(favoriteEventId))
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val viewModel = SportsViewModel(sportRepository, filterEventsUseCase, testDispatcher, savedStateHandle)

        composeTestRule.setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            MatchTimeTheme {
                SportsScreenRoot(
                    viewModel, snackbarHostState
                )
            }
        }

        advanceUntilIdle()

        composeTestRule.onNodeWithTag("${TestTags.EXPAND_COLLAPSE_BUTTON}_SOCCER", useUnmergedTree = true).performClick()

        composeTestRule.onNodeWithText("AEK").assertIsDisplayed()
        composeTestRule.onNodeWithText("Olympiakos").assertIsDisplayed()

        val isFavoriteContentDescription = composeTestRule.activity.getString(R.string.content_description_remove_from_favorites)
        composeTestRule.onNodeWithTag("${TestTags.FAVORITE_STAR}_$favoriteEventId").assertContentDescriptionEquals(
            isFavoriteContentDescription
        )
    }

    @Test
    fun event_is_marked_as_favorite_when_star_is_pressed() = runTest {
        val eventId = "22911144"
        sportRepository.setSportsResult(Result.Success(sportDtoList))
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val viewModel = SportsViewModel(sportRepository, filterEventsUseCase, testDispatcher, savedStateHandle)

        composeTestRule.setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            MatchTimeTheme {
                SportsScreenRoot(
                    viewModel, snackbarHostState
                )
            }
        }

        advanceUntilIdle()
        composeTestRule.onNodeWithTag("${TestTags.EXPAND_COLLAPSE_BUTTON}_SOCCER", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithTag("${TestTags.FAVORITE_STAR}_$eventId").performClick()

        val isFavoriteContentDescription = composeTestRule.activity.getString(R.string.content_description_remove_from_favorites)
        composeTestRule.onNodeWithTag("${TestTags.FAVORITE_STAR}_$eventId").assertContentDescriptionEquals(isFavoriteContentDescription)
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
                    eventStartTime = "45652458"
                ),
                EventDto(
                    "22925144",
                    "FOOT",
                    "SalfordCity-Bolton",
                    "66652458"
                )
            )
        ),
        SportDto(
            sportId = "BASK",
            sportName = "BASKETBALL",
            activeEvents = listOf(
                EventDto(
                    "66925166",
                    "BASK",
                    "Iraq-Palestine",
                    "66652458"
                )
            )
        )
    )

}