package com.mobiles.bioequip_des.Presentation.Views.Main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.mobiles.bioequip_des.Presentation.NavManager.NavRoute

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem(NavRoute.Home.route, Icons.Default.Home, "HOME")
    object Profile : BottomNavItem(NavRoute.Profile.route, Icons.Default.Person, "PERFIL")
    object Settings : BottomNavItem(NavRoute.Settings.route, Icons.Default.Settings, "AJUSTES")
}