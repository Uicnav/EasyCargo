package com.vantechinformatics.easycargo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vantechinformatics.easycargo.data.RouteEntity
import com.vantechinformatics.easycargo.data.dao.RouteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RouteViewModel(private val dao: RouteDao) : ViewModel() {

    fun deleteRouteComplete(routeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteRouteComplete(routeId = routeId)
        }
    }

    val allRoutes: Flow<List<RouteEntity>> = dao.getAllRoutes()

    suspend fun insertRoute(routeEntity: RouteEntity): Long {/**/
        return dao.insertRoute(routeEntity)
    }
}