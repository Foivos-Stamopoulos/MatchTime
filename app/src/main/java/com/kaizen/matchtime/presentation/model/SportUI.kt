package com.kaizen.matchtime.presentation.model

import androidx.compose.ui.graphics.vector.ImageVector

data class SportUI (

    val id: String,

    val name: String,

    val isExpanded: Boolean = false,

    val showOnlyFavorites: Boolean = false,

    val icon: ImageVector,

    val events: List<EventUI>

)