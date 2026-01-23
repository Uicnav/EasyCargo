package com.vantechinformatics.easycargo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.vantechinformatics.easycargo.data.ParcelEntity
import com.vantechinformatics.easycargo.data.RouteEntity
import com.vantechinformatics.easycargo.data.RouteStats
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    // ... insertRoute rămâne la fel ...
    @Insert
    suspend fun insertRoute(route: RouteEntity): Long

    @Query("SELECT * FROM routes ORDER BY createdAt DESC")
    fun getAllRoutes(): Flow<List<RouteEntity>>


    @Query("UPDATE parcels SET isDelivered = :isDelivered WHERE id = :parcelId")
    suspend fun updateParcelStatus(parcelId: Long, isDelivered: Boolean)

    // 4. INSERARE PROGRESIVĂ (100, 101...)
    @Query("SELECT MAX(displayId) FROM parcels WHERE routeId = :routeId")
    suspend fun getMaxIdForRoute(routeId: Long): Int?

    @Query("DELETE FROM routes WHERE routeId = :routeId")
    suspend fun deleteRouteById(routeId: Long)

    @Transaction
    suspend fun deleteRouteComplete(routeId: Long) {
        deleteRouteById(routeId)
    }
}