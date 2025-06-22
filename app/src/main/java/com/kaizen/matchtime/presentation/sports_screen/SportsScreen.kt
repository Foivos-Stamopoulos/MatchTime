package com.kaizen.matchtime.presentation.sports_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.kaizen.matchtime.presentation.model.SportUI
import com.kaizen.matchtime.presentation.sports_screen.components.EmptyStateUI
import com.kaizen.matchtime.presentation.sports_screen.components.ErrorUI
import com.kaizen.matchtime.presentation.sports_screen.components.SportItem
import com.kaizen.matchtime.presentation.sports_screen.preview.SportProvider

@Composable
fun SportsScreenRoot(
    viewModel: SportsViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SportEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message.asString(context))
            }
        }
    }

    val state by viewModel.uiState.collectAsState()

    SportsScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportsScreen(
    state: SportsUiState,
    snackbarHostState: SnackbarHostState,
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize().background(Gray),
                    contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.isError -> {
                ErrorUI(
                    paddingValues = paddingValues,
                    onAction = onAction
                )
            }

            else -> {
                SportsList(
                    paddingValues = paddingValues,
                    state = state,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
fun SportsList(
    paddingValues: PaddingValues,
    state: SportsUiState,
    onAction: (SportAction) -> Unit
) {
    if (state.sports.isEmpty()) {
        EmptyStateUI()
    } else {
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize().background(Gray)
        ) {
            itemsIndexed(
                items = state.sports,
                key = { _, item ->
                    item.id
                }) { index, item ->
                if (index == 0) {
                    Spacer(
                        modifier = Modifier.fillMaxWidth()
                            .height(8.dp)
                            .background(Gray))
                }
                SportItem(
                    sport = item,
                    onAction = onAction
                )
                Spacer(modifier = Modifier.height(16.dp).fillMaxWidth().background(Gray))

            }
        }
    }
}

@Preview
@Composable
fun SportsScreenPreview(@PreviewParameter(SportProvider::class) sports: List<SportUI>) {
    MatchTimeTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        SportsScreen(
            state = SportsUiState(
                isLoading = false,
                sports = sports
            ),
            snackbarHostState = snackbarHostState,
            onAction = {}
        )
    }
}

@Preview
@Composable
fun SportsScreenLoadingPreview() {
    MatchTimeTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        SportsScreen(
            state = SportsUiState(
                isLoading = true,
                sports = emptyList()
            ),
            snackbarHostState = snackbarHostState,
            onAction = {}
        )
    }
}

@Preview
@Composable
fun SportsScreenErrorPreview() {
    MatchTimeTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        SportsScreen(
            state = SportsUiState(
                isLoading = false,
                isError = true,
                sports = emptyList()
            ),
            snackbarHostState = snackbarHostState,
            onAction = {}
        )
    }
}

@Preview
@Composable
fun NoSportsScreenPreview() {
    MatchTimeTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        SportsScreen(
            state = SportsUiState(
                isLoading = false,
                isError = false,
                sports = emptyList()
            ),
            snackbarHostState = snackbarHostState,
            onAction = {}
        )
    }
}