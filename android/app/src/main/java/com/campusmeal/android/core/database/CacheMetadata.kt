package com.campusmeal.android.core.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** Records when a cached data set was last synchronized, to support offline-with-cache states. */
@Entity(tableName = "cache_metadata")
data class CacheMetadataEntity(
    @PrimaryKey val cacheKey: String,
    val lastSyncedAtEpochMillis: Long,
)

@Dao
interface CacheMetadataDao {

    @Upsert
    suspend fun upsert(metadata: CacheMetadataEntity)

    @Query("SELECT * FROM cache_metadata WHERE cacheKey = :cacheKey")
    fun observe(cacheKey: String): Flow<CacheMetadataEntity?>

    @Query("SELECT * FROM cache_metadata WHERE cacheKey = :cacheKey")
    suspend fun get(cacheKey: String): CacheMetadataEntity?

    @Query("DELETE FROM cache_metadata WHERE cacheKey = :cacheKey")
    suspend fun delete(cacheKey: String)

    @Query("DELETE FROM cache_metadata")
    suspend fun clear()
}
