package com.vantechinformatics.easycargo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vantechinformatics.easycargo.data.RouteStats
import com.vantechinformatics.easycargo.data.RouteUi
import com.vantechinformatics.easycargo.data.dao.ParcelDao
import com.vantechinformatics.easycargo.data.dao.RouteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class RouteViewModel(private val dao: RouteDao, private val parcelDao: ParcelDao) : ViewModel() {
    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow<RouteUiState>(RouteUiState.Loading)

    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<RouteUiState> = _uiState
    private var routeToDelete: RouteUi? = null

    init {
        getRoutes()
    }

    fun deleteRouteComplete(routeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteRouteComplete(routeId = routeId)
        }
    }

    fun prepareDeleteRoute(routeId: Long) {
        val currentList = _uiState.value as? RouteUiState.Success ?: return
        val updatedList = currentList.data.map {
            if (it.routeId == routeId) {
                routeToDelete = it.copy(isVisible = false)
                routeToDelete!!
            } else {
                it
            }
        }
        updatedList.let {
            _uiState.value = RouteUiState.Success(it)
        }
    }

    fun deleteRouteToDelete() {
        routeToDelete?.let {
            deleteRouteComplete(it.routeId)
        }
        routeToDelete = null
    }

    private fun getRoutes() = viewModelScope.launch(Dispatchers.IO) {

        dao.getAllRoutes().map { routeEntities ->
            routeEntities.map { entity ->
                entity.toUiModel()
            }
        }.collect {
            _uiState.value = RouteUiState.Success(it.map { value ->
                if (value.routeId == routeToDelete?.routeId) value.copy(isVisible = false) else value
            })
        }
    }

    fun undoDeleteRoute(routeId: Long) {
        routeToDelete?.let { routeToDelete ->
            if (routeToDelete.routeId == routeId) {
                val currentList = _uiState.value as? RouteUiState.Success ?: return
                val updatedList = currentList.data.map {
                    if (it.routeId == routeId) {
                        it.copy(isVisible = true)
                    } else {
                        it
                    }
                }
                updatedList.let {
                    _uiState.value = RouteUiState.Success(it)
                }
                this.routeToDelete = null
            }
        }
    }

    fun deleteGameToDelete() {
        routeToDelete?.let {
            deleteRouteComplete(it.routeId)
        }
        routeToDelete = null
    }

    fun getRouteStats(routeId: Long): Flow<RouteStats> {
        return parcelDao.getRouteStats(routeId)
    }

    suspend fun insertRoute(route: RouteUi): Long {
        return dao.insertRoute(route.toEntity())
    }
}


sealed interface RouteUiState {
    data object Loading : RouteUiState
    data class Success(val data: List<RouteUi>) : RouteUiState
    data class Error(val exception: Throwable) : RouteUiState
}