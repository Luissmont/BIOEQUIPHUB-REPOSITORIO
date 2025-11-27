package com.mobiles.bioequip_des.Presentation.ViewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiles.bioequip_des.Data.Models.Equipment
import com.mobiles.bioequip_des.Data.Repositories.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class InventoryUiState {
    object Idle : InventoryUiState()
    object Loading : InventoryUiState()
    data class Success(val equipments: List<Equipment>) : InventoryUiState()
    data class EquipmentAdded(val equipment: Equipment) : InventoryUiState()
    data class Error(val message: String) : InventoryUiState()
}

class InventoryViewModel : ViewModel() {

    private val repository = InventoryRepository()

    private val _uiState = MutableStateFlow<InventoryUiState>(InventoryUiState.Idle)
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    fun addEquipment(equipment: Equipment, photoUri: Uri?, context: Context) {
        viewModelScope.launch {
            _uiState.value = InventoryUiState.Loading

            val result = repository.addEquipment(equipment, photoUri, context)

            _uiState.value = if (result.isSuccess) {
                InventoryUiState.EquipmentAdded(result.getOrNull()!!)
            } else {
                InventoryUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al agregar equipo"
                )
            }
        }
    }

    fun loadEquipments(registryId: String) {
        viewModelScope.launch {
            _uiState.value = InventoryUiState.Loading

            val result = repository.getEquipmentsByRegistry(registryId)

            _uiState.value = if (result.isSuccess) {
                InventoryUiState.Success(result.getOrNull()!!)
            } else {
                InventoryUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al cargar equipos"
                )
            }
        }
    }

    fun searchByRegistrationNumber(registryId: String, query: String) {
        viewModelScope.launch {
            _uiState.value = InventoryUiState.Loading

            val result = repository.searchByRegistrationNumber(registryId, query)

            _uiState.value = if (result.isSuccess) {
                InventoryUiState.Success(result.getOrNull()!!)
            } else {
                InventoryUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error en búsqueda"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = InventoryUiState.Idle
    }
}