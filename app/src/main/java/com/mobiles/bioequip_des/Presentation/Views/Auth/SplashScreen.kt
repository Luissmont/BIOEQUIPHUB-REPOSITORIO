package com.mobiles.bioequip_des.Presentation.Views.Auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobiles.bioequip_des.R
import com.mobiles.bioequip_des.Presentation.ViewModel.AuthViewModel
import com.mobiles.bioequip_des.Presentation.ui.theme.BIOEQUIPDESTheme
import com.mobiles.bioequip_des.Presentation.ui.theme.BioequipTeal
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToWelcome: () -> Unit = {},
    onNavigateToMain: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        delay(2000)

        if (authViewModel.checkUserSession()) {
            onNavigateToMain()
        } else {
            onNavigateToWelcome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BioequipTeal),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.sunny),
            contentDescription = "Logo BioEquip",
            modifier = Modifier.size(250.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    BIOEQUIPDESTheme {
        SplashScreen()
    }
}