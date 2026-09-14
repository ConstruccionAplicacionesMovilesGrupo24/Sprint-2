package com.campusmeal.android.core.common

/** Screen state shared by all CampusMeal features. */
sealed interface UiState<out T> {

    /** Nothing has been requested yet. */
    data object Initial : UiState<Nothing>

    data object Loading : UiState<Nothing>

    data class Content<T>(val data: T) : UiState<T>

    /** The request succeeded but there is nothing to show. */
    data object Empty : UiState<Nothing>

    /** The network is unavailable; [data] comes from the local cache. */
    data class OfflineWithCache<T>(
        val data: T,
        val lastUpdatedEpochMillis: Long?,
    ) : UiState<T>

    data class Error(
        val message: String? = null,
        val cause: Throwable? = null,
    ) : UiState<Nothing>

    /** The session is missing or expired and the user must authenticate again. */
    data object Unauthorized : UiState<Nothing>

    data class PermissionDenied(val permissions: List<String>) : UiState<Nothing>
}
