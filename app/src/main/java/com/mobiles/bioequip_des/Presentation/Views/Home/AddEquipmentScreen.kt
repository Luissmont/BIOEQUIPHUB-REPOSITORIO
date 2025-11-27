package com.mobiles.bioequip_des.Presentation.Views.Home

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
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
import java.io.File
import android.content.ContentResolver
import java.io.FileOutputStream

private fun copyUriToFile(context: Context, uri: Uri): Uri? {
    return try {
        val contentResolver: ContentResolver = context.contentResolver
        val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")

        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEquipmentScreen(
    onNavigateBack: () -> Unit = {},
    onEquipmentAdded: () -> Unit = {},
    inventoryViewModel: InventoryViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser

    var name by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }
    var registrationNumber by remember { mutableStateOf("") }
    var inventoryNumber by remember { mutableStateOf("") }
    var manufacturerNumber by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var budgetItem by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var physicalLocation by remember { mutableStateOf("") }
    var serviceArea by remember { mutableStateOf("") }
    var classification by remember { mutableStateOf("") }
    var medicalUnit by remember { mutableStateOf("") }

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val uiState by inventoryViewModel.uiState.collectAsState()
    val userState by userViewModel.uiState.collectAsState()

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            userViewModel.loadUserData(uid)
        }
    }

    val tempPhotoUri = remember {
        val file = File(context.externalCacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri = tempPhotoUri
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { photoUri = it }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(tempPhotoUri)
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is InventoryUiState.EquipmentAdded -> {
                inventoryViewModel.resetState()
                onEquipmentAdded()
            }
            else -> {}
        }
    }

    val isFormValid = name.isNotBlank() &&
            registrationNumber.isNotBlank() &&
            inventoryNumber.isNotBlank()

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
                    text = "Agregar equipo",
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
            if (photoUri != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Foto del equipo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFA726)
                    )
                ) {
                    Text("Tomar Foto", fontSize = 14.sp)
                }

                Button(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFA726)
                    )
                ) {
                    Text("Desde Galería", fontSize = 14.sp)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            EquipmentTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nombre del equipo:"
            )

            EquipmentTextField(
                value = serialNumber,
                onValueChange = { serialNumber = it },
                label = "Número de serie:"
            )

            EquipmentTextField(
                value = registrationNumber,
                onValueChange = { registrationNumber = it },
                label = "Número de registro:"
            )

            EquipmentTextField(
                value = inventoryNumber,
                onValueChange = { inventoryNumber = it },
                label = "Número de inventario:"
            )

            EquipmentTextField(
                value = manufacturerNumber,
                onValueChange = { manufacturerNumber = it },
                label = "Número del fabricante:"
            )

            EquipmentTextField(
                value = brand,
                onValueChange = { brand = it },
                label = "Marca:"
            )

            EquipmentTextField(
                value = model,
                onValueChange = { model = it },
                label = "Modelo:"
            )

            EquipmentTextField(
                value = budgetItem,
                onValueChange = { budgetItem = it },
                label = "Partida/Subpartida:"
            )

            EquipmentTextField(
                value = series,
                onValueChange = { series = it },
                label = "Serie:"
            )

            EquipmentTextField(
                value = physicalLocation,
                onValueChange = { physicalLocation = it },
                label = "Localización Física del bien:"
            )

            EquipmentTextField(
                value = serviceArea,
                onValueChange = { serviceArea = it },
                label = "Servicio al que pertenece:"
            )

            EquipmentTextField(
                value = classification,
                onValueChange = { classification = it },
                label = "Clasificación:"
            )

            EquipmentTextField(
                value = medicalUnit,
                onValueChange = { medicalUnit = it },
                label = "Unidad Médica:"
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState is InventoryUiState.Error) {
                Text(
                    text = (uiState as InventoryUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Button(
                onClick = {
                    if (userState is UserUiState.Success) {
                        val activeRegistryId = (userState as UserUiState.Success).data.activeRegistry?.id
                        val userId = currentUser?.uid

                        if (activeRegistryId != null && userId != null) {
                            val equipment = Equipment(
                                registryId = activeRegistryId,
                                name = name,
                                serialNumber = serialNumber,
                                registrationNumber = registrationNumber,
                                inventoryNumber = inventoryNumber,
                                manufacturerNumber = manufacturerNumber,
                                brand = brand,
                                model = model,
                                budgetItem = budgetItem,
                                series = series,
                                physicalLocation = physicalLocation,
                                serviceArea = serviceArea,
                                classification = classification,
                                medicalUnit = medicalUnit,
                                createdBy = userId
                            )


                            inventoryViewModel.addEquipment(equipment, photoUri, context)
                        }
                    }
                },
                enabled = isFormValid && uiState !is InventoryUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    disabledContainerColor = Color.Gray
                )
            ) {
                if (uiState is InventoryUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Guardar", fontSize = 16.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EquipmentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5)
            ),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddEquipmentScreenPreview() {
    BIOEQUIPDESTheme {
        AddEquipmentScreen()
    }
}