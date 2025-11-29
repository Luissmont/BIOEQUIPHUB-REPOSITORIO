package com.mobiles.bioequip_des.Data.Models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Equipment(
    val id: String = "",
    val registryId: String = "",
    val name: String = "",
    val serialNumber: String = "",
    val registrationNumber: String = "",
    val inventoryNumber: String = "",
    val manufacturerNumber: String = "",
    val brand: String = "",
    val model: String = "",
    val budgetItem: String = "",
    val series: String = "",
    val physicalLocation: String = "",
    val serviceArea: String = "",
    val classification: String = "",
    val medicalUnit: String = "",
    val photoUrl: String = "",
    val status: String = "disponible",
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: String = ""
) : Parcelable