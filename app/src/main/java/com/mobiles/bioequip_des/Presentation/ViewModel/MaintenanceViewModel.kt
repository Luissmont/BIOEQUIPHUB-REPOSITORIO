package com.mobiles.bioequip_des.Presentation.ViewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiles.bioequip_des.Data.Models.MaintenanceUpdate
import com.mobiles.bioequip_des.Data.Repositories.MaintenanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MaintenanceUiState {
    object Idle : MaintenanceUiState()
    object Loading : MaintenanceUiState()
    data class Success(val updates: List<MaintenanceUpdate>) : MaintenanceUiState()
    data class UpdateCreated(val update: MaintenanceUpdate) : MaintenanceUiState()
    data class MaintenanceFinished(val reportId: String) : MaintenanceUiState()
    data class Error(val message: String) : MaintenanceUiState()
}

class MaintenanceViewModel : ViewModel() {

    private val repository = MaintenanceRepository()

    private val _uiState = MutableStateFlow<MaintenanceUiState>(MaintenanceUiState.Idle)
    val uiState: StateFlow<MaintenanceUiState> = _uiState.asStateFlow()

    fun createUpdate(update: MaintenanceUpdate, photoUri: Uri?, context: Context) {
        viewModelScope.launch {
            _uiState.value = MaintenanceUiState.Loading

            val result = repository.createMaintenanceUpdate(update, photoUri, context)

            _uiState.value = if (result.isSuccess) {
                MaintenanceUiState.UpdateCreated(result.getOrNull()!!)
            } else {
                MaintenanceUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al crear actualización"
                )
            }
        }
    }

    fun loadUpdates(reportId: String) {
        viewModelScope.launch {
            _uiState.value = MaintenanceUiState.Loading

            val result = repository.getMaintenanceUpdates(reportId)

            _uiState.value = if (result.isSuccess) {
                MaintenanceUiState.Success(result.getOrNull()!!)
            } else {
                MaintenanceUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al cargar actualizaciones"
                )
            }
        }
    }

    fun finishMaintenance(reportId: String, equipmentId: String) {
        viewModelScope.launch {
            _uiState.value = MaintenanceUiState.Loading

            val result = repository.finishMaintenance(reportId, equipmentId)

            _uiState.value = if (result.isSuccess) {
                MaintenanceUiState.MaintenanceFinished(reportId)
            } else {
                MaintenanceUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al finalizar mantenimiento"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = MaintenanceUiState.Idle
    }
}