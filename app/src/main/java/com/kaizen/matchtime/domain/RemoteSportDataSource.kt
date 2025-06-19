package com.kaizen.matchtime.domain

import com.kaizen.matchtime.data.remote.dto.SportDto
import com.kaizen.matchtime.domain.util.DataError
import com.kaizen.matchtime.domain.util.Result

interface RemoteSportDataSource {

    suspend fun fetchSports(): Result<List<SportDto>, DataError.NetworkError>

}