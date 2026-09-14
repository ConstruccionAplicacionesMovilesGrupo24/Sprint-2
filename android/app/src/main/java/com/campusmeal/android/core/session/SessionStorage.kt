package com.campusmeal.android.core.session

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SessionTokens(
    val accessToken: String,
    val refreshToken: String,
) {
    // Keeps tokens out of logs, crash reports and debugger string dumps.
    override fun toString(): String = "SessionTokens(accessToken=<redacted>, refreshToken=<redacted>)"
}

/**
 * Storage boundary for authentication tokens.
 *
 * Implementations must never persist tokens in plaintext (SharedPreferences, DataStore, Room or
 * files) without Android Keystore-backed encryption.
 */
interface SessionStorage {
    val session: Flow<SessionTokens?>

    suspend fun save(tokens: SessionTokens)

    suspend fun clear()
}

/**
 * Interim implementation that keeps tokens in process memory only; they are lost when the process
 * dies and are never written to disk.
 *
 * PENDING: replace with an Android Keystore-backed implementation before real login ships.
 */
class InMemorySessionStorage : SessionStorage {

    private val state = MutableStateFlow<SessionTokens?>(null)

    override val session: StateFlow<SessionTokens?> = state.asStateFlow()

    override suspend fun save(tokens: SessionTokens) {
        state.value = tokens
    }

    override suspend fun clear() {
        state.value = null
    }
}
