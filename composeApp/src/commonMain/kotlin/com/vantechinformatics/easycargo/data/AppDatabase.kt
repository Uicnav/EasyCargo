package com.vantechinformatics.easycargo.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Created by ionvaranita on 20/11/17.
 */
@Database(
    entities = [RouteEntity::class, ParcelEntity::class],
    version = 99,
    exportSchema = false
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase(), DB {
    //PLAYERS 2
    abstract fun appDao(): AppDao


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

fun getRoomDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase {
    val database = builder.setDriver(BundledSQLiteDriver()).setQueryCoroutineContext(Dispatchers.IO)
        .fallbackToDestructiveMigration(true).build()
    return database
}


