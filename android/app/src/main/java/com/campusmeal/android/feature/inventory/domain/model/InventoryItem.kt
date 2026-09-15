package com.campusmeal.android.feature.inventory.domain.model

/**
 * An inventory item that answers BQ2, independent of Retrofit and Room.
 *
 * [expirationDate] is a validated ISO `YYYY-MM-DD` string: `java.time` needs API 26 and core library
 * desugaring is not configured for minSdk 24. [remainingDays] is computed by the backend; 0 means the
 * item expires today.
 */
data class InventoryItem(
    val id: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val expirationDate: String,
    val remainingDays: Int,
)
