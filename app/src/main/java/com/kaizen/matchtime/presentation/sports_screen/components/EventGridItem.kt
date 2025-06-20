package com.kaizen.matchtime.presentation.sports_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaizen.matchtime.R
import com.kaizen.matchtime.presentation.design_system.Blue
import com.kaizen.matchtime.presentation.design_system.MatchTimeTheme
import com.kaizen.matchtime.presentation.design_system.Red
import com.kaizen.matchtime.presentation.design_system.Yellow
import com.kaizen.matchtime.presentation.model.EventUI
import com.kaizen.matchtime.presentation.sports_screen.SportAction
import com.kaizen.matchtime.presentation.util.UiText

@Composable
fun EventGridItem(
    event: EventUI,
    onAction: (SportAction) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .background(Color(0xFF3A3A3A))
            .padding(4.dp)
    ) {
        Text(
            modifier = Modifier
                .border(width = 1.dp, color = Blue, RoundedCornerShape(4.dp))
                .padding(4.dp),
            text = event.countdown.asString(),
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        Icon(
            imageVector = if (event.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
            contentDescription = stringResource(R.string.content_description_favorite),
            tint = Yellow,
            modifier = Modifier.size(24.dp).clickable {
                onAction(SportAction.OnEventFavoriteClick(event.id, !event.isFavorite))
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = event.competitor1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = stringResource(R.string.label_versus),
            color = Red,
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = event.competitor2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(fontScale = 1f)
@Preview(fontScale = 1.5f)
@Composable
fun EventGridFavoriteItemPreview() {
    MatchTimeTheme {
        EventGridItem(
            event = EventUI(
                "1",
                "123",
                "AEK",
                "Olympiakos",
                true,
                UiText.DynamicString("03:25:00")
            ),
            {}
        )
    }
}

@Preview
@Composable
fun EventGridUnFavoriteItemPreview() {
    MatchTimeTheme {
        EventGridItem(
            event = EventUI(
                "22911144",
                "FOOT",
                "Erithros Asteras Tripolis",
                "Olympiakos",
                false,
                UiText.DynamicString("03:25:00")
            ),
            onAction = {}
        )
    }
}