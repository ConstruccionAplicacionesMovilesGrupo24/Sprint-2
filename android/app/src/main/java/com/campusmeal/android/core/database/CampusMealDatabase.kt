package com.campusmeal.android.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Local cache database. It holds no seed data; feature entities are added with their features.
 * Tokens and other secrets must never be stored here.
 */
@Database(
    entities = [CacheMetadataEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class CampusMealDatabase : RoomDatabase() {

    abstract fun cacheMetadataDao(): CacheMetadataDao

    companion object {
        const val NAME = "campusmeal.db"

        fun create(context: Context): CampusMealDatabase =
            Room.databaseBuilder(context.applicationContext, CampusMealDatabase::class.java, NAME).build()
    }
}
