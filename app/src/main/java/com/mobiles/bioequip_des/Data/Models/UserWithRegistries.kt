package com.mobiles.bioequip_des.Data.Models

data class UserWithRegistries(
    val user: User,
    val registries: List<Registry> = emptyList(),
    val activeRegistry: Registry? = null
)