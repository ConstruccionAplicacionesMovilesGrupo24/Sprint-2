package com.campusmeal.android.feature.inventory.domain.model

/** BQ2 window: items expiring within the next three days (0 = today). */
const val EXPIRING_WITHIN_DAYS = 3

enum class InventoryDataSource { NETWORK, CACHE }

sealed interface ExpiringInventoryResult {

    /**
     * Items in the priority order set by the backend. An empty list is a valid answer.
     *
     * [lastSyncedAtEpochMillis] is when the list was fetched: the current call for
     * [InventoryDataSource.NETWORK], or the last successful sync for [InventoryDataSource.CACHE].
     */
    data class Success(
        val items: List<InventoryItem>,
        val source: InventoryDataSource,
        val lastSyncedAtEpochMillis: Long,
    ) : ExpiringInventoryResult

    /** There is no session, or the backend answered HTTP 401. Cached data is never returned. */
    data object Unauthorized : ExpiringInventoryResult

    data class Failure(val error: InventoryError) : ExpiringInventoryResult
}

sealed interface InventoryError {

    /** Connectivity failure or HTTP 5xx, and no successful sync was ever cached. Null [httpCode] means no HTTP response. */
    data class BackendUnavailable(val httpCode: Int?) : InventoryError

    /** Any other non-2xx status except 401. The response body is not kept. */
    data class Http(val code: Int) : InventoryError

    /** The response broke the contract: a required field was missing or invalid. */
    data object InvalidResponse : InventoryError
}
