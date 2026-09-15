package com.campusmeal.android.core.session

import kotlinx.coroutines.flow.first

/**
 * Supplies the `Authorization` header value for CampusMeal API calls. Features depend on this
 * instead of [SessionStorage], so they never see the refresh token.
 *
 * The returned value contains the access token: never log it or include it in exceptions.
 */
interface AuthorizationHeaderProvider {

    /** Returns `Bearer <access-token>`, or null when there is no usable session. */
    suspend fun getAuthorizationHeader(): String?
}

class SessionAuthorizationHeaderProvider(
    private val sessionStorage: SessionStorage,
) : AuthorizationHeaderProvider {

    override suspend fun getAuthorizationHeader(): String? =
        sessionStorage.session.first()
            ?.accessToken
            ?.takeIf { it.isNotBlank() }
            ?.let { "Bearer $it" }
}
