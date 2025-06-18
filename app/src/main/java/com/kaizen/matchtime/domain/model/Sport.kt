package com.kaizen.matchtime.domain.model

data class Sport(

    val id: String,

    val name: String,

    val activeEvents: List<Event>

)