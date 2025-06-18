package com.kaizen.matchtime.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EventDto(

    @SerializedName("i")
    val eventId: String,

    @SerializedName("si")
    val sportId: String,

    @SerializedName("d")
    val eventName: String,

    @SerializedName("tt")
    val eventStartTime: String

)