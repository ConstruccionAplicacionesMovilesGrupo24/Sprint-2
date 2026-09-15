package com.campusmeal.android.feature.inventory.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** One item of the last successful BQ2 result. [priorityIndex] restores the backend's order. */
@Entity(
    tableName = "expiring_inventory_items",
    indices = [Index(value = ["priorityIndex"], unique = true)],
)
data class ExpiringInventoryEntity(
    @PrimaryKey val itemId: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val expirationDate: String,
    val remainingDays: Int,
    val priorityIndex: Int,
)
