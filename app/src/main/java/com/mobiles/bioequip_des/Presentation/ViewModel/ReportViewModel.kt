package com.mobiles.bioequip_des.Presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiles.bioequip_des.Data.Models.Report
import com.mobiles.bioequip_des.Data.Repositories.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ReportUiState {
    object Idle : ReportUiState()
    object Loading : ReportUiState()
    data class Success(val report: Report?) : ReportUiState()
    data class ReportCreated(val report: Report) : ReportUiState()
    data class MaintenanceStarted(val reportId: String) : ReportUiState()
    data class InterventionsHistory(val reports: List<Report>) : ReportUiState()
    data class Error(val message: String) : ReportUiState()
}

class ReportViewModel : ViewModel() {

    private val repository = ReportRepository()

    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Idle)
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    fun createReport(report: Report, equipmentId: String) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading

            val result = repository.createReport(report, equipmentId)

            _uiState.value = if (result.isSuccess) {
                ReportUiState.ReportCreated(result.getOrNull()!!)
            } else {
                ReportUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al crear reporte"
                )
            }
        }
    }

    fun getActiveReport(equipmentId: String) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading

            val result = repository.getActiveReport(equipmentId)

            _uiState.value = if (result.isSuccess) {
                ReportUiState.Success(result.getOrNull())
            } else {
                ReportUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al obtener reporte"
                )
            }
        }
    }

    fun startMaintenance(reportId: String, userId: String, equipmentId: String) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading

            val result = repository.startMaintenance(reportId, userId, equipmentId)

            _uiState.value = if (result.isSuccess) {
                ReportUiState.MaintenanceStarted(reportId)
            } else {
                ReportUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al iniciar mantenimiento"
                )
            }
        }
    }


    fun resetState() {
        _uiState.value = ReportUiState.Idle
    }

    fun getInterventionsHistory(equipmentId: String) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading

            val result = repository.getEquipmentInterventionsHistory(equipmentId)

            _uiState.value = if (result.isSuccess) {
                ReportUiState.InterventionsHistory(result.getOrNull()!!)
            } else {
                ReportUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al cargar historial"
                )
            }
        }
    }
}