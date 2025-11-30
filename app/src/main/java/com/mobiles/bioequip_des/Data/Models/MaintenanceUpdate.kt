package com.mobiles.bioequip_des.Data.Models

data class MaintenanceUpdate(
    val id: String = "",
    val reportId: String = "",
    val equipmentId: String = "",
    val registryId: String = "",
    val updateName: String = "",
    val changes: String = "",
    val progress: String = "",
    val recommendations: String = "",
    val photoUrl: String = "",
    val createdBy: String = "",
    val creatorName: String = "",
    val creatorRole: String = "",
    val createdAt: Long = System.currentTimeMillis()
)