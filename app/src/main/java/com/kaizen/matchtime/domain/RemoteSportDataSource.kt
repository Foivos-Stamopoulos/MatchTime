package com.kaizen.matchtime.domain

import com.kaizen.matchtime.data.remote.dto.SportDto
import com.kaizen.matchtime.domain.util.NetworkError
import com.kaizen.matchtime.domain.util.Result

interface RemoteSportDataSource {

    suspend fun fetchSports(): Result<List<SportDto>, NetworkError>

}