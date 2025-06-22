package com.kaizen.matchtime.presentation.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BlurCircular
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material.icons.filled.SportsHandball
import androidx.compose.material.icons.filled.SportsHockey
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.ui.graphics.vector.ImageVector
import com.kaizen.matchtime.R
import com.kaizen.matchtime.domain.model.Event
import com.kaizen.matchtime.domain.model.Sport
import com.kaizen.matchtime.presentation.model.EventUI
import com.kaizen.matchtime.presentation.model.SportUI
import com.kaizen.matchtime.presentation.util.UiText
import java.util.Locale

fun Sport.toUI(nowInSeconds: Long, expandedMap: Map<String, Boolean>): SportUI {
    return SportUI(
        id = id,
        name = name,
        showOnlyFavorites = false,
        isExpanded = expandedMap[id] == true,
        icon = SportIconMapper.getIconForSport(id),
        events = activeEvents.map { it.toUI(nowInSeconds) }
    )
}


fun Event.toUI(nowInSeconds: Long): EventUI {
    val (competitor1, competitor2) = name.toCompetitorNames()
    val startTimeInSeconds = startTime.toLongOrNull() ?: 0L
    return EventUI(
        id = id,
        sportId = sportId,
        competitor1 = competitor1,
        competitor2 = competitor2,
        isFavorite = isFavorite,
        countdown = formatCountdown(startTimeInSeconds, nowInSeconds)
    )
}

private fun String.toCompetitorNames(): Pair<String, String> {
    val parts = split("-").map { it.trim() }
    return if (parts.size == 2) {
        parts[0] to parts[1]
    } else {
        "" to ""
    }
}

private fun formatCountdown(startTimeInSeconds: Long, nowInSeconds: Long): UiText {
    val secondsLeft = nowInSeconds - startTimeInSeconds
    if (secondsLeft <= 0) return UiText.StringResource(R.string.label_started)
    val h = secondsLeft / 3600
    val m = (secondsLeft % 3600) / 60
    val s = secondsLeft % 60
    return UiText.DynamicString(String.format(Locale.getDefault(),"%02d:%02d:%02d", h, m, s))
}

object SportIconMapper {

    fun getIconForSport(sportId: String): ImageVector {
        return when (sportId.uppercase()) {
            "FOOT" -> Icons.Default.SportsSoccer
            "BASK" -> Icons.Default.SportsBasketball
            "TENN" -> Icons.Default.SportsTennis
            "TABL" -> Icons.Default.SportsTennis
            "VOLL" -> Icons.Default.SportsVolleyball
            "ESPS" -> Icons.Default.SportsEsports
            "ICEH" -> Icons.Default.SportsHockey
            "HAND" -> Icons.Default.SportsHandball
            "SNOO" -> Icons.Default.TableRestaurant
            "FUTS" -> Icons.Default.SportsSoccer
            "DART" -> Icons.Default.BlurCircular
            else -> Icons.Default.SportsGymnastics
        }
    }
}