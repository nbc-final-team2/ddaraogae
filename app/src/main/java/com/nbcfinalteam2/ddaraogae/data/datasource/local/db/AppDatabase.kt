package com.nbcfinalteam2.ddaraogae.data.datasource.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocationRoomEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun locationDao(): LocationDao
}