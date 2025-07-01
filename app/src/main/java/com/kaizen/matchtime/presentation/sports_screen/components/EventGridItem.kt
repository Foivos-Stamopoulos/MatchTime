package com.kaizen.matchtime.presentation.sports_screen.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaizen.matchtime.R
import com.kaizen.matchtime.presentation.design_system.LightGray
import com.kaizen.matchtime.presentation.design_system.MatchTimeTheme
import com.kaizen.matchtime.presentation.design_system.Yellow
import com.kaizen.matchtime.presentation.model.EventUI
import com.kaizen.matchtime.presentation.sports_screen.SportAction
import com.kaizen.matchtime.presentation.util.TestTags
import com.kaizen.matchtime.presentation.util.UiText

@Composable
fun EventGridItem(
    modifier: Modifier = Modifier,
    event: EventUI,
    onAction: (SportAction) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(4.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth()
                .border(width = 1.dp, color = MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                .padding(4.dp),
            text = event.countdown.asString(),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Icon(
            imageVector = if (event.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
            contentDescription = if (event.isFavorite) stringResource(R.string.content_description_remove_from_favorites) else stringResource(R.string.content_description_add_to_favorites),
            tint = if (event.isFavorite) Yellow else LightGray,
            modifier = Modifier.testTag("${TestTags.FAVORITE_STAR}_${event.id}")
                .size(24.dp).clickable {
                onAction(SportAction.OnEventFavoriteClick(event.id, !event.isFavorite))
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = event.competitor1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = stringResource(R.string.label_versus),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelSmall
        )

        Text(
            text = event.competitor2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(fontScale = 1f)
@Preview(fontScale = 1.5f)
@Composable
fun EventGridFavoriteItemPreview() {
    MatchTimeTheme {
        EventGridItem(
            modifier = Modifier,
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Light mode")
@Composable
fun EventGridUnFavoriteItemPreview() {
    MatchTimeTheme {
        EventGridItem(
            modifier = Modifier,
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