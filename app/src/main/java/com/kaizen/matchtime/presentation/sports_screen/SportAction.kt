package com.kaizen.matchtime.presentation.sports_screen

sealed interface SportAction {
    data class OnToggleFilterFavoriteEvents(val sportId: String): SportAction
    data class OnToggleExpand(val sportId: String): SportAction
    data class OnEventFavoriteClick(val eventId: String, val isNowFavorite: Boolean): SportAction
}