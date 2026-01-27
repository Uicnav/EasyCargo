package com.vantechinformatics.easycargo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vantechinformatics.easycargo.data.ParcelUi
import com.vantechinformatics.easycargo.data.RouteStats
import com.vantechinformatics.easycargo.data.dao.ParcelDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ParcelViewModel(private val dao: ParcelDao) : ViewModel() {

    var parcelToDelete: ParcelUi? = null

    fun searchParcels(routeId: Long, searchQuery: String): Flow<List<ParcelUi>> {
        return dao.searchParcels(routeId, searchQuery).map { parcelEntities ->
            // Transform each ParcelEntity in the list to a ParcelUi
            parcelEntities.filter { parcelEntity -> parcelEntity.isVisible }.map { entity ->
                entity.toUiModel()
            }
        }
    }

    fun prepareDeleteParcel(parcel: ParcelUi) {
        parcelToDelete = parcel
        updateParcelStatus(parcel.id, parcel.isDelivered, false)
    }


    fun getRouteStats(id: Long): Flow<RouteStats> {
        return dao.getRouteStats(id)
    }

    fun deleteParcelsById(parceId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteParcelsById(parceId)

        }
    }

    suspend fun addParcel(
        id: Long,
        firstNameLastName: String,
        phone: String,
        weight: Double,
        priceKg: Double,
        pieces: Int,
        city: String
    ): ParcelUi {

        return dao.addParcel(id, firstNameLastName, phone, weight, priceKg, pieces, city)
            .toUiModel()
    }

    fun updateParcelStatus(parcelId: Long, isDelivered: Boolean, isVisible: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateParcelStatus(parcelId, isDelivered, isVisible)

        }
    }

    fun undoDeleteParcel() {
        parcelToDelete?.let { parcel ->
            updateParcelStatus(parcel.id, parcel.isDelivered, true)
        }
    }

    fun deleteParcelToDelete() {
        parcelToDelete?.let {
            deleteParcelsById(it.id)
        }
        parcelToDelete = null
    }
}