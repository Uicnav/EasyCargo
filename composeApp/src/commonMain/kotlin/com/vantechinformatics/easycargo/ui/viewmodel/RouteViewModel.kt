package com.vantechinformatics.easycargo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vantechinformatics.easycargo.data.RouteUi
import com.vantechinformatics.easycargo.data.dao.RouteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class RouteViewModel(private val dao: RouteDao) : ViewModel() {

    fun deleteRouteComplete(routeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteRouteComplete(routeId = routeId)
        }
    }

    val allRoutes: Flow<List<RouteUi>> = dao.getAllRoutes().map { routeEntities ->
        routeEntities.map { entity ->
            entity.toUiModel()
        }
    }

    suspend fun insertRoute(route: RouteUi): Long {/**/
        return dao.insertRoute(route.toEntity())
    }
}