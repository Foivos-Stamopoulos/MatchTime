package com.kaizen.matchtime.domain.use_case

import com.kaizen.matchtime.domain.model.Event
import com.kaizen.matchtime.domain.model.Sport
import javax.inject.Inject

class FilterEventsUseCase @Inject constructor() {

    operator fun invoke(sport: Sport, favoriteMap: Map<String, Boolean>): List<Event> {
        return if (favoriteMap[sport.id] == true) {
            sport.activeEvents.filter { it.isFavorite }
        } else {
            sport.activeEvents
        }
    }

}