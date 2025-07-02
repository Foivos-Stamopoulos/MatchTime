package com.kaizen.matchtime.presentation.sports_screen

import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.kaizen.matchtime.R
import com.kaizen.matchtime.presentation.design_system.MatchTimeTheme
import com.kaizen.matchtime.presentation.model.EventUI
import com.kaizen.matchtime.presentation.model.SportUI
import com.kaizen.matchtime.presentation.util.TestTags
import com.kaizen.matchtime.presentation.util.UiText
import org.junit.Rule
import org.junit.Test

class SportsScreenUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun sportsList_renders_correctly_when_sport_is_collapsed() {
        composeTestRule.setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            MatchTimeTheme {
                SportsScreen(
                    state = SportsUiState(
                        sports = collapsedSportsList,
                        isLoading = false,
                        isError = false
                    ),
                    snackbarHostState = snackbarHostState
                ) { }
            }
        }

        composeTestRule.onNodeWithText("SOCCER").assertIsDisplayed()
        val expandArrow = composeTestRule.activity.getString(R.string.content_description_expand_sport)
        composeTestRule.onNodeWithContentDescription(expandArrow).assertIsDisplayed()

        composeTestRule.onNodeWithText("01:22:00").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("AEK").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Olympiakos").assertIsNotDisplayed()
    }

    @Test
    fun sportsList_renders_correctly_when_sport_is_expanded() {
        composeTestRule.setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            MatchTimeTheme {
                SportsScreen(
                    state = SportsUiState(
                        sports = expandedSportsList,
                        isLoading = false,
                        isError = false
                    ),
                    snackbarHostState = snackbarHostState
                ) { }
            }
        }

        composeTestRule.onNodeWithText("SOCCER").assertIsDisplayed()
        val collapseArrow = composeTestRule.activity.getString(R.string.content_description_collapse_sport)
        composeTestRule.onNodeWithContentDescription(collapseArrow).assertIsDisplayed()
        composeTestRule.onNodeWithText("01:22:00").assertIsDisplayed()
        composeTestRule.onNodeWithText("AEK").assertIsDisplayed()
        composeTestRule.onNodeWithText("Olympiakos").assertIsDisplayed()
    }

    @Test
    fun progress_indicator_is_displayed_when_screen_is_loading() {
        composeTestRule.setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            MatchTimeTheme {
                SportsScreen(
                    state = SportsUiState(
                        sports = emptyList(),
                        isLoading = true,
                        isError = false
                    ),
                    snackbarHostState = snackbarHostState
                ) { }
            }
        }

        composeTestRule.onNodeWithTag(TestTags.LOADER).assertIsDisplayed()
    }

    @Test
    fun error_ui_is_displayed_when_state_has_error() {
        composeTestRule.setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            MatchTimeTheme {
                SportsScreen(
                    state = SportsUiState(
                        sports = emptyList(),
                        isLoading = false,
                        isError = true
                    ),
                    snackbarHostState = snackbarHostState
                ) { }
            }
        }

        composeTestRule.onNodeWithTag(TestTags.ERROR_UI).assertIsDisplayed()
    }

    private val expandedSportsList = listOf(
        SportUI(
            id = "FOOT",
            name = "SOCCER",
            isExpanded = true,
            showOnlyFavorites = false,
            icon = Icons.Default.SportsSoccer,
            events = listOf(
                EventUI(
                    "22911144",
                    "FOOT",
                    "AEK",
                    "Olympiakos",
                    false,
                    UiText.DynamicString("01:22:00")
                ),
                EventUI(
                    "22925144",
                    "FOOT",
                    "SalfordCity",
                    "Bolton",
                    true,
                    UiText.DynamicString("03:25:00")
                )
            )
        )
    )

    private val collapsedSportsList = listOf(
        SportUI(
            id = "FOOT",
            name = "SOCCER",
            isExpanded = false,
            showOnlyFavorites = false,
            icon = Icons.Default.SportsSoccer,
            events = listOf(
                EventUI(
                    "22911144",
                    "FOOT",
                    "AEK",
                    "Olympiakos",
                    false,
                    UiText.DynamicString("01:22:00")
                ),
                EventUI(
                    "22925144",
                    "FOOT",
                    "SalfordCity",
                    "Bolton",
                    true,
                    UiText.DynamicString("03:25:00")
                )
            )
        )
    )

}