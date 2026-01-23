package com.vantechinformatics.easycargo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vantechinformatics.easycargo.data.ParcelEntity
import com.vantechinformatics.easycargo.data.RouteEntity
import com.vantechinformatics.easycargo.data.RouteStats
import com.vantechinformatics.easycargo.data.dao.ParcelDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ParcelViewModel(private val dao: ParcelDao) : ViewModel() {
    fun searchParcels(routeId: Long, searchQuery: String): Flow<List<ParcelEntity>> {
        return dao.searchParcels(routeId, searchQuery)
    }

    fun getRouteStats(routeId: Long): Flow<RouteStats> {
       return dao.getRouteStats(routeId)
    }

    fun deleteParcelsById(parceId: Long){
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
    ): ParcelEntity {
       return    dao.addParcel(routeId,firstNameLastName,phone,weight, priceKg, pieces)
   }
    fun updateParcelStatus(parcelId: Long, isDelivered: Boolean) {
        viewModelScope.launch {
            dao.updateParcelStatus(parcelId, isDelivered)

        }
    }
}