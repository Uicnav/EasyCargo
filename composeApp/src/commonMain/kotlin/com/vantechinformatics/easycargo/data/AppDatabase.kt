package com.vantechinformatics.easycargo.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.vantechinformatics.easycargo.data.dao.ParcelDao
import com.vantechinformatics.easycargo.data.dao.RouteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Created by ionvaranita on 20/11/17.
 */
@Database(
    entities = [RouteEntity::class, ParcelEntity::class],
    version = 103,
    exportSchema = false
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase(), DB {
    //PLAYERS 2
    abstract fun routeDao(): RouteDao
    abstract fun parcelDao(): ParcelDao

    override fun clearAllTables() {}
}

interface DB {
    fun clearAllTables() {}
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

// 102 → 103: adaugă coloana `type` (păstrează coletele existente).
private val MIGRATION_102_103 = object : Migration(102, 103) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE parcels ADD COLUMN type TEXT NOT NULL DEFAULT ''")
    }
}

fun getRoomDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase {
    val database = builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .addMigrations(MIGRATION_102_103)
        // Fallback rămâne ON pentru downgrade-uri sau schimbări viitoare neașteptate,
        // dar migrarea de mai sus e încercată mai întâi pentru 102→103.
        .fallbackToDestructiveMigration(true)
        .build()
    return database
}


