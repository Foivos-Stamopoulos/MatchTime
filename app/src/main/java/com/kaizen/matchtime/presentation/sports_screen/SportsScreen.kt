package com.kaizen.matchtime.presentation.sports_screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SportsScreenRoot(
    modifier: Modifier,
    viewModel: SportsViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    SportsScreen(
        modifier = modifier,
        onAction = viewModel::onAction
    )
}

@Composable
fun SportsScreen(
    modifier: Modifier,
    onAction: (SportAction) -> Unit
) {
    Text(
        text = "Hello user!",
        modifier = modifier
    )
}