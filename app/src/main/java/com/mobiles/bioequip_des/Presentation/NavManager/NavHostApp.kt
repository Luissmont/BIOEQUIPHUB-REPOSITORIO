package com.mobiles.bioequip_des.Presentation.NavManager

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mobiles.bioequip_des.Presentation.Views.Auth.LoginScreen
import com.mobiles.bioequip_des.Presentation.Views.Auth.RegisterScreen
import com.mobiles.bioequip_des.Presentation.Views.Auth.SplashScreen
import com.mobiles.bioequip_des.Presentation.Views.Auth.WelcomeScreen

@Composable
fun NavHostApp(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavRoute.Splash.route
    ) {
        composable(route = NavRoute.Splash.route) {
            SplashScreen(
                onNavigateToWelcome = {
                    navController.navigate(NavRoute.Welcome.route) {
                        popUpTo(NavRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = NavRoute.Welcome.route) {
            WelcomeScreen(
                onNavigateToRegister = {
                    navController.navigate(NavRoute.Register.route)
                },
                onNavigateToLogin = {
                    navController.navigate(NavRoute.Login.route)
                }
            )
        }

        composable(route = NavRoute.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    // Por ahora regresa al Welcome, después irá al MainContainer
                    navController.popBackStack()
                }
            )
        }

        composable(route = NavRoute.Login.route) {
            LoginScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    navController.popBackStack()
                }
            )
        }

    }
}