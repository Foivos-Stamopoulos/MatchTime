package com.kaizen.matchtime.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SportDto(

    @SerializedName("i")
    val sportId: String,

    @SerializedName("d")
    val sportName: String,

    @SerializedName("e")
    val activeEvents: List<EventDto>

)