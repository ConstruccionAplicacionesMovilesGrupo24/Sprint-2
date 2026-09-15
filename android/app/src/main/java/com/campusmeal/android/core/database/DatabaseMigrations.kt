package com.campusmeal.android.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Explicit Room migrations. Destructive migration is never used, so cached data survives upgrades. */
object DatabaseMigrations {

    /** v1 → v2: adds the BQ2 expiring-inventory cache. Existing `cache_metadata` rows are kept. */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `expiring_inventory_items` (" +
                    "`itemId` TEXT NOT NULL, `name` TEXT NOT NULL, `quantity` REAL NOT NULL, " +
                    "`unit` TEXT NOT NULL, `expirationDate` TEXT NOT NULL, `remainingDays` INTEGER NOT NULL, " +
                    "`priorityIndex` INTEGER NOT NULL, PRIMARY KEY(`itemId`))",
            )
            db.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS `index_expiring_inventory_items_priorityIndex` " +
                    "ON `expiring_inventory_items` (`priorityIndex`)",
            )
        }
    }

    val ALL: Array<Migration> = arrayOf(MIGRATION_1_2)
}
