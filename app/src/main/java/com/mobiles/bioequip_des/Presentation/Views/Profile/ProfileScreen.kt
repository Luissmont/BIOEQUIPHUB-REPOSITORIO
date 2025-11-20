package com.mobiles.bioequip_des.Presentation.Views.Profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.google.firebase.auth.FirebaseAuth
import com.mobiles.bioequip_des.R
import com.mobiles.bioequip_des.Presentation.ViewModel.UserUiState
import com.mobiles.bioequip_des.Presentation.ViewModel.UserViewModel
import com.mobiles.bioequip_des.Presentation.ui.theme.BIOEQUIPDESTheme
import com.mobiles.bioequip_des.Presentation.ui.theme.BioequipTeal

@Composable
fun ProfileScreen(
    onNavigateToCreateRegistry: () -> Unit = {},
    onNavigateToJoinRegistry: () -> Unit = {},
    userViewModel: UserViewModel = viewModel()
) {
    val uiState by userViewModel.uiState.collectAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { uid ->
            userViewModel.loadUserData(uid)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.fondomed2),
                contentDescription = "Header background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            when (val state = uiState) {
                is UserUiState.Success -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(start = 24.dp, bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sunny),
                            contentDescription = "User icon",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(30.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "${state.data.user.name} ${state.data.user.lastName}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = state.data.user.role,
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
                else -> {}
            }
        }

        when (val state = uiState) {
            is UserUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BioequipTeal)
                }
            }

            is UserUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.data.registries) { registry ->
                        RegistryCard(
                            registryName = registry.name,
                            isActive = state.data.activeRegistry?.id == registry.id,
                            onClick = {
                                currentUser?.uid?.let { uid ->
                                    userViewModel.setActiveRegistry(uid, registry.id)
                                }
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onNavigateToJoinRegistry,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF6B9D)
                            )
                        ) {
                            Text("AGREGAR UNIDAD", fontSize = 14.sp, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onNavigateToCreateRegistry,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD81B60)
                            )
                        ) {
                            Text("CREAR UNIDAD", fontSize = 14.sp, color = Color.White)
                        }
                    }
                }
            }

            is UserUiState.Error -> {
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
