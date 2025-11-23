package com.mobiles.bioequip_des.Data.Repositories

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mobiles.bioequip_des.Data.Models.Registry
import kotlinx.coroutines.tasks.await

class RegistryRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun createRegistry(
        name: String,
        type: String,
        accessCode: String,
        adminUid: String
    ): Result<Registry> {
        return try {
            val existingRegistry = firestore.collection("registries")
                .whereEqualTo("accessCode", accessCode)
                .get()
                .await()

            if (!existingRegistry.isEmpty) {
                throw Exception("El código de acceso ya existe")
            }

            val registryRef = firestore.collection("registries").document()
            val registry = Registry(
                id = registryRef.id,
                name = name,
                type = type,
                accessCode = accessCode,
                adminUid = adminUid,
                members = listOf(adminUid)
            )

            registryRef.set(registry).await()

            firestore.collection("users")
                .document(adminUid)
                .update(
                    mapOf(
                        "registryIds" to FieldValue.arrayUnion(registry.id),
                        "activeRegistryId" to registry.id
                    )
                )
                .await()

            Result.success(registry)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinRegistry(
        userId: String,
        userName: String,
        userEmail: String,
        accessCode: String
    ): Result<Registry> {
        return try {
            val querySnapshot = firestore.collection("registries")
                .whereEqualTo("accessCode", accessCode)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                throw Exception("Código de acceso inválido")
            }

            val registryDoc = querySnapshot.documents[0]
            val registry = registryDoc.toObject(Registry::class.java)
                ?: throw Exception("Error al obtener registro")

            if (registry.members.contains(userId)) {
                throw Exception("Ya eres miembro de este registro")
            }

            firestore.collection("registries")
                .document(registry.id)
                .update("members", FieldValue.arrayUnion(userId))
                .await()

            firestore.collection("users")
                .document(userId)
                .update(
                    mapOf(
                        "registryIds" to FieldValue.arrayUnion(registry.id),
                        "activeRegistryId" to registry.id
                    )
                )
                .await()

            Result.success(registry)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRegistryById(registryId: String): Result<Registry> {
        return try {
            val doc = firestore.collection("registries")
                .document(registryId)
                .get()
                .await()

            val registry = doc.toObject(Registry::class.java)
                ?: throw Exception("Registro no encontrado")

            Result.success(registry)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}