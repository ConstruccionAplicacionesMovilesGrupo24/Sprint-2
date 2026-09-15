package com.campusmeal.android.feature.inventory.data.remote

import kotlinx.serialization.Serializable

/**
 * Provisional NestJS contract for `GET inventory/expiring`. Every field is required, so a missing
 * field fails decoding; unknown fields are ignored by the shared Json configuration.
 * Keep these types out of the UI: map them with the inventory mappers.
 */
@Serializable
data class ExpiringInventoryResponseDto(
    val items: List<InventoryItemDto>,
)

@Serializable
data class InventoryItemDto(
    val id: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val expirationDate: String,
    val remainingDays: Int,
    val active: Boolean,
)
