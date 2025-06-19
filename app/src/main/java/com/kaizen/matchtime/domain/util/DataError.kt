package com.kaizen.matchtime.domain.util

sealed interface DataError: Error {

    enum class NetworkError: DataError {
        NO_INTERNET,
        SERVER_ERROR,
        UNKNOWN
    }

}