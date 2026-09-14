package com.campusmeal.android.core.session

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** Exposes session state to features without giving them direct access to raw tokens. */
class SessionRepository(private val storage: SessionStorage) {

    val isAuthenticated: Flow<Boolean> = storage.session
        .map { it != null }
        .distinctUntilChanged()

    suspend fun signOut() {
        storage.clear()
    }
}
