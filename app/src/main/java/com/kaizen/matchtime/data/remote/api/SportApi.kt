package com.kaizen.matchtime.data.remote.api

import com.kaizen.matchtime.data.remote.dto.SportDto
import retrofit2.http.GET

interface SportApi {

    @GET("MockSports/sports.json")
    suspend fun fetchSports(): List<SportDto>

}