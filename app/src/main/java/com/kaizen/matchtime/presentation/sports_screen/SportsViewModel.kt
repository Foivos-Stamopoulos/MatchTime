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
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SportsViewModel @Inject constructor(
    private val repository: SportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SportsUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SportEvent>()
    val events: SharedFlow<SportEvent> = _events.asSharedFlow()

    private val expandedStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    init {
        loadSports()
    }

    private fun loadSports() {
        viewModelScope.launch {
            repository.getSportsWithFavoriteEvents()
                .onStart {
                    _uiState.update { it.copy(isLoading = true, isError = false) }
                }.collect { result ->
                    when (result) {
                        is Result.Error -> onError(result.error)
                        is Result.Success -> onDataLoaded(result.data)
                    }
                }
        }
    }

    private fun onDataLoaded(sports: List<Sport>) {
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                isError = false,
                sports = sports.map { it.toUI(23452345) }
            )
        }
    }

    private fun onError(message: DataError) {
        _uiState.update { state ->
            state.copy(
                isLoading = false,
                isError = true
            )
        }
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
                loadSports()
            }
        }
    }

}