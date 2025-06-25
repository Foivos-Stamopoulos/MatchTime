package com.kaizen.matchtime.presentation.sports_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaizen.matchtime.R
import com.kaizen.matchtime.app.di.IoDispatcher
import com.kaizen.matchtime.domain.model.Sport
import com.kaizen.matchtime.domain.repository.SportRepository
import com.kaizen.matchtime.domain.use_case.FilterEventsUseCase
import com.kaizen.matchtime.domain.util.DataError
import com.kaizen.matchtime.domain.util.Result
import com.kaizen.matchtime.presentation.mapper.toUI
import com.kaizen.matchtime.presentation.util.UiText
import com.kaizen.matchtime.presentation.util.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SportsViewModel @Inject constructor(
    private val repository: SportRepository,
    private val filterEventsUseCase: FilterEventsUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _events = MutableSharedFlow<SportEvent>()
    val events: SharedFlow<SportEvent> = _events.asSharedFlow()

    private val expandedStates = MutableStateFlow(savedStateHandle.get<HashMap<String, Boolean>>(KEY_EXPANDED_MAP)?.toMap() ?: emptyMap())
    private val favoriteFilterStates = MutableStateFlow(savedStateHandle.get<HashMap<String, Boolean>>(KEY_FAVORITE_MAP)?.toMap() ?: emptyMap())
    private val reloadDataTrigger = MutableSharedFlow<Unit>(replay = 0)

    private val _now = MutableStateFlow(System.currentTimeMillis() / 1000)
    private val now: StateFlow<Long> = _now.asStateFlow()

    companion object {
        private const val KEY_EXPANDED_MAP= "expanded_map"
        private const val KEY_FAVORITE_MAP= "favorite_map"
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<SportsUiState> = reloadDataTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
            repository.getSportsWithFavoriteEvents()
                .flatMapLatest { sportsResult ->
                    when (sportsResult) {
                        is Result.Error -> {
                            showMessage(sportsResult.error)
                            flowOf(getErrorState())
                        }
                        is Result.Success -> {
                            combine(
                                now,
                                expandedStates,
                                favoriteFilterStates
                            ) { nowSeconds, expandedMap, favoriteMap ->
                                getDataState(sportsResult.data, expandedMap, favoriteMap, nowSeconds)
                            }
                        }
                    }
                }
                .onStart { emit(SportsUiState(isLoading = true)) }
                .catch { throwable ->
                    Timber.d("In flow exception: $throwable")
                    emit(SportsUiState(isError = true))
                    _events.emit(SportEvent.ShowSnackbar(UiText.StringResource(R.string.error_unknown)))
                }
        }
        .flowOn(ioDispatcher)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SportsUiState()
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
            sports = sports.map { sport ->
                sport.toUI(
                    nowSeconds,
                    expandedMap,
                    favoriteMap,
                    filterEventsUseCase(sport, favoriteMap)
                )
            },
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
            }.also {
                savedStateHandle[KEY_FAVORITE_MAP] = HashMap(it)
            }
        }
    }

    private fun onToggleExpand(sportId: String) {
        expandedStates.update { current ->
            current.toMutableMap().apply {
                this[sportId] = !(this[sportId] ?: false)
            }.also {
                savedStateHandle[KEY_EXPANDED_MAP] = HashMap(it)
            }
        }
    }

    private fun onEventFavoriteClick(eventId: String, isNowFavorite: Boolean) {
        viewModelScope.launch {
            repository.setFavoriteEvent(eventId, isNowFavorite)
        }
    }

    fun tickerFlow(intervalMs: Long = 1000L): Flow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis() / 1000) // Current time in seconds
            delay(intervalMs)
        }
    }

    fun updateNow(value: Long) {
        _now.value = value
    }

}