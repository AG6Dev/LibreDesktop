package dev.ag6.bs_app.repository.auth

import dev.ag6.bs_app.model.auth.AuthResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun isAuthenticated(): Boolean

    suspend fun getAuthToken(): String?

    suspend fun getUserId(): String?

    fun login(username: String, password: String, countryCode: String = ""): Flow<AuthResponse>
}