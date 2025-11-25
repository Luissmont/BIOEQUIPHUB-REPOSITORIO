package com.mobiles.bioequip_des.Data.Models

data class User(
    val uid: String = "",
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val role: String = "",
    val registryIds: List<String> = emptyList(),
    val activeRegistryId:String? = null,
    val createdAt: Long = System.currentTimeMillis()
)