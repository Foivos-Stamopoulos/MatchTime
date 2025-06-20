package com.kaizen.matchtime.presentation.sports_screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SportsViewModel @Inject constructor() : ViewModel() {


    fun onAction(action: SportAction) {
        when (action) {
            is SportAction.OnEventFavoriteClick -> TODO()
            is SportAction.OnToggleExpand -> TODO()
            is SportAction.OnToggleFilterFavoriteEvents -> TODO()
        }
    }

}