package com.kaizen.matchtime.presentation.sports_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaizen.matchtime.R
import com.kaizen.matchtime.presentation.design_system.Gray
import com.kaizen.matchtime.presentation.design_system.MatchTimeTheme
import com.kaizen.matchtime.presentation.design_system.Red
import com.kaizen.matchtime.presentation.sports_screen.SportAction

@Composable
fun ErrorUI(
    paddingValues: PaddingValues,
    onAction: (SportAction) -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Gray)
            .padding(paddingValues),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = "Error",
                tint = Red,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = stringResource(R.string.error_can_not_connect),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onAction(SportAction.Refresh)
                }
            ) {
                Text(
                    text = stringResource(R.string.button_refresh)
                )
            }
        }
    }
}

@Preview
@Composable
fun ErrorUiPreview() {
    MatchTimeTheme {
        ErrorUI(
            paddingValues = PaddingValues(16.dp),
            onAction = {}
        )
    }
}