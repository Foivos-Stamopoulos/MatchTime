package com.kaizen.matchtime.presentation.sports_screen.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaizen.matchtime.R
import com.kaizen.matchtime.presentation.design_system.Blue
import com.kaizen.matchtime.presentation.design_system.Gray
import com.kaizen.matchtime.presentation.design_system.LightGray

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    colors: CustomSwitchColors = CustomSwitchColors(),
    thumbContent: @Composable BoxScope.() -> Unit = {}
) {
    val trackWidth = 46.dp
    val trackHeight = 18.dp
    val thumbSize = 28.dp

    val transition = updateTransition(targetState = checked, label = "switch_transition")

    val thumbOffsetX by transition.animateDp(label = "thumb_offset") { isChecked ->
        if (isChecked) trackWidth - thumbSize else 0.dp
    }

    val trackColor by transition.animateColor(label = "track_color") { isChecked ->
        if (isChecked) colors.checkedTrackColor else colors.uncheckedTrackColor
    }

    Box(
        modifier = modifier
            .width(trackWidth)
            .height(thumbSize)
            .clickable(
                role = Role.Switch,
                onClick = { onCheckedChange(!checked) }
            )
            .semantics {
                this.role = Role.Switch
                this.stateDescription = if (checked) "On" else "Off"
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(trackWidth)
                .height(trackHeight)
                .clip(RoundedCornerShape(50))
                .background(trackColor)
        )

        Box(
            modifier = Modifier
                .size(thumbSize)
                .offset(x = thumbOffsetX)
                .clip(CircleShape)
                .background(if (checked) colors.checkedThumbColor else colors.uncheckedThumbColor)
        ) {
            thumbContent()
        }
    }
}

data class CustomSwitchColors(
    val checkedTrackColor: Color = Gray,
    val uncheckedTrackColor: Color = LightGray,
    val checkedThumbColor: Color = Blue,
    val uncheckedThumbColor: Color = LightGray
)

@Preview
@Composable
fun CustomSwitchCheckedPreview() {
    CustomSwitch(
        checked = true,
        onCheckedChange = {},
        thumbContent = {
            Icon(
                modifier = Modifier.padding(4.dp),
                imageVector = Icons.Default.StarRate,
                contentDescription = stringResource(R.string.content_description_favorite_switch),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    )
}

@Preview
@Composable
fun CustomSwitchUnCheckedPreview() {
    CustomSwitch(
        checked = false,
        onCheckedChange = {},
        thumbContent = {
            Icon(
                modifier = Modifier.padding(4.dp),
                imageVector = Icons.Default.StarRate,
                contentDescription = stringResource(R.string.content_description_favorite_switch),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    )
}