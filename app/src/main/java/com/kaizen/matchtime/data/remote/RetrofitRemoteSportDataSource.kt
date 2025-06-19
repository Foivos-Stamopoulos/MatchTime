package com.kaizen.matchtime.data.remote

import com.kaizen.matchtime.data.remote.api.SportApi
import com.kaizen.matchtime.data.remote.dto.SportDto
import com.kaizen.matchtime.data.remote.util.NetworkHelper
import com.kaizen.matchtime.domain.RemoteSportDataSource
import com.kaizen.matchtime.domain.util.DataError
import com.kaizen.matchtime.domain.util.Result
import javax.inject.Inject

class RetrofitRemoteSportDataSource @Inject constructor(
    private val api: SportApi
) : RemoteSportDataSource {

    override suspend fun fetchSports(): Result<List<SportDto>, DataError.NetworkError> {
        return NetworkHelper.safeApiCall { api.fetchSports() }
    }

}