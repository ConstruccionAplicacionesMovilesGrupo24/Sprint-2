package com.campusmeal.android.core.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class FusedLocationProvider(context: Context) : LocationProvider {

    private val appContext = context.applicationContext
    private val client = LocationServices.getFusedLocationProviderClient(appContext)

    override fun hasLocationPermission(): Boolean =
        LOCATION_PERMISSIONS.any { permission ->
            ContextCompat.checkSelfPermission(appContext, permission) == PackageManager.PERMISSION_GRANTED
        }

    @SuppressLint("MissingPermission") // Guarded by hasLocationPermission() and the SecurityException catch.
    override suspend fun currentLocation(): LocationResult {
        if (!hasLocationPermission()) return LocationResult.PermissionDenied

        val cancellation = CancellationTokenSource()
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation { cancellation.cancel() }
            try {
                client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancellation.token)
                    .addOnSuccessListener { location ->
                        val result = location?.let {
                            LocationResult.Available(
                                latitude = it.latitude,
                                longitude = it.longitude,
                                accuracyMeters = if (it.hasAccuracy()) it.accuracy else null,
                            )
                        } ?: LocationResult.Unavailable
                        continuation.resume(result)
                    }
                    .addOnFailureListener { continuation.resume(LocationResult.Unavailable) }
                    .addOnCanceledListener { continuation.cancel() }
            } catch (_: SecurityException) {
                // Permission was revoked between the check and the request.
                continuation.resume(LocationResult.PermissionDenied)
            }
        }
    }

    companion object {
        val LOCATION_PERMISSIONS = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }
}
