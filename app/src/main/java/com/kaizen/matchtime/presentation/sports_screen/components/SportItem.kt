package com.kaizen.matchtime.presentation.sports_screen.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.kaizen.matchtime.R
import com.kaizen.matchtime.presentation.design_system.MatchTimeTheme
import com.kaizen.matchtime.presentation.model.SportUI
import com.kaizen.matchtime.presentation.sports_screen.SportAction
import com.kaizen.matchtime.presentation.sports_screen.preview.SportProvider
import com.kaizen.matchtime.presentation.util.TestTags

@Composable
fun SportItem(
    modifier: Modifier = Modifier,
    sport: SportUI,
    onAction: (SportAction) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = sport.icon,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = sport.name,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomSwitch(
                    modifier = Modifier.testTag("${TestTags.SWITCH}_${sport.name}"),
                    checked = sport.showOnlyFavorites,
                    onCheckedChange = {
                        onAction(SportAction.OnToggleFilterFavoriteEvents(sport.id))
                    },
                    thumbContent = {
                        Icon(
                            modifier = Modifier.padding(4.dp),
                            imageVector = Icons.Default.StarRate,
                            contentDescription = stringResource(R.string.content_description_favorite_switch),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                )

                IconButton(onClick = {
                    onAction(SportAction.OnToggleExpand(sport.id))
                }) {
                    Icon(
                        modifier = Modifier.testTag("${TestTags.EXPAND_COLLAPSE_BUTTON}_${sport.name}"),
                        imageVector = if (sport.isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (sport.isExpanded) stringResource(R.string.content_description_collapse_sport) else stringResource(R.string.content_description_expand_sport),
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }

        if (sport.isExpanded) {
            val spacing = 8.dp
            val horizontalPadding = 12.dp
            val itemsPerRow = 4
            val configuration = LocalConfiguration.current
            val screenWidthDp = configuration.screenWidthDp.dp - horizontalPadding.times(2)

            val itemSize = remember(screenWidthDp) {
                val totalSpacing = spacing * 3
                (screenWidthDp - totalSpacing) / itemsPerRow
            }

            if (sport.events.isEmpty()) {
                val text = if (sport.showOnlyFavorites) stringResource(R.string.label_no_favorite_events) else stringResource(R.string.label_no_events)
                Text(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    text = text,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            } else {
                FlowRow(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding, vertical = 8.dp),
                    maxItemsInEachRow = 4,
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    sport.events.forEach { event ->
                        EventGridItem(
                            modifier = Modifier.width(itemSize),
                            event = event,
                            onAction = onAction
                        )
                    }
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light mode")
@Composable
fun SportItemPreview(@PreviewParameter(SportProvider::class) sports: List<SportUI>) {
    MatchTimeTheme {
        SportItem(
            modifier = Modifier,
            sport = sports.first(),
            onAction = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light mode")
@Composable
fun SportItemFilterFavoritesEnabledPreview(@PreviewParameter(SportProvider::class) sports: List<SportUI>) {
    val sport = sports.first()
    val event = sport.events.first().copy(isFavorite = true)
    MatchTimeTheme {
        SportItem(
            modifier = Modifier,
            sport = sport.copy(showOnlyFavorites = true, events = listOf(event)),
            onAction = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light mode")
@Composable
fun SportItemNoFavoritesPreview(@PreviewParameter(SportProvider::class) sports: List<SportUI>) {
    val sport = sports.first()
    MatchTimeTheme {
        SportItem(
            modifier = Modifier,
            sport = sport.copy(showOnlyFavorites = true, events = emptyList()),
            onAction = {}
        )
    }
}