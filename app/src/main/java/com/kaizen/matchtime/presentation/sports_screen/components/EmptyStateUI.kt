package com.kaizen.matchtime.presentation.sports_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kaizen.matchtime.R
import com.kaizen.matchtime.presentation.design_system.Gray

@Composable
fun EmptyStateUI() {
    Box(
        modifier = Modifier.fillMaxSize().background(Gray),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.label_no_sports),
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview
@Composable
fun EmptyStateUiPreview() {
    MaterialTheme {
        EmptyStateUI()
    }
}