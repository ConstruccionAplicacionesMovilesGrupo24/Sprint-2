package com.campusmeal.android.feature.inventory.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class ExpiringInventoryDao {

    @Query("SELECT * FROM expiring_inventory_items ORDER BY priorityIndex ASC")
    abstract suspend fun getItems(): List<ExpiringInventoryEntity>

    @Insert
    abstract suspend fun insertItems(items: List<ExpiringInventoryEntity>)

    @Query("DELETE FROM expiring_inventory_items")
    abstract suspend fun deleteItems()

    /** Replaces the previous result atomically. An empty list leaves the table empty. */
    @Transaction
    open suspend fun replaceItems(items: List<ExpiringInventoryEntity>) {
        deleteItems()
        insertItems(items)
    }
}
