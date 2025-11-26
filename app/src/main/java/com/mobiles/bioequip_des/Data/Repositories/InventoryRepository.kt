package com.mobiles.bioequip_des.Data.Repositories

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mobiles.bioequip_des.Data.Models.Equipment
import kotlinx.coroutines.tasks.await
import java.util.UUID

class InventoryRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    suspend fun addEquipment(
        equipment: Equipment,
        photoUri: Uri?
    ): Result<Equipment> {
        return try {
            val equipmentRef = firestore.collection("equipment").document()
            var updatedEquipment = equipment.copy(id = equipmentRef.id)


            if (photoUri != null) {
                val photoUrl = uploadPhoto(equipment.registryId, equipmentRef.id, photoUri)
                updatedEquipment = updatedEquipment.copy(photoUrl = photoUrl)
            }

            equipmentRef.set(updatedEquipment).await()

            Result.success(updatedEquipment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadPhoto(registryId: String, equipmentId: String, photoUri: Uri): String {
        val photoRef = storage.reference
            .child("equipments")
            .child(registryId)
            .child(equipmentId)
            .child("photo.jpg")

        photoRef.putFile(photoUri).await()
        return photoRef.downloadUrl.await().toString()
    }

    suspend fun getEquipmentsByRegistry(registryId: String): Result<List<Equipment>> {
        return try {
            val querySnapshot = firestore.collection("equipment")
                .whereEqualTo("registryId", registryId)
                .get()
                .await()

            val equipments = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Equipment::class.java)
            }

            Result.success(equipments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchByRegistrationNumber(registryId: String, query: String): Result<List<Equipment>> {
        return try {
            val querySnapshot = firestore.collection("equipment")
                .whereEqualTo("registryId", registryId)
                .whereEqualTo("registrationNumber", query)
                .get()
                .await()

            val equipments = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Equipment::class.java)
            }

            Result.success(equipments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}