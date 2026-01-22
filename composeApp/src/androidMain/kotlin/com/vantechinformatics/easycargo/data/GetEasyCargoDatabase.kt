package com.vantechinformatics.easycargo.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection

fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("EasyCargoDatabase.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext, name = dbFile.absolutePath
    ).fallbackToDestructiveMigration(true).addCallback(object:RoomDatabase.Callback() {
        override fun onCreate(db: SQLiteConnection) {
            super.onCreate(db)
        }
    })
}