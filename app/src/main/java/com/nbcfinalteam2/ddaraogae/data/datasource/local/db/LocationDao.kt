package com.nbcfinalteam2.ddaraogae.data.datasource.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(locationRoomEntity: LocationRoomEntity)

    @Query("SELECT * FROM locationroomentity")
    suspend fun getAllLocations(): List<LocationRoomEntity>

    @Query("SELECT * FROM locationroomentity ORDER BY `order` DESC LIMIT 1")
    fun getLatestLocation(): Flow<LocationRoomEntity?>

    @Query("DELETE FROM locationroomentity")
    suspend fun deleteAllLocations()

}