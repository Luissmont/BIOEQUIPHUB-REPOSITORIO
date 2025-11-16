package com.mobiles.bioequip_des.Presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiles.bioequip_des.Data.Models.JoinRequest
import com.mobiles.bioequip_des.Data.Models.Registry
import com.mobiles.bioequip_des.Data.Repositories.RegistryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RegistryUiState {
    object Idle : RegistryUiState()
    object Loading : RegistryUiState()
    data class CreateSuccess(val registry: Registry) : RegistryUiState()
    data class JoinSuccess(val request: JoinRequest) : RegistryUiState()
    data class Error(val message: String) : RegistryUiState()
}

class RegistryViewModel : ViewModel() {

    private val repository = RegistryRepository()

    private val _uiState = MutableStateFlow<RegistryUiState>(RegistryUiState.Idle)
    val uiState: StateFlow<RegistryUiState> = _uiState.asStateFlow()

    fun createRegistry(
        name: String,
        type: String,
        accessCode: String,
        adminUid: String
    ) {
        viewModelScope.launch {
            _uiState.value = RegistryUiState.Loading

            val result = repository.createRegistry(name, type, accessCode, adminUid)

            _uiState.value = if (result.isSuccess) {
                RegistryUiState.CreateSuccess(result.getOrNull()!!)
            } else {
                RegistryUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al crear registro"
                )
            }
        }
    }

    fun requestJoinRegistry(
        userId: String,
        userName: String,
        userEmail: String,
        accessCode: String
    ) {
        viewModelScope.launch {
            _uiState.value = RegistryUiState.Loading

            val result = repository.requestJoinRegistry(userId, userName, userEmail, accessCode)

            _uiState.value = if (result.isSuccess) {
                RegistryUiState.JoinSuccess(result.getOrNull()!!)
            } else {
                RegistryUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al solicitar unión"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = RegistryUiState.Idle
    }
}