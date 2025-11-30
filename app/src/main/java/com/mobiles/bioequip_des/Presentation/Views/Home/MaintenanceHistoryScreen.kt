package com.mobiles.bioequip_des.Presentation.Views.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.mobiles.bioequip_des.R
import com.mobiles.bioequip_des.Data.Models.Equipment
import com.mobiles.bioequip_des.Data.Models.MaintenanceUpdate
import com.mobiles.bioequip_des.Presentation.ViewModel.MaintenanceUiState
import com.mobiles.bioequip_des.Presentation.ViewModel.MaintenanceViewModel
import com.mobiles.bioequip_des.Presentation.ViewModel.UserUiState
import com.mobiles.bioequip_des.Presentation.ViewModel.UserViewModel
import com.mobiles.bioequip_des.Presentation.ui.theme.BIOEQUIPDESTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MaintenanceHistoryScreen(
    equipment: Equipment,
    reportId: String,
    onNavigateBack: () -> Unit = {},
    onNavigateToAddUpdate: (Equipment, String) -> Unit = { _, _ -> },
    onNavigateToUpdateDetail: (MaintenanceUpdate) -> Unit = {},
    onMaintenanceFinished: () -> Unit = {},
    maintenanceViewModel: MaintenanceViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val maintenanceState by maintenanceViewModel.uiState.collectAsState()
    val userState by userViewModel.uiState.collectAsState()

    var showFinishDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            userViewModel.loadUserData(uid)
        }
    }

    LaunchedEffect(reportId) {
        maintenanceViewModel.loadUpdates(reportId)
    }

    LaunchedEffect(maintenanceState) {
        if (maintenanceState is MaintenanceUiState.MaintenanceFinished) {
            maintenanceViewModel.resetState()
            onMaintenanceFinished()
        } else if (maintenanceState is MaintenanceUiState.UpdateCreated) {
            maintenanceViewModel.loadUpdates(reportId)
        }
    }

    val canModify = when (userState) {
        is UserUiState.Success -> {
            val userRole = (userState as UserUiState.Success).data.user.role.lowercase().trim()
            userRole in listOf("biomedico", "biomédico", "tecnico", "técnico")
        }
        else -> false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color(0xFFF5F5F5))
        ) {
            Image(
                painter = painterResource(id = R.drawable.fondomed2),
                contentDescription = "Header background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.3f
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mantenimiento",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFFFFA726), CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.reload),
                    contentDescription = "En mantenimiento",
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }

            Text(
                text = "EN MANTENIMIENTO",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (val state = maintenanceState) {
                is MaintenanceUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is MaintenanceUiState.Success -> {
                    if (state.updates.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        ) {
                            Text(
                                text = "Aún no hay actualizaciones",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.updates) { update ->
                                MaintenanceUpdateCard(
                                    update = update,
                                    onClick = { onNavigateToUpdateDetail(update) }
                                )
                            }
                        }
                    }
                }

                is MaintenanceUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }

                else -> {}
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onNavigateToAddUpdate(equipment, reportId) },
                    enabled = canModify,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00BCD4),
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    Text("Actualizar", fontSize = 16.sp, color = Color.White)
                }

                Button(
                    onClick = { showFinishDialog = true },
                    enabled = canModify,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    Text("FINALIZAR", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Finalizar Mantenimiento") },
            text = { Text("¿Está seguro que quiere terminar este mantenimiento?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFinishDialog = false
                        maintenanceViewModel.finishMaintenance(reportId, equipment.id)
                    }
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
private fun MaintenanceUpdateCard(
    update: MaintenanceUpdate,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    val timeFormat = SimpleDateFormat("HH:mm", Locale("es", "ES"))

    val date = Date(update.createdAt)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.reload),
                    contentDescription = "Update",
                    modifier = Modifier.size(28.dp),
                    tint = Color(0xFF4A90A4)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = update.updateName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = dateFormat.format(date),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = timeFormat.format(date),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MaintenanceHistoryScreenPreview() {
    BIOEQUIPDESTheme {
        MaintenanceHistoryScreen(
            equipment = Equipment(id = "test", name = "Test"),
            reportId = "test"
        )
    }
}