package com.kaizen.matchtime.presentation.util

import com.kaizen.matchtime.R
import com.kaizen.matchtime.domain.util.DataError

fun DataError.asUiText(): UiText {
    return when (this) {
        DataError.NetworkError.NO_INTERNET -> {
            UiText.StringResource(R.string.error_no_internet)
        }
        DataError.NetworkError.SERVER_ERROR -> {
            UiText.StringResource(R.string.error_server)
        }
        DataError.NetworkError.UNKNOWN -> {
            UiText.StringResource(R.string.error_unknown)
        }
    }
}