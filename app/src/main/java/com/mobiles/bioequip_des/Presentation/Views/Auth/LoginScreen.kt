package com.mobiles.bioequip_des.Presentation.Views.Auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobiles.bioequip_des.Presentation.ViewModel.AuthUiState
import com.mobiles.bioequip_des.Presentation.ViewModel.AuthViewModel
import com.mobiles.bioequip_des.Presentation.ui.theme.BIOEQUIPDESTheme
import com.mobiles.bioequip_des.Presentation.ui.theme.BioequipTeal

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by authViewModel.uiState.collectAsState()

    val isEmailValid = email.isEmpty() || (email.contains("@") && email.length > 3)
    val isPasswordValid = password.isEmpty() || password.length >= 6

    val emailError = if (!isEmailValid && email.isNotEmpty()) "Formato de E-mail no válido" else null
    val passwordError = if (!isPasswordValid && password.isNotEmpty()) "Mínimo 6 caracteres" else null

    val isFormValid = email.isNotBlank() && password.isNotBlank() && isEmailValid && isPasswordValid

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> {
                authViewModel.resetState()
                onLoginSuccess()
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
            text = "Iniciar Sesión",
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
                .fillMaxHeight(0.65f)
                .align(Alignment.BottomCenter),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = {
                            Text(
                                "E-mail",
                                color = if (emailError != null) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        isError = emailError != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            errorContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    if (emailError != null) {
                        Text(
                            text = emailError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(
                                "Contraseña",
                                color = if (passwordError != null) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        isError = passwordError != null,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            errorContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    if (passwordError != null) {
                        Text(
                            text = passwordError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
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
                        authViewModel.login(email, password)
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
                        Text("INICIAR SESIÓN", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    BIOEQUIPDESTheme {
        LoginScreen()
    }
}