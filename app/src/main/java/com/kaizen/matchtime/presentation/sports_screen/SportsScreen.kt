package com.kaizen.matchtime.presentation.sports_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaizen.matchtime.R
import com.kaizen.matchtime.presentation.design_system.Blue
import com.kaizen.matchtime.presentation.design_system.Gray
import com.kaizen.matchtime.presentation.design_system.MatchTimeTheme
import com.kaizen.matchtime.presentation.model.EventUI
import com.kaizen.matchtime.presentation.model.SportUI
import com.kaizen.matchtime.presentation.sports_screen.components.SportItem
import com.kaizen.matchtime.presentation.sports_screen.preview.SportProvider
import com.kaizen.matchtime.presentation.util.UiText

@Composable
fun SportsScreenRoot(
    viewModel: SportsViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    SportsScreen(
        sports = listOf(),
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportsScreen(
    sports: List<SportUI>,
    onAction: (SportAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue
                ),
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(
                    modifier = Modifier.fillMaxWidth()
                        .height(8.dp)
                        .background(Gray))
            }
            items(sports) { sport ->
                SportItem(
                    sport = sport,
                    onAction = onAction
                )
            }
        }
    }
}

@Preview
@Composable
fun SportsScreenPreview(@PreviewParameter(SportProvider::class) sports: List<SportUI>) {
    MatchTimeTheme {
        SportsScreen(
            sports = sports,
            onAction = {}
        )
    }
}