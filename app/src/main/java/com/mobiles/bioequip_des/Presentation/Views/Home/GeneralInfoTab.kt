package com.mobiles.bioequip_des.Presentation.Views.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobiles.bioequip_des.R
import com.mobiles.bioequip_des.Data.Models.Equipment
import com.mobiles.bioequip_des.Presentation.ui.theme.BIOEQUIPDESTheme

@Composable
fun GeneralInfoTab(
    equipment: Equipment,
    onNavigateBack: () -> Unit = {}
) {
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
                    text = "Información General",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoField(label = "Número de Serie:", value = equipment.serialNumber)
            InfoField(label = "Número de Registro:", value = equipment.registrationNumber)
            InfoField(label = "Número de Inventario:", value = equipment.inventoryNumber)
            InfoField(label = "Número de Fabricante:", value = equipment.manufacturerNumber)
            InfoField(label = "Marca:", value = equipment.brand)
            InfoField(label = "Modelo:", value = equipment.model)
            InfoField(label = "Partida/Subpartida:", value = equipment.budgetItem)
            InfoField(label = "Serie:", value = equipment.series)
            InfoField(label = "Localización Física del bien:", value = equipment.physicalLocation)
            InfoField(label = "Servicio al que pertenece:", value = equipment.serviceArea)
            InfoField(label = "Clasificación:", value = equipment.classification)
            InfoField(label = "Unidad Médica:", value = equipment.medicalUnit)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoField(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF5A5A5A))
        ) {
            Text(
                text = value.ifEmpty { "No disponible" },
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GeneralInfoTabPreview() {
    BIOEQUIPDESTheme {
        GeneralInfoTab(
            equipment = Equipment(
                name = "Electrocardiógrafo",
                serialNumber = "281322323132323232",
                registrationNumber = "25282309032",
                inventoryNumber = "INV-001",
                manufacturerNumber = "MAN-123",
                brand = "Phillips",
                model = "ECG-2000",
                budgetItem = "35701-0002",
                series = "A123",
                physicalLocation = "Piso 2, Sala 4",
                serviceArea = "Cardiología",
                classification = "Clase II",
                medicalUnit = "Hospital General"
            )
        )
    }
}