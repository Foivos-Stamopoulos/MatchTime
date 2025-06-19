package com.kaizen.matchtime.presentation.sports_screen

import com.kaizen.matchtime.presentation.util.UiText

sealed interface SportEvent {
    data class ShowMessage(val error: UiText): SportEvent
}