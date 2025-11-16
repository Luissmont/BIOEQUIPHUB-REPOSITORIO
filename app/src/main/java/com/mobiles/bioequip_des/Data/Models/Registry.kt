package com.mobiles.bioequip_des.Data.Models

data class Registry(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val accessCode: String = "",
    val adminUid: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val members: List<String> = emptyList()
)