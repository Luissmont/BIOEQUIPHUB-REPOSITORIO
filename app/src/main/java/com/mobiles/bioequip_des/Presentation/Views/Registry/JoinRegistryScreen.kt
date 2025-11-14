package com.mobiles.bioequip_des.Presentation.Views.Registry

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobiles.bioequip_des.R
import com.mobiles.bioequip_des.Presentation.ui.theme.BIOEQUIPDESTheme
import androidx.compose.foundation.background

@Composable
fun JoinRegistryScreen(
    onNavigateBack: () -> Unit = {},
    onJoinSuccess: () -> Unit = {}
) {
    var code by remember { mutableStateOf("") }
    val isCodeValid = code.isNotBlank() && code.length >= 4

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(color = Color.White)

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.fondomed),
                contentDescription = "Patrón médico",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(24.dp)

        ) {
            Text(
                text = "Ingrese a un Registro",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Ingrese su código",
                fontSize = 16.sp,
                color = Color.DarkGray
            )

            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color(0xFFE8E8E8),
                    unfocusedContainerColor = Color(0xFFE8E8E8)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // TODO: Validar código con Firebase
                    if (isCodeValid) {
                        onJoinSuccess()
                    }
                },
                enabled = isCodeValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text("continuar", fontSize = 16.sp, color = Color.White)
            }

            Text(
                text = "Su solicitud para entrar al registro especificado será evaluada por el equipo encargado de administrar el registro. Cuando su petición sea aceptada recibirá una notificación del equipo",
                fontSize = 12.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JoinRegistryScreenPreview() {
    BIOEQUIPDESTheme {
        JoinRegistryScreen()
    }
}