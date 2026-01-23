package com.vantechinformatics.easycargo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vantechinformatics.easycargo.data.ParcelEntity
import com.vantechinformatics.easycargo.data.ParcelUi
import com.vantechinformatics.easycargo.data.RouteStats
import com.vantechinformatics.easycargo.data.dao.ParcelDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ParcelViewModel(private val dao: ParcelDao) : ViewModel() {
    fun searchParcels(routeId: Long, searchQuery: String): Flow<List<ParcelUi>> {
        return dao.searchParcels(routeId, searchQuery).map { parcelEntities ->
            // Transform each ParcelEntity in the list to a ParcelUi
            parcelEntities.map { entity ->
                entity.toUiModel()
            }
        }
    }

    fun getRouteStats(routeId: Long): Flow<RouteStats> {
        return dao.getRouteStats(routeId)
    }

    fun deleteParcelsById(parceId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteParcelsById(parceId)

        }
    }

    suspend fun addParcel(
        routeId: Long,
        firstNameLastName: String,
        phone: String,
        weight: Double,
        priceKg: Double,
        pieces: Int
    ): ParcelUi {
        return dao.addParcel(routeId, firstNameLastName, phone, weight, priceKg, pieces).toUiModel()
    }

    fun updateParcelStatus(parcelId: Long, isDelivered: Boolean) {
        viewModelScope.launch {
            dao.updateParcelStatus(parcelId, isDelivered)

        }
    }
}