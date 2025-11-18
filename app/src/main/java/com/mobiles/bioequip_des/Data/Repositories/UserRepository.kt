package com.mobiles.bioequip_des.Data.Repositories

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mobiles.bioequip_des.Data.Models.Registry
import com.mobiles.bioequip_des.Data.Models.User
import com.mobiles.bioequip_des.Data.Models.UserWithRegistries
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getUserWithRegistries(userId: String): Result<UserWithRegistries> {
        return try {
            val userDoc = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val user = userDoc.toObject(User::class.java)
                ?: throw Exception("Usuario no encontrado")

            val registryIds = userDoc.get("registryIds") as? List<String> ?: emptyList()
            val activeRegistryId = userDoc.getString("activeRegistryId")

            val registries = mutableListOf<Registry>()
            for (regId in registryIds) {
                val regDoc = firestore.collection("registries")
                    .document(regId)
                    .get()
                    .await()

                regDoc.toObject(Registry::class.java)?.let {
                    registries.add(it)
                }
            }

            val activeRegistry = registries.find { it.id == activeRegistryId }

            Result.success(UserWithRegistries(user, registries, activeRegistry))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setActiveRegistry(userId: String, registryId: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .update("activeRegistryId", registryId)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}