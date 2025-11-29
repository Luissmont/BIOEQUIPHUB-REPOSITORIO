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
import com.mobiles.bioequip_des.Data.Models.Equipment
import com.mobiles.bioequip_des.Presentation.ViewModel.InventoryUiState
import com.mobiles.bioequip_des.Presentation.ViewModel.InventoryViewModel
import com.mobiles.bioequip_des.Presentation.ViewModel.UserUiState
import com.mobiles.bioequip_des.Presentation.ViewModel.UserViewModel
import com.mobiles.bioequip_des.Presentation.ui.theme.BIOEQUIPDESTheme

enum class EquipmentFilter {
    ALL, DISPONIBLE, FUERA_SERVICIO, MANTENIMIENTO
}

@Composable
fun InventoryScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToDetail: (String, String) -> Unit = { _, _ -> },
    inventoryViewModel: InventoryViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val inventoryState by inventoryViewModel.uiState.collectAsState()
    val userState by userViewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(EquipmentFilter.ALL) }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            userViewModel.loadUserData(uid)
        }
    }

    LaunchedEffect(userState) {
        if (userState is UserUiState.Success) {
            val activeRegistryId = (userState as UserUiState.Success).data.activeRegistry?.id
            activeRegistryId?.let { registryId ->
                inventoryViewModel.loadEquipments(registryId)
            }
        }
    }

    val filteredEquipments = when (inventoryState) {
        is InventoryUiState.Success -> {
            val allEquipments = (inventoryState as InventoryUiState.Success).equipments
            when (selectedFilter) {
                EquipmentFilter.ALL -> allEquipments
                EquipmentFilter.DISPONIBLE -> allEquipments.filter { it.status == "disponible" }
                EquipmentFilter.FUERA_SERVICIO -> allEquipments.filter { it.status == "fuera_servicio" }
                EquipmentFilter.MANTENIMIENTO -> allEquipments.filter { it.status == "mantenimiento" }
            }
        }
        else -> emptyList()
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
                    text = "Inventario de Equipos",
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterCard(
                    count = filteredEquipments.count { it.status == "disponible" },
                    label = "Equipos\nDisponibles",
                    color = Color(0xFF4CAF50),
                    isSelected = selectedFilter == EquipmentFilter.DISPONIBLE,
                    onClick = {
                        selectedFilter = if (selectedFilter == EquipmentFilter.DISPONIBLE)
                            EquipmentFilter.ALL else EquipmentFilter.DISPONIBLE
                    },
                    modifier = Modifier.weight(1f)
                )

                FilterCard(
                    count = filteredEquipments.count { it.status == "fuera_servicio" },
                    label = "Fuera de servicio",
                    color = Color(0xFFE53935),
                    isSelected = selectedFilter == EquipmentFilter.FUERA_SERVICIO,
                    onClick = {
                        selectedFilter = if (selectedFilter == EquipmentFilter.FUERA_SERVICIO)
                            EquipmentFilter.ALL else EquipmentFilter.FUERA_SERVICIO
                    },
                    modifier = Modifier.weight(1f)
                )

                FilterCard(
                    count = filteredEquipments.count { it.status == "mantenimiento" },
                    label = "En mantenimiento",
                    color = Color(0xFFFDD835),
                    isSelected = selectedFilter == EquipmentFilter.MANTENIMIENTO,
                    onClick = {
                        selectedFilter = if (selectedFilter == EquipmentFilter.MANTENIMIENTO)
                            EquipmentFilter.ALL else EquipmentFilter.MANTENIMIENTO
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF00BCD4)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "${filteredEquipments.size}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Equipos en total",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }

            Text(
                text = "Todos los equipos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            when (inventoryState) {
                is InventoryUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is InventoryUiState.Success -> {
                    if (filteredEquipments.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay equipos disponibles",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredEquipments) { equipment ->
                                EquipmentCard(
                                    equipment = equipment,
                                    onClick = {
                                        onNavigateToDetail(equipment.id, equipment.registryId)
                                    }
                                )
                            }
                        }
                    }
                }

                is InventoryUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (inventoryState as InventoryUiState.Error).message,
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
private fun FilterCard(
    count: Int,
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color else Color.LightGray
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$count",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.Black,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun EquipmentCard(
    equipment: Equipment,
    onClick: () -> Unit = {}
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
                if (equipment.photoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = equipment.photoUrl,
                        contentDescription = equipment.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.sunny),
                        contentDescription = equipment.name,
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
                    text = equipment.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = equipment.physicalLocation,
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "N.R: ${equipment.registrationNumber}",
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
                        when (equipment.status) {
                            "disponible" -> Color(0xFF4CAF50)
                            "fuera_servicio" -> Color(0xFFE53935)
                            "mantenimiento" -> Color(0xFFFDD835)
                            else -> Color.Gray
                        }
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InventoryScreenPreview() {
    BIOEQUIPDESTheme {
        InventoryScreen()
    }
}