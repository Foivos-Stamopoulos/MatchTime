package com.kaizen.matchtime.data.mapper

import com.kaizen.matchtime.data.remote.dto.EventDto
import com.kaizen.matchtime.domain.model.Event

fun EventDto.toDomain(favoriteEventIds: Set<String>): Event {
    return Event(
        id = eventId,
        sportId = sportId,
        name = eventName,
        startTime = eventStartTime,
        isFavorite = eventId in favoriteEventIds
    )
}