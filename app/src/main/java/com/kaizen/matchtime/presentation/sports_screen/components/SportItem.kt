package com.kaizen.matchtime.presentation.sports_screen.components

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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.kaizen.matchtime.presentation.design_system.Gray
import com.kaizen.matchtime.presentation.design_system.MatchTimeTheme
import com.kaizen.matchtime.presentation.model.SportUI
import com.kaizen.matchtime.presentation.sports_screen.SportAction
import com.kaizen.matchtime.presentation.sports_screen.preview.SportProvider

@Composable
fun SportItem(
    sport: SportUI,
    onAction: (SportAction) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = sport.icon,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = sport.name,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {

                Switch(
                    checked = sport.showOnlyFavorites,
                    onCheckedChange = {
                        onAction(SportAction.OnToggleFilterFavoriteEvents(sport.id, sport.showOnlyFavorites))
                                      },
                    thumbContent = {
                        Icon(
                            imageVector = Icons.Default.StarRate,
                            contentDescription = null,
                            tint = if (sport.showOnlyFavorites) Color.Blue else Color.Gray
                        )
                    }
                )

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            }
        }

        if (sport.isExpanded) {
            FlowRow(
                modifier = Modifier.background(Gray).fillMaxWidth()
            ) {
                sport.events.forEach { event ->
                    EventGridItem(
                        event,
                        {}
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SportItemPreview(@PreviewParameter(SportProvider::class) sports: List<SportUI>) {
    MatchTimeTheme {
        SportItem(
            sport = sports.first(),
            onAction = {}
        )
    }
}