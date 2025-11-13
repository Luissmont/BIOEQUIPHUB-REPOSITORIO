package com.mobiles.bioequip_des

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mobiles.bioequip_des.Presentation.Views.Auth.RegisterScreen
import com.mobiles.bioequip_des.Presentation.ui.theme.BIOEQUIPDESTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BIOEQUIPDESTheme {
                RegisterScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BIOEQUIPDESTheme {
        RegisterScreen()
    }
}