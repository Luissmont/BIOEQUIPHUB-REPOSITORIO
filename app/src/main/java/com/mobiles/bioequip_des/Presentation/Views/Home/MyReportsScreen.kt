package com.mobiles.bioequip_des.Presentation.Views.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.mobiles.bioequip_des.R
import com.mobiles.bioequip_des.Data.Models.Report
import com.mobiles.bioequip_des.Presentation.ViewModel.InventoryUiState
import com.mobiles.bioequip_des.Presentation.ViewModel.InventoryViewModel
import com.mobiles.bioequip_des.Presentation.ViewModel.ReportUiState
import com.mobiles.bioequip_des.Presentation.ViewModel.ReportViewModel
import com.mobiles.bioequip_des.Presentation.ViewModel.UserUiState
import com.mobiles.bioequip_des.Presentation.ViewModel.UserViewModel
import com.mobiles.bioequip_des.Presentation.ui.theme.BIOEQUIPDESTheme

@Composable
fun MyReportsScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToEquipmentDetail: (String, String) -> Unit = { _, _ -> },
    reportViewModel: ReportViewModel = viewModel(),
    inventoryViewModel: InventoryViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val reportState by reportViewModel.uiState.collectAsState()
    val inventoryState by inventoryViewModel.uiState.collectAsState()
    val userState by userViewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            userViewModel.loadUserData(uid)
        }
    }

    LaunchedEffect(currentUser?.uid, userState) {
        currentUser?.uid?.let { uid ->
            reportViewModel.getUserReports(uid)

            if (userState is UserUiState.Success) {
                val activeRegistryId = (userState as UserUiState.Success).data.activeRegistry?.id
                activeRegistryId?.let { registryId ->
                    inventoryViewModel.loadEquipments(registryId)
                }
            }
        }
    }

    val equipments = when (val state = inventoryState) {
        is InventoryUiState.Success -> state.equipments
        else -> emptyList()
    }

    val filteredReports = when (val state = reportState) {
        is ReportUiState.UserReports -> {
            if (searchQuery.isBlank()) {
                state.reports
            } else {
                state.reports.filter { report ->
                    val equipment = equipments.find { it.id == report.equipmentId }
                    equipment?.registrationNumber?.contains(searchQuery, ignoreCase = true) == true
                }
            }
        }
        else -> emptyList()
    }

    val maintenanceCount = when (val state = reportState) {
        is ReportUiState.UserReports -> state.reports.count { it.status == "in_maintenance" }
        else -> 0
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
                    text = "Mis Reportes",
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar Equipo por N.R.", fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE53935)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "$maintenanceCount",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Equipos en mantenimiento",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            Text(
                text = "Todos los equipos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            when (val state = reportState) {
                is ReportUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ReportUiState.UserReports -> {
                    if (filteredReports.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isBlank())
                                    "No tienes reportes activos"
                                else
                                    "No se encontraron equipos",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredReports) { report ->
                                val equipment = equipments.find { it.id == report.equipmentId }
                                if (equipment != null) {
                                    ReportEquipmentCard(
                                        equipmentName = equipment.name,
                                        equipmentLocation = equipment.physicalLocation,
                                        registrationNumber = equipment.registrationNumber,
                                        photoUrl = equipment.photoUrl,
                                        status = report.status,
                                        onClick = {
                                            onNavigateToEquipmentDetail(
                                                equipment.id,
                                                equipment.registryId
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                is ReportUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun ReportEquipmentCard(
    equipmentName: String,
    equipmentLocation: String,
    registrationNumber: String,
    photoUrl: String,
    status: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                if (photoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = equipmentName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.sunny),
                        contentDescription = equipmentName,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = equipmentName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = equipmentLocation,
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "N.R: $registrationNumber",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        when (status) {
                            "in_maintenance" -> Color(0xFFFFA726)
                            "pending" -> Color(0xFFE53935)
                            else -> Color.Gray
                        }
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyReportsScreenPreview() {
    BIOEQUIPDESTheme {
        MyReportsScreen()
    }
}