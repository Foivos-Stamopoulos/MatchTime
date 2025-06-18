package com.kaizen.matchtime.data.mapper

import com.kaizen.matchtime.data.remote.dto.SportDto
import com.kaizen.matchtime.domain.model.Sport

fun SportDto.toDomain(favoriteEventIds: Set<String>): Sport {
    return Sport(
        id = sportId,
        name = sportName,
        activeEvents = activeEvents.map { it.toDomain(favoriteEventIds) }
    )
}