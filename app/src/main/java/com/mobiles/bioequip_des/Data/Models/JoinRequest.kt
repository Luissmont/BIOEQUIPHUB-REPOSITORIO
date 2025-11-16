package com.mobiles.bioequip_des.Data.Models

data class JoinRequest(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val registryId: String = "",
    val registryName: String = "",
    val status: String = "pending",
    val createdAt: Long = System.currentTimeMillis()
)