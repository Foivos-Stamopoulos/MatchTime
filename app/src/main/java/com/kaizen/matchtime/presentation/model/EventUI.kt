package com.kaizen.matchtime.presentation.model

import com.kaizen.matchtime.presentation.util.UiText

data class EventUI (

    val id: String,

    val sportId: String,

    val competitor1: String,

    val competitor2: String,

    val isFavorite: Boolean,

    val countdown: UiText

)