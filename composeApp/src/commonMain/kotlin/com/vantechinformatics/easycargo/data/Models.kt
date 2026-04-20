package com.vantechinformatics.easycargo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.vantechinformatics.easycargo.utils.getCurrentTime

// Parcel kind. Stored as a stable string token in DB; UI maps to a localized label.
enum class ParcelType(val token: String) {
    BOX("box"),
    SUITCASE("suitcase"),
    BAG("bag"),
    FURNITURE("furniture"),
    ENVELOPE("envelope"),
    DOCUMENTS("documents"),
    OTHER("other");

    companion object {
        fun fromToken(token: String?): ParcelType =
            entries.firstOrNull { it.token == token } ?: OTHER
    }
}

@Entity(
    tableName = "parcels", foreignKeys = [ForeignKey(
        entity = RouteEntity::class,
        parentColumns = ["routeId"],
        childColumns = ["routeId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ParcelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, val routeId: Long,

    val displayId: Int,          // Identificatorul vizual: 100, 101, 102...
    val firstNameLastName: String, val phone: String,

    val weight: Double,          // Kg
    val pricePerKg: Double,      // Preț pe Kg
    val totalSum: Double,        // Suma finală (calculată)
    val pieceCount: Int,         // Număr bucăți (saci/pachete)

    val isDelivered: Boolean = false, // Status: Livrat sau Nu
    val isVisible: Boolean,
    val addedAt: Long = getCurrentTime(),
    val city: String,
    @ColumnInfo(defaultValue = "")
    val type: String = ""        // Token din ParcelType (gol = nesetat)
) {
    // Corrected toUiModel function
    fun toUiModel(): ParcelUi {
        return ParcelUi(
            id = id,
            routeId = routeId,
            displayId = displayId,
            firstNameLastName = firstNameLastName,
            phone = phone,
            weight = weight,
            pricePerKg = pricePerKg,
            totalSum = totalSum,
            pieceCount = pieceCount,
            isDelivered = isDelivered,
            addedAt = addedAt,
            isVisible = isVisible,
            showOnlyInfo = false,
            city = city,
            type = type
        )
    }
}

data class ParcelUi(
    val id: Long = 0, val routeId: Long,

    val displayId: Int, val firstNameLastName: String, val phone: String,

    val weight: Double, val pricePerKg: Double, val totalSum: Double, val pieceCount: Int,

    val isDelivered: Boolean, val addedAt: Long, var showOnlyInfo: Boolean = false, var isVisible: Boolean = true, val city: String,
    val type: String = ""
) {
    // Completed toEntity function to map all fields
    fun toEntity(): ParcelEntity {
        return ParcelEntity(
            id = id,
            routeId = routeId,
            displayId = displayId,
            firstNameLastName = firstNameLastName,
            phone = phone,
            weight = weight,
            pricePerKg = pricePerKg,
            totalSum = totalSum,
            pieceCount = pieceCount,
            isDelivered = isDelivered,
            addedAt = addedAt,
            isVisible = isVisible,
            city = city,
            type = type
        )
    }
}

// O clasă ajutătoare pentru a citi statisticile rutei
data class RouteStats(
    val totalParcels: Int, val deliveredParcels: Int, val totalMoney: Double
)

@Entity(tableName = "routes")
data class RouteUi(
    val routeId: Long = 0, val name: String,        // Ex: "Tur 18.01.2026"
    val createdAt: Long = getCurrentTime(),     // Timestamp
    val isActive: Boolean = true,
    var isVisible: Boolean = true
) {
    fun toEntity(): RouteEntity {
        return RouteEntity(
            routeId = routeId, name = name, createdAt = createdAt, isActive = isActive
        )
    }
}

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey(autoGenerate = true) val routeId: Long = 0,
    val name: String,        // Ex: "Tur 18.01.2026"
    val createdAt: Long = getCurrentTime(),     // Timestamp
    val isActive: Boolean = true
) {
    // Transformă Entitatea de DB în Model de UI
    fun toUiModel(): RouteUi {
        return RouteUi(
            routeId = routeId, name = name, createdAt = createdAt, isActive = isActive
        )
    }
}