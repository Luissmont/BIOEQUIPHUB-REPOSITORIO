package com.mobiles.bioequip_des.Presentation.Views.Auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobiles.bioequip_des.R
import com.mobiles.bioequip_des.Presentation.ui.theme.BIOEQUIPDESTheme
import com.mobiles.bioequip_des.Presentation.ui.theme.BioequipTeal
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToWelcome: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        delay(2000)
        onNavigateToWelcome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BioequipTeal),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Image(
                painter = painterResource(id = R.drawable.logo_bioequiphub),
                contentDescription = "Logo BioEquip",
                modifier = Modifier.size(250.dp)
            )
            Spacer(modifier = Modifier.height(90.dp))
            Text(
                text = "Bioequip",
                color = Color.Black,
                style = MaterialTheme.typography.headlineLarge

            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    BIOEQUIPDESTheme {
        SplashScreen(
            onNavigateToWelcome = {}
        )
    }
}