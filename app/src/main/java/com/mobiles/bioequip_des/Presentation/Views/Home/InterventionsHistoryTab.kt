package com.mobiles.bioequip_des.Presentation.Views.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.mobiles.bioequip_des.R
import com.mobiles.bioequip_des.Data.Models.Equipment
import com.mobiles.bioequip_des.Data.Models.Report
import com.mobiles.bioequip_des.Presentation.ViewModel.ReportUiState
import com.mobiles.bioequip_des.Presentation.ViewModel.ReportViewModel
import com.mobiles.bioequip_des.Presentation.ui.theme.BIOEQUIPDESTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InterventionsHistoryTab(
    equipment: Equipment,
    onNavigateBack: () -> Unit = {},
    onNavigateToInterventionDetail: (Equipment, String) -> Unit = { _, _ -> },
    reportViewModel: ReportViewModel = viewModel()
) {
    val reportState by reportViewModel.uiState.collectAsState()

    LaunchedEffect(equipment.id) {
        reportViewModel.getInterventionsHistory(equipment.id)
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
                    text = "Historial de Intervenciones",
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
                    .size(100.dp)
                    .background(Color(0xFF00BCD4), CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.carpeta),
                    contentDescription = "Historial",
                    modifier = Modifier.size(50.dp),
                    tint = Color.White
                )
            }

            Text(
                text = "Historial de Intervenciones",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (val state = reportState) {
                is ReportUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ReportUiState.InterventionsHistory -> {
                    if (state.reports.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        ) {
                            Text(
                                text = "Este equipo no tiene intervenciones registradas",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(state.reports) { index, report ->
                                InterventionCard(
                                    interventionNumber = state.reports.size - index,
                                    report = report,
                                    onClick = {
                                        onNavigateToInterventionDetail(equipment, report.id)
                                    }
                                )
                            }
                        }
                    }
                }

                is ReportUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun InterventionCard(
    interventionNumber: Int,
    report: Report,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    val date = Date(report.createdAt)

    val interventionName = when (interventionNumber) {
        1 -> "Primer Mantenimiento"
        2 -> "Segundo Mantenimiento"
        3 -> "Tercer Mantenimiento"
        else -> "${interventionNumber}° Mantenimiento"
    }

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
                    .background(Color(0xFF4CAF50), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.carpeta),
                    contentDescription = "Intervention",
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = interventionName,
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InterventionsHistoryTabPreview() {
    BIOEQUIPDESTheme {
        InterventionsHistoryTab(
            equipment = Equipment(id = "test", name = "Test Equipment")
        )
    }
}