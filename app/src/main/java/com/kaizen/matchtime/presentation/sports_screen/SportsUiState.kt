package com.kaizen.matchtime.presentation.sports_screen

import com.kaizen.matchtime.presentation.model.SportUI

data class SportsUiState (

    val sports: List<SportUI> = emptyList(),

    val isLoading: Boolean  = false,

    val isError: Boolean = false

)