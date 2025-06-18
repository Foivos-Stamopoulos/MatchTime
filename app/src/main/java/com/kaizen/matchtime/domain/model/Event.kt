package com.kaizen.matchtime.domain.model

data class Event (

    val id: String,

    val sportId: String,

    val name: String,

    val startTime: String,

    val isFavorite: Boolean

)