package com.mobiles.bioequip_des.Presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobiles.bioequip_des.Data.Models.User
import com.mobiles.bioequip_des.Data.Repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun checkUserSession(): Boolean {
        return repository.isUserLoggedIn()
    }

    fun register(
        name: String,
        lastName: String,
        email: String,
        password: String,
        role: String
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val result = repository.register(name, lastName, email, password, role)

            _uiState.value = if (result.isSuccess) {
                AuthUiState.Success(result.getOrNull()!!)
            } else {
                AuthUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al registrar usuario"
                )
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            val result = repository.login(email, password)

            _uiState.value = if (result.isSuccess) {
                AuthUiState.Success(result.getOrNull()!!)
            } else {
                AuthUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al iniciar sesión"
                )
            }
        }
    }

    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState.Idle
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    suspend fun checkUserHasRegistry(): Boolean {
        return repository.userHasRegistry()
    }

}

