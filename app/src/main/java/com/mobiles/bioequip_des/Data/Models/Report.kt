package com.mobiles.bioequip_des.Data.Models;

data class Report(
    val id: String = "",
    val equipmentId: String = "",
    val registryId: String = "",
    val reportedBy: String = "",
    val reporterName: String = "",
    val reporterRole: String = "",
    val reason: String = "",
    val status: String = "pending",
    val createdAt: Long = System.currentTimeMillis(),
    val startedMaintenanceAt: Long? = null,
    val startedMaintenanceBy: String? = null,
    val resolvedAt: Long? = null,
    val resolvedBy: String? = null
)