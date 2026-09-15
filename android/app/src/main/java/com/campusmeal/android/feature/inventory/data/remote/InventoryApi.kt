package com.campusmeal.android.feature.inventory.data.remote

import com.campusmeal.android.feature.inventory.domain.model.EXPIRING_WITHIN_DAYS
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface InventoryApi {

    /**
     * Returns the current user's items expiring within [withinDays] days, already filtered and
     * ordered by priority by the backend. [authorization] is the full `Bearer` header value.
     * The path is relative because the base URL already ends in `/api/v1/`.
     */
    @GET("inventory/expiring")
    suspend fun getExpiringInventory(
        @Header("Authorization") authorization: String,
        @Query("withinDays") withinDays: Int = EXPIRING_WITHIN_DAYS,
    ): ExpiringInventoryResponseDto
}
