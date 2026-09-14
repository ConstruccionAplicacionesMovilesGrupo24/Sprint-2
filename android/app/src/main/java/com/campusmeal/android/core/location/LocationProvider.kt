package com.campusmeal.android.core.location

/** Foreground location boundary. Background location is out of scope for CampusMeal. */
interface LocationProvider {

    fun hasLocationPermission(): Boolean

    suspend fun currentLocation(): LocationResult
}

sealed interface LocationResult {

    data class Available(
        val latitude: Double,
        val longitude: Double,
        val accuracyMeters: Float?,
    ) : LocationResult

    data object PermissionDenied : LocationResult

    /** Location services are disabled or no fix could be obtained. */
    data object Unavailable : LocationResult
}
