package com.kaizen.matchtime.presentation.model

data class SportUI (

    val id: String,

    val name: String,

    val isExpanded: Boolean = false,

    val showOnlyFavorites: Boolean = false,

    val events: List<EventUI>

)