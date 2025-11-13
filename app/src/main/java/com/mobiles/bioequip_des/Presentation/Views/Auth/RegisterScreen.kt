package com.mobiles.bioequip_des.Presentation.Views.Auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mobiles.bioequip_des.Presentation.ui.theme.BIOEQUIPDESTheme

@Composable
fun RegisterScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("PANTALLA DE REGISTRO EN CONSTRUCCIÓN")
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    BIOEQUIPDESTheme {
        RegisterScreen()
    }
}