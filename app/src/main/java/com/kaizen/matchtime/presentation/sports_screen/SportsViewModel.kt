package com.kaizen.matchtime.presentation.sports_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizen.matchtime.domain.model.Sport
import com.kaizen.matchtime.domain.repository.SportRepository
import com.kaizen.matchtime.domain.util.DataError
import com.kaizen.matchtime.domain.util.Result
import com.kaizen.matchtime.presentation.mapper.toUI
import com.kaizen.matchtime.presentation.util.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SportsViewModel @Inject constructor(
    private val repository: SportRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<SportEvent>()
    val events: SharedFlow<SportEvent> = _events.asSharedFlow()

    private val expandedStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    private val favoriteFilterStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    private val reloadDataTrigger = MutableSharedFlow<Unit>(replay = 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<SportsUiState> = reloadDataTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
        repository.getSportsWithFavoriteEvents()
    }.flatMapLatest { sportsResult ->
            when (sportsResult) {
                is Result.Error -> {
                    showMessage(sportsResult.error)
                    flowOf(getErrorState())
                }
                is Result.Success -> {
                    combine(
                        tickerFlow(),
                        expandedStates,
                        favoriteFilterStates
                    ) { nowSeconds, expandedMap, favoriteMap ->
                        getDataState(sportsResult.data, expandedMap, favoriteMap, nowSeconds)
                    }
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SportsUiState(isLoading = true)
        )

    private fun getErrorState(): SportsUiState {
        return SportsUiState(
            sports = emptyList(),
            isLoading = false,
            isError = true
        )
    }

    private fun getDataState(
        sports: List<Sport>,
        expandedMap: Map<String, Boolean>,
        favoriteMap: Map<String, Boolean>,
        nowSeconds: Long
    ): SportsUiState {
        return SportsUiState(
            sports = sports.map { it.toUI(nowSeconds, expandedMap, favoriteMap) },
            isLoading = false,
            isError = false
        )
    }

    private fun showMessage(message: DataError) {
        viewModelScope.launch {
            _events.emit(SportEvent.ShowSnackbar(message.asUiText()))
        }
    }

    fun onAction(action: SportAction) {
        when (action) {
            is SportAction.OnEventFavoriteClick -> onEventFavoriteClick(action.eventId, action.isNowFavorite)
            is SportAction.OnToggleFilterFavoriteEvents -> onToggleFilterFavoriteEvents(action.sportId)
            is SportAction.OnToggleExpand -> onToggleExpand(action.sportId)
            SportAction.Refresh -> {
                reloadData()
            }
        }
    }

    private fun reloadData() {
        viewModelScope.launch {
            reloadDataTrigger.emit(Unit)
        }
    }

    private fun onToggleFilterFavoriteEvents(sportId: String) {
        favoriteFilterStates.update { current ->
            current.toMutableMap().apply {
                this[sportId] = !(this[sportId] ?: false)
            }
        }
    }

    private fun onToggleExpand(sportId: String) {
        expandedStates.update { current ->
            current.toMutableMap().apply {
                this[sportId] = !(this[sportId] ?: false)
            }
        }
    }

    private fun onEventFavoriteClick(eventId: String, isNowFavorite: Boolean) {
        viewModelScope.launch {
            repository.setFavoriteEvent(eventId, isNowFavorite)
        }
    }

    private fun tickerFlow(intervalMs: Long = 1000L): Flow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis() / 1000) // Current time in seconds
            delay(intervalMs)
        }
    }

}