package com.mobiles.bioequip_des.Presentation.Views.Main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mobiles.bioequip_des.Presentation.NavManager.NavRoute
import com.mobiles.bioequip_des.Presentation.Views.Home.HomeScreen
import com.mobiles.bioequip_des.Presentation.Views.Profile.ProfileScreen
import com.mobiles.bioequip_des.Presentation.Views.Profile.SettingsScreen
import com.mobiles.bioequip_des.Presentation.ui.theme.BioequipTeal

@Composable
fun MainAppScreen(
    onLogout: () -> Unit = {},
    onNavigateToCreateRegistry: () -> Unit = {},
    onNavigateToJoinRegistry: () -> Unit = {}
) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Profile,
        BottomNavItem.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = BioequipTeal
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 10.sp) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.Black,
                            unselectedTextColor = Color.Black,
                            indicatorColor = Color(0xFF4A90A4)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoute.Profile.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoute.Home.route) {
                HomeScreen()
            }
            composable(NavRoute.Profile.route) {
                ProfileScreen(
                    onNavigateToCreateRegistry = onNavigateToCreateRegistry,
                    onNavigateToJoinRegistry = onNavigateToJoinRegistry
                )
            }
            composable(NavRoute.Settings.route) {
                SettingsScreen(
                    onLogout = onLogout
                )
            }
        }
    }
}