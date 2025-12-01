package com.mobiles.bioequip_des.Presentation.Views.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.mobiles.bioequip_des.R
import com.mobiles.bioequip_des.Data.Models.Equipment
import com.mobiles.bioequip_des.Presentation.ViewModel.ReportUiState
import com.mobiles.bioequip_des.Presentation.ViewModel.ReportViewModel
import com.mobiles.bioequip_des.Presentation.ViewModel.UserUiState
import com.mobiles.bioequip_des.Presentation.ViewModel.UserViewModel
import com.mobiles.bioequip_des.Presentation.ui.theme.BIOEQUIPDESTheme
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun CurrentTrackingTab(
    equipment: Equipment,
    onNavigateBack: () -> Unit = {},
    onNavigateToReportFault: (Equipment) -> Unit = {},
    onNavigateToMaintenance: (Equipment, String) -> Unit = { _, _ -> },
    reportViewModel: ReportViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val reportState by reportViewModel.uiState.collectAsState()
    val userState by userViewModel.uiState.collectAsState()

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            userViewModel.loadUserData(uid)
        }
    }

    LaunchedEffect(equipment.id) {
        reportViewModel.getActiveReport(equipment.id)
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
                    text = "Tracking Actual",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        when (val state = reportState) {
            is ReportUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ReportUiState.Success -> {
                val activeReport = state.report

                if (activeReport == null) {
                    EquipmentWorkingContent(
                        onReportFault = { onNavigateToReportFault(equipment) }
                    )
                } else {
                    if (activeReport.status == "in_maintenance") {
                        LaunchedEffect(Unit) {
                            onNavigateToMaintenance(equipment, activeReport.id)
                        }
                    } else {
                        EquipmentFaultContent(
                            reporterName = activeReport.reporterName,
                            reason = activeReport.reason,
                            reportStatus = activeReport.status,
                            onStartMaintenance = {
                                currentUser?.uid?.let { userId ->
                                    reportViewModel.startMaintenance(
                                        activeReport.id,
                                        userId,
                                        equipment.id
                                    )
                                }
                            },
                            userState = userState
                        )
                    }
                }
            }
            is ReportUiState.MaintenanceStarted -> {
                LaunchedEffect(Unit) {
                    val reportId = (reportState as ReportUiState.MaintenanceStarted) .reportId
                    onNavigateToMaintenance(equipment, reportId)
                }
            }

            is ReportUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun EquipmentWorkingContent(
    onReportFault: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .background(Color(0xFF4CAF50), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.check2),
                contentDescription = "Funcionando",
                modifier = Modifier.size(100.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Este equipo se encuentra\nen funcionamiento\nactualmente",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onReportFault,
            modifier = Modifier
                .width(200.dp)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE53935)
            )
        ) {
            Text("Reportar Avería", fontSize = 16.sp, color = Color.White)
        }
    }
}

@Composable
private fun EquipmentFaultContent(
    reporterName: String,
    reason: String,
    reportStatus: String,
    onStartMaintenance: () -> Unit,
    userState: UserUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(180.dp)
                .background(Color(0xFFE53935), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.sad),
                contentDescription = "Fuera de servicio",
                modifier = Modifier.size(100.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "El equipo se encuentra\nfuera de servicio",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Persona que realizó la publicación:",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = reporterName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Motivo del Reporte:",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = reason,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (reportStatus == "pending") {
            val canStartMaintenance = when (userState) {
                is UserUiState.Success -> {
                    val userRole = userState.data.user.role.lowercase().trim()

                    userRole in listOf(
                        "biomedico",
                        "biomédico",
                        "tecnico",
                        "técnico"
                    )
                }
                else -> false
            }

            Button(
                onClick = onStartMaintenance,
                enabled = canStartMaintenance,
                modifier = Modifier
                    .width(240.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726),
                    disabledContainerColor = Color.LightGray
                )
            ) {
                Text("Empezar Mantenimiento", fontSize = 16.sp, color = Color.White)
            }

            if (!canStartMaintenance) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Solo biomédicos y técnicos pueden iniciar mantenimiento",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                if (userState is UserUiState.Success){
                    Text(
                        text = "Tu rol: ${userState.data.user.role}",
                        fontSize = 10.sp,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (reportStatus == "in_maintenance") {
            Text(
                text = "Este equipo está en mantenimiento",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFA726),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurrentTrackingTabPreview() {
    BIOEQUIPDESTheme {
        CurrentTrackingTab(
            equipment = Equipment(
                id = "test",
                name = "Electrocardiógrafo"
            )
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}