package com.campusmeal.android.core.network

/**
 * Network settings supplied by the build (see android/README.md). Business logic must not
 * hardcode backend URLs. The app only talks to the CampusMeal API; external providers such as
 * route services are reached exclusively through that API.
 */
data class NetworkConfig(
    val baseUrl: String,
    val httpLoggingEnabled: Boolean,
    val connectTimeoutSeconds: Long = 15,
    val readTimeoutSeconds: Long = 30,
) {
    init {
        require(baseUrl.isNotBlank()) { "CampusMeal API base URL is not configured. See android/README.md." }
        require(baseUrl.endsWith("/")) { "CampusMeal API base URL must end with '/'." }
    }
}
