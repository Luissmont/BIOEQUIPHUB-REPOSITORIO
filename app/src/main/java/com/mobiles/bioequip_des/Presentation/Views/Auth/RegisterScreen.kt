package com.mobiles.bioequip_des.Presentation.Views.Auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobiles.bioequip_des.Presentation.ViewModel.AuthUiState
import com.mobiles.bioequip_des.Presentation.ViewModel.AuthViewModel
import com.mobiles.bioequip_des.Presentation.ui.theme.BIOEQUIPDESTheme
import com.mobiles.bioequip_des.Presentation.ui.theme.BioequipTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val roles = listOf("Biomédico", "Técnico", "Médico", "Enfermero", "Mantenimiento", "Practicante")

    val uiState by authViewModel.uiState.collectAsState()

    // Validaciones
    val isEmailValid = email.isEmpty() || (email.contains("@") && email.length > 3)
    val isPasswordLengthValid = password.isEmpty() || password.length >= 6
    val isPasswordMatch = confirmPassword.isEmpty() || password == confirmPassword

    val emailError = if (!isEmailValid && email.isNotEmpty()) "Formato de E-mail no válido" else null
    val passwordError = if (!isPasswordLengthValid && password.isNotEmpty()) "Mínimo 6 caracteres" else null
    val confirmPasswordError = if (!isPasswordMatch && confirmPassword.isNotEmpty()) "Las contraseñas no coinciden" else null

    val isFormValid = name.isNotBlank() &&
            lastName.isNotBlank() &&
            role.isNotBlank() &&
            isEmailValid &&
            isPasswordLengthValid &&
            isPasswordMatch &&
            password.isNotBlank() &&
            confirmPassword.isNotBlank()

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                authViewModel.resetState()
                onRegisterSuccess()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BioequipTeal)
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Regresar",
                tint = Color.Black
            )
        }

        Text(
            text = "Regístrate",
            style = TextStyle(
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier
                .padding(top = 80.dp, start = 24.dp)
        )

        Card(
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.80f)
                .align(Alignment.BottomCenter),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomTextField(name, { name = it }, "Nombres")
                CustomTextField(lastName, { lastName = it }, "Apellidos")

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "E-mail",
                    isError = emailError != null,
                    errorMessage = emailError
                )

                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
                    isPassword = true,
                    isError = passwordError != null,
                    errorMessage = passwordError
                )

                CustomTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirmar Contraseña",
                    isPassword = true,
                    isError = confirmPasswordError != null,
                    errorMessage = confirmPasswordError
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = role.ifEmpty { "Seleccione su rol" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Rol") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .height(60.dp),
                            shape = RoundedCornerShape(30.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            roles.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        role = selectionOption
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState is AuthUiState.Error) {
                    Text(
                        text = (uiState as AuthUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Button(
                    onClick = {
                        authViewModel.register(name, lastName, email, password, role)
                    },
                    enabled = isFormValid && uiState !is AuthUiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(50.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    if (uiState is AuthUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text("REGISTRARSE", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    label,
                    color = if (isError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(30.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                errorContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    BIOEQUIPDESTheme {
        RegisterScreen()
    }
}