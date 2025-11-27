package com.mobiles.bioequip_des.Presentation.NavManager

sealed class NavRoute(val route: String) {
    object Splash : NavRoute("splash")
    object Welcome : NavRoute("welcome")
    object Login : NavRoute("login")
    object Register : NavRoute("register")
    object RegistrationSuccess : NavRoute("registration_success")

    object JoinRegistry : NavRoute("join_registry")

    object CreateRegistry : NavRoute("create_registry")


    object MainContainer : NavRoute("main_container")
    object Home : NavRoute("home")
    object Inventory : NavRoute("inventory")
    object AddEquipment : NavRoute("add_equipment")
    object EquipmentAddedSuccess : NavRoute("equipment_added_success")
    object Registry : NavRoute("registry")
    object Profile : NavRoute("profile")

    object EquipmentDetail : NavRoute("equipment_detail/{equipmentId}") {
        fun createRoute(equipmentId: String) = "equipment_detail/$equipmentId"
    }
    object ReportFault : NavRoute("report_fault")
    object Settings : NavRoute("settings")
}