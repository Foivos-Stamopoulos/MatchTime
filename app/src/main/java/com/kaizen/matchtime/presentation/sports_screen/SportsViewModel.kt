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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SportsViewModel @Inject constructor(
    repository: SportRepository
) : ViewModel() {

    private val _events = MutableSharedFlow<SportEvent>()
    val events: SharedFlow<SportEvent> = _events.asSharedFlow()

    private val expandedStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    val uiState: StateFlow<SportsUiState> = combine(
        repository.getSportsWithFavoriteEvents(),
        expandedStates,
    ) { sportsResult, expandedMap ->

        return@combine when (sportsResult) {
            is Result.Error -> {
                showMessage(sportsResult.error)
                getErrorState()
            }
            is Result.Success -> {
                getDataState(sportsResult.data, expandedMap)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SportsUiState(isLoading = true))

    private fun getErrorState(): SportsUiState {
        return SportsUiState(
            sports = emptyList(),
            isLoading = false,
            isError = true
        )
    }

    private fun getDataState(sports: List<Sport>, expandedMap: Map<String, Boolean>): SportsUiState {
        return SportsUiState(
            sports = sports.map { it.toUI(123, expandedMap) },
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
            is SportAction.OnEventFavoriteClick -> TODO()
            is SportAction.OnToggleFilterFavoriteEvents -> TODO()
            is SportAction.OnToggleExpand -> {
                expandedStates.update { current ->
                    current.toMutableMap().apply {
                        this[action.sportId] = !(this[action.sportId] ?: false)
                    }
                }
            }
            SportAction.Refresh -> {
                //loadSports()
            }
        }
    }

}