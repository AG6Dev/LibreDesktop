package dev.ag6.bs_app.repository.readings

import dev.ag6.bs_app.model.connection.ConnectionResponse
import dev.ag6.bs_app.model.reading.GlucoseReading
import dev.ag6.bs_app.model.reading.mapToGlucoseReading
import dev.ag6.bs_app.repository.auth.AuthRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.security.MessageDigest

class ReadingsRepositoryImpl(val httpClient: HttpClient, val authRepository: AuthRepository) : ReadingsRepository {
    companion object {
        private const val CONNECTIONS_URL = "https://api.libreview.io/llu/connections"
    }

    override suspend fun getLastThirtyReadings(): List<GlucoseReading> {
        //get readings from local database
        return listOf()
    }

    override suspend fun getCurrentReading(): GlucoseReading? {
        val userId = authRepository.getUserId() ?: return null
        val token = authRepository.getAuthToken() ?: return null

        val userHash = MessageDigest.getInstance("SHA-256")
            .digest(userId.toByteArray())
            .joinToString("") { "%02x".format(it) }

        val response = httpClient.get(CONNECTIONS_URL) {
            contentType(ContentType.Application.Json)
            bearerAuth(token)
            headers {
                append("Accept-Encoding", "gzip")
                append("product", "llu.android")
                append("version", "4.16.0")
                append("Account-Id", userHash)
            }
        }
        return when (val connection = response.body<ConnectionResponse>()) {
            is ConnectionResponse.Success -> connection.data.first().glucoseItem.mapToGlucoseReading()
            is ConnectionResponse.Error -> null
        }
    }
}