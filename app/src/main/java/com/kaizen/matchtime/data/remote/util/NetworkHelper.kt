package com.kaizen.matchtime.data.remote.util

import com.kaizen.matchtime.domain.util.DataError
import com.kaizen.matchtime.domain.util.Result
import retrofit2.HttpException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException

object NetworkHelper {

    suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T, DataError.NetworkError> {
        try {
            val response = apiCall.invoke()
            return Result.Success(response)
        } catch (e: Exception) {
            return exceptionToErrorResult(e)
        }
    }

    private fun exceptionToErrorResult(e: Throwable): Result<Nothing, DataError.NetworkError> {
        return when (e) {
            is UnknownHostException -> {
                Result.Error(DataError.NetworkError.NO_INTERNET)
            }
            is HttpException -> {
                httpExceptionToErrorResult(e)
            }
            else -> {
                Result.Error(DataError.NetworkError.UNKNOWN)
            }
        }
    }

    /**
     * Covers a few status code cases just for the demo purposes
     */
    private fun httpExceptionToErrorResult(
        e: HttpException,
    ): Result<Nothing, DataError.NetworkError> {
        val statusCode = e.code()
        return when(statusCode) {
            in 500..599 -> Result.Error(DataError.NetworkError.SERVER_ERROR)
            else -> Result.Error(DataError.NetworkError.UNKNOWN)
        }
    }

}