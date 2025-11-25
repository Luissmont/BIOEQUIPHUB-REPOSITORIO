package com.mobiles.bioequip_des.Presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiles.bioequip_des.Data.Models.UserWithRegistries
import com.mobiles.bioequip_des.Data.Repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UserUiState {
    object Idle : UserUiState()
    object Loading : UserUiState()
    data class Success(val data: UserWithRegistries) : UserUiState()
    data class Error(val message: String) : UserUiState()
}

class UserViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    fun loadUserData(userId: String) {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading

            val result = repository.getUserWithRegistries(userId)

            _uiState.value = if (result.isSuccess) {
                UserUiState.Success(result.getOrNull()!!)
            } else {
                UserUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al cargar datos"
                )
            }
        }
    }

    fun setActiveRegistry(userId: String, registryId: String) {
        viewModelScope.launch {
            val result = repository.setActiveRegistry(userId, registryId)

            if (result.isSuccess) {
                loadUserData(userId)
            }
        }
    }

    fun leaveRegistry(userId: String, registryId: String) {
        viewModelScope.launch {
            _uiState.value = UserUiState.Loading

            val result = repository.leaveRegistry(userId, registryId)

            if (result.isSuccess) {
                loadUserData(userId)
            } else {
                _uiState.value = UserUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al salir del registro"
                )
            }
        }
    }
}