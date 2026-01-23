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
interface ParcelDao {

    // 1. STATISTICI RUTĂ: Numără coletele și adună banii
    @Query(
        """
        SELECT 
            COUNT(*) as totalParcels, 
            COALESCE(SUM(CASE WHEN isDelivered = 1 THEN 1 ELSE 0 END), 0) as deliveredParcels,
            COALESCE(SUM(totalSum), 0) as totalMoney 
        FROM parcels 
        WHERE routeId = :routeId
    """
    )
    fun getRouteStats(routeId: Long): Flow<RouteStats>

    // 2. LISTA: Ordonată descrescător (cel mai nou sus)
    @Query("SELECT * FROM parcels WHERE routeId = :routeId ORDER BY displayId DESC")
    fun getParcelsForRoute(routeId: Long): Flow<List<ParcelEntity>>

    // 3. SCHIMBĂ STATUS: Livrat <-> Nelivrat
    @Query("UPDATE parcels SET isDelivered = :isDelivered WHERE id = :parcelId")
    suspend fun updateParcelStatus(parcelId: Long, isDelivered: Boolean)

    // 4. INSERARE PROGRESIVĂ (100, 101...)
    @Query("SELECT MAX(displayId) FROM parcels WHERE routeId = :routeId")
    suspend fun getMaxIdForRoute(routeId: Long): Int?

    @Insert
    suspend fun insertParcel(parcel: ParcelEntity)

    @Query("""
    SELECT * FROM parcels 
    WHERE routeId = :routeId 
    AND (
        CAST(displayId AS TEXT) LIKE '%' || :query || '%' 
        OR firstNameLastName LIKE '%' || :query || '%' 
        OR phone LIKE '%' || :query || '%'
    )
    ORDER BY 
        isDelivered ASC,  -- Pune FALSE (0) înainte de TRUE (1)
        displayId DESC    -- În cadrul grupurilor, cele mai noi primele
""")
    fun searchParcels(routeId: Long, query: String): Flow<List<ParcelEntity>>


    @Transaction
    suspend fun addParcel(
        routeId: Long,
        firstNameLastName: String,
        phone: String,
        weight: Double,
        priceKg: Double,
        pieces: Int
    ): ParcelEntity {
        // 1. CALCULĂM "BAZA" (Prefixul)
        // Ruta 5  -> 5000
        // Ruta 10 -> 10000
        // Ruta 12 -> 12000
        val multiplier = 1000
        val routeBase = routeId.toInt() * multiplier

        // 2. AFLĂM ULTIMUL ID
        val maxId = getMaxIdForRoute(routeId)

        // 3. LOGICA DE INCREMENTARE SIMPLĂ ȘI SIGURĂ
        // Dacă nu avem colete, începem cu 'routeBase' (ex: 5000)
        // Următorul va fi 5001.
        val lastId = maxId ?: routeBase

        val nextId = lastId + 1

        // 4. VERIFICARE DE SIGURANȚĂ (OPȚIONAL)
        // Ne asigurăm că nu intrăm peste ruta următoare (ex: Ruta 5 nu ajunge la 6000)
        if (nextId >= (routeId.toInt() + 1) * multiplier) {
            // Aici poți decide: arunci eroare sau treci automat la 10000 (dar devine complicat)
            // Pentru un camion, 999 de colete este arhisuficient.
            throw Exception("Ruta este plină! (Max 999 colete)")
        }

        // 5. SALVARE
        val newParcel = ParcelEntity(
            routeId = routeId,
            displayId = nextId, // Rezultat: 5001, 5002, 5003...
            firstNameLastName = firstNameLastName,
            phone = phone,
            weight = weight,
            pricePerKg = priceKg,
            totalSum = weight * priceKg,
            pieceCount = pieces,
            isDelivered = false,
        )

        insertParcel(newParcel)
        return newParcel
    }



    @Query("DELETE FROM parcels WHERE id = :id")
    suspend fun deleteParcelsById(id: Long)
}