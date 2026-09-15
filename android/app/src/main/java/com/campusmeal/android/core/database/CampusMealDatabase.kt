package com.campusmeal.android.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.campusmeal.android.feature.inventory.data.local.ExpiringInventoryDao
import com.campusmeal.android.feature.inventory.data.local.ExpiringInventoryEntity

/**
 * Local cache database. It holds no seed data; feature entities are added with their features.
 * Tokens and other secrets must never be stored here.
 *
 * Every version bump needs an explicit migration in [DatabaseMigrations] and a committed schema file.
 */
@Database(
    entities = [CacheMetadataEntity::class, ExpiringInventoryEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class CampusMealDatabase : RoomDatabase() {

    abstract fun cacheMetadataDao(): CacheMetadataDao

    abstract fun expiringInventoryDao(): ExpiringInventoryDao

    companion object {
        const val NAME = "campusmeal.db"

        fun create(context: Context): CampusMealDatabase =
            Room.databaseBuilder(context.applicationContext, CampusMealDatabase::class.java, NAME)
                .addMigrations(*DatabaseMigrations.ALL)
                .build()
    }
}
