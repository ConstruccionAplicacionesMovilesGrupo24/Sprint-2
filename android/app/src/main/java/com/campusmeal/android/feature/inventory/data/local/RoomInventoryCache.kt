package com.campusmeal.android.feature.inventory.data.local

import androidx.room.withTransaction
import com.campusmeal.android.core.database.CacheMetadataEntity
import com.campusmeal.android.core.database.CampusMealDatabase
import com.campusmeal.android.feature.inventory.data.mapper.toDomain
import com.campusmeal.android.feature.inventory.data.mapper.toEntities
import com.campusmeal.android.feature.inventory.domain.model.InventoryItem

/**
 * Room-backed [ExpiringInventoryCache]. Items live in `expiring_inventory_items`; the sync timestamp
 * lives in the shared `cache_metadata` table under [CACHE_KEY]. The metadata row, not the item count,
 * tells whether a sync ever happened, so an empty successful result is still a cached answer.
 */
class RoomInventoryCache(private val database: CampusMealDatabase) : ExpiringInventoryCache {

    private val itemsDao = database.expiringInventoryDao()
    private val metadataDao = database.cacheMetadataDao()

    override suspend fun read(): CachedExpiringInventory? =
        database.withTransaction {
            val metadata = metadataDao.get(CACHE_KEY) ?: return@withTransaction null
            CachedExpiringInventory(
                items = itemsDao.getItems().map { it.toDomain() },
                lastSyncedAtEpochMillis = metadata.lastSyncedAtEpochMillis,
            )
        }

    override suspend fun replace(items: List<InventoryItem>, syncedAtEpochMillis: Long) {
        database.withTransaction {
            itemsDao.replaceItems(items.toEntities())
            metadataDao.upsert(CacheMetadataEntity(cacheKey = CACHE_KEY, lastSyncedAtEpochMillis = syncedAtEpochMillis))
        }
    }

    override suspend fun clear() {
        database.withTransaction {
            itemsDao.deleteItems()
            metadataDao.delete(CACHE_KEY)
        }
    }

    companion object {
        const val CACHE_KEY = "inventory.expiring"
    }
}
