package com.mobiles.bioequip_des.Presentation.Views.Home

import android.Manifest
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
import com.mobiles.bioequip_des.Data.Models.MaintenanceUpdate
import com.mobiles.bioequip_des.Presentation.ViewModel.MaintenanceUiState
import com.mobiles.bioequip_des.Presentation.ViewModel.MaintenanceViewModel
import com.mobiles.bioequip_des.Presentation.ViewModel.UserUiState
import com.mobiles.bioequip_des.Presentation.ViewModel.UserViewModel
import com.mobiles.bioequip_des.Presentation.ui.theme.BIOEQUIPDESTheme
import java.io.File
import android.net.Uri

@Composable
fun AddMaintenanceUpdateScreen(
    equipment: Equipment,
    reportId: String,
    onNavigateBack: () -> Unit = {},
    onUpdateCreated: () -> Unit = {},
    maintenanceViewModel: MaintenanceViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser

    var updateName by remember { mutableStateOf("") }
    var changes by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf("") }
    var recommendations by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val maintenanceState by maintenanceViewModel.uiState.collectAsState()
    val userState by userViewModel.uiState.collectAsState()

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            userViewModel.loadUserData(uid)
        }
    }

    LaunchedEffect(maintenanceState) {
        if (maintenanceState is MaintenanceUiState.UpdateCreated) {
            maintenanceViewModel.resetState()
            onUpdateCreated()
        }
    }

    val tempPhotoUri = remember {
        val file = File(context.externalCacheDir, "temp_maintenance_${System.currentTimeMillis()}.jpg")
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

    val isFormValid = updateName.isNotBlank() &&
            changes.isNotBlank() &&
            progress.isNotBlank() &&
            recommendations.isNotBlank()

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
                    text = "Actualizar Equipo",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UpdateTextField(
                value = updateName,
                onValueChange = { updateName = it },
                label = "Nombre de la actualización:",
                placeholder = "Primera actualización"
            )

            UpdateTextField(
                value = changes,
                onValueChange = { changes = it },
                label = "Cambios nuevos:",
                placeholder = "El equipo actualmente fue intervenido de la manera correcta...",
                minLines = 3
            )

            UpdateTextField(
                value = progress,
                onValueChange = { progress = it },
                label = "Avance del mantenimiento:",
                placeholder = "Se prevee que el equipo cuente con el 80 porciento...",
                minLines = 3
            )

            UpdateTextField(
                value = recommendations,
                onValueChange = { recommendations = it },
                label = "Recomendaciones:",
                placeholder = "Se recomienda que de momento no se mueva de su lugar...",
                minLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (photoUri != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Foto de actualización",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA726)
                )
            ) {
                Text("Agregar Foto", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (maintenanceState is MaintenanceUiState.Error) {
                Text(
                    text = (maintenanceState as MaintenanceUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    if (userState is UserUiState.Success) {
                        val user = (userState as UserUiState.Success).data.user

                        val update = MaintenanceUpdate(
                            reportId = reportId,
                            equipmentId = equipment.id,
                            registryId = equipment.registryId,
                            updateName = updateName,
                            changes = changes,
                            progress = progress,
                            recommendations = recommendations,
                            createdBy = currentUser?.uid ?: "",
                            creatorName = user.name,
                            creatorRole = user.role
                        )

                        maintenanceViewModel.createUpdate(update, photoUri, context)
                    }
                },
                enabled = isFormValid && maintenanceState !is MaintenanceUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    disabledContainerColor = Color.Gray
                )
            ) {
                if (maintenanceState is MaintenanceUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Publicar", fontSize = 18.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun UpdateTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    minLines: Int = 1
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, fontSize = 14.sp, color = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5)
            ),
            minLines = minLines,
            maxLines = if (minLines > 1) 6 else 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddMaintenanceUpdateScreenPreview() {
    BIOEQUIPDESTheme {
        AddMaintenanceUpdateScreen(
            equipment = Equipment(id = "test", name = "Test"),
            reportId = "test"
        )
    }
}