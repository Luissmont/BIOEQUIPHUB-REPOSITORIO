package com.mobiles.bioequip_des.Presentation.Views.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.mobiles.bioequip_des.R
import com.mobiles.bioequip_des.Data.Models.Equipment
import com.mobiles.bioequip_des.Presentation.ViewModel.InventoryUiState
import com.mobiles.bioequip_des.Presentation.ViewModel.InventoryViewModel
import com.mobiles.bioequip_des.Presentation.ui.theme.BIOEQUIPDESTheme
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun EquipmentDetailScreen(
    equipmentId: String,
    registryId: String,
    onNavigateBack: () -> Unit = {},
    onNavigateToGeneralInfo: (Equipment) -> Unit = {},
    inventoryViewModel: InventoryViewModel = viewModel()
) {
    val uiState by inventoryViewModel.uiState.collectAsState()

    LaunchedEffect(registryId) {
        inventoryViewModel.loadEquipments(registryId)
    }

    val equipment = when (val state = uiState) {
        is InventoryUiState.Success -> {
            state.equipments.find { it.id == equipmentId }
        }
        else -> null
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
                    text = equipment?.name ?: "Equipo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        if (equipment != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Card(
                    modifier = Modifier
                        .size(280.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4A90A4))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
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
                                modifier = Modifier.size(120.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "N.R.: ${equipment.registrationNumber}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                OptionCard(
                    icon = R.drawable.carpeta,
                    title = "Información General",
                    description = "Aquí podrás encontrar cualquier información de registro, serie, marca u otra índole de este equipo",
                    onClick = { onNavigateToGeneralInfo(equipment) }
                )

                OptionCard(
                    icon = R.drawable.reload,
                    title = "Historial de Intervenciones",
                    description = "Apartado para que veas todas las intervenciones que ha tenido el equipo durante su estancia en la clínica",
                    onClick = { /* TODO: Implementar después */ }
                )

                OptionCard(
                    icon = R.drawable.seguimiento,
                    title = "Tracking Actual",
                    description = "Aquí podrás ver el estado actual de tu equipo y cómo se lleva su proceso en caso de tener un servicio",
                    onClick = { /* TODO: Implementar después */ }
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {  },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    )
                ) {
                    Text("Reportar", fontSize = 18.sp, color = Color.White)
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun OptionCard(
    icon: Int,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    modifier = Modifier.size(32.dp),
                    tint = Color(0xFF4A90A4)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EquipmentDetailScreenPreview() {
    BIOEQUIPDESTheme {
        EquipmentDetailScreen(
            equipmentId = "test",
            registryId = "test"
        )
    }
}