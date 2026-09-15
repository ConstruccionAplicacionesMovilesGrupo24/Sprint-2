package com.campusmeal.android.feature.inventory.domain.repository

import com.campusmeal.android.feature.inventory.domain.model.EXPIRING_WITHIN_DAYS
import com.campusmeal.android.feature.inventory.domain.model.ExpiringInventoryResult

interface InventoryRepository {

    /**
     * Answers BQ2 for the current user: items expiring within [EXPIRING_WITHIN_DAYS] days, in the
     * backend's priority order.
     */
    suspend fun getExpiringInventory(): ExpiringInventoryResult

    /**
     * Deletes the cached BQ2 result. The cache is not scoped per user, so the shared session
     * mechanism must call this on sign-out.
     */
    suspend fun clearCache()
}
