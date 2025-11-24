package com.mobiles.bioequip_des.Presentation.NavManager

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mobiles.bioequip_des.Presentation.Views.Auth.LoginScreen
import com.mobiles.bioequip_des.Presentation.Views.Auth.RegisterScreen
import com.mobiles.bioequip_des.Presentation.Views.Auth.SplashScreen
import com.mobiles.bioequip_des.Presentation.Views.Auth.WelcomeScreen
import com.mobiles.bioequip_des.Presentation.Views.Auth.RegistrationSuccessScreen
import com.mobiles.bioequip_des.Presentation.Views.Registry.JoinRegistryScreen
import com.mobiles.bioequip_des.Presentation.Views.Registry.CreateRegistryScreen
import com.mobiles.bioequip_des.Presentation.Views.Main.MainAppScreen


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
                },
                onNavigateToRegistrationSuccess = {
                    navController.navigate(NavRoute.RegistrationSuccess.route) {
                        popUpTo(NavRoute.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(NavRoute.MainContainer.route){
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
                    navController.navigate(NavRoute.RegistrationSuccess.route){
                        popUpTo(NavRoute.Register.route){inclusive = true}
                    }
                }
            )
        }

        composable(route = NavRoute.RegistrationSuccess.route) {
            RegistrationSuccessScreen(
                onNavigateToCreateRegistry = {
                    navController.navigate(NavRoute.CreateRegistry.route)
                },
                onNavigateToJoinRegistry = {
                    navController.navigate(NavRoute.JoinRegistry.route)
                }
            )
        }

        composable(route = NavRoute.CreateRegistry.route) {
            CreateRegistryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCreateSuccess = {
                    navController.navigate(NavRoute.MainContainer.route) {
                        popUpTo(NavRoute.RegistrationSuccess.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = NavRoute.Login.route) {
            LoginScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    navController.navigate(NavRoute.MainContainer.route) {
                        popUpTo(NavRoute.Welcome.route){ inclusive = true }
                    }
                }
            )
        }

        composable(route = NavRoute.JoinRegistry.route) {
            JoinRegistryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onJoinSuccess = {
                    navController.navigate(NavRoute.MainContainer.route) {
                        popUpTo(NavRoute.RegistrationSuccess.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = NavRoute.MainContainer.route) {
            MainAppScreen(
                onLogout = {
                    navController.navigate(NavRoute.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToCreateRegistry = {
                    navController.navigate(NavRoute.CreateRegistry.route)
                },
                onNavigateToJoinRegistry = {
                    navController.navigate(NavRoute.JoinRegistry.route)
                }
            )
        }

    }
}