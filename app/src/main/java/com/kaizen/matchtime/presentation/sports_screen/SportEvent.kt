package com.kaizen.matchtime.presentation.sports_screen

import com.kaizen.matchtime.presentation.util.UiText

sealed interface SportEvent {
    data class ShowSnackbar(val message: UiText): SportEvent
}