package com.mobiles.bioequip_des.Data.Repositories

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mobiles.bioequip_des.Data.Models.Equipment
import kotlinx.coroutines.tasks.await
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InventoryRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val client = OkHttpClient()

    private val imgbbApiKey = "888e44ec20adacd66c87a01513b336bf"

    suspend fun addEquipment(
        equipment: Equipment,
        photoUri: Uri?,
        context: Context
    ): Result<Equipment> {
        return try {
            val equipmentRef = firestore.collection("equipment").document()
            var updatedEquipment = equipment.copy(id = equipmentRef.id)

            if (photoUri != null) {
                val photoUrl = uploadToImgBB(photoUri, context)
                updatedEquipment = updatedEquipment.copy(photoUrl = photoUrl)
            }

            equipmentRef.set(updatedEquipment).await()

            Result.success(updatedEquipment)
        } catch (e: Exception) {
            Log.e("InventoryRepository", "Error adding equipment", e)
            Result.failure(e)
        }
    }


    private suspend fun uploadToImgBB(photoUri: Uri, context: Context): String {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("InventoryRepository", "Starting upload for URI: $photoUri")

                val inputStream = context.contentResolver.openInputStream(photoUri)
                    ?: throw Exception("Cannot open input stream")

                val bytes = ByteArrayOutputStream().use { output ->
                    inputStream.copyTo(output)
                    output.toByteArray()
                }
                inputStream.close()

                Log.d("InventoryRepository", "Image size: ${bytes.size} bytes")

                if (bytes.size > 32 * 1024 * 1024) {
                    throw Exception("Image too large: ${bytes.size} bytes")
                }

                val base64Image = Base64.encodeToString(bytes, Base64.NO_WRAP)
                Log.d("InventoryRepository", "Base64 length: ${base64Image.length}")

                val formBody = FormBody.Builder()
                    .add("key", imgbbApiKey)
                    .add("image", base64Image)
                    .add("name", "equipment_${System.currentTimeMillis()}")
                    .build()

                val request = Request.Builder()
                    .url("https://api.imgbb.com/1/upload")
                    .post(formBody)
                    .build()

                Log.d("InventoryRepository", "Sending request to ImgBB...")

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: throw Exception("Empty response")

                Log.d("InventoryRepository", "Response code: ${response.code}")
                Log.d("InventoryRepository", "Response body: $responseBody")

                if (!response.isSuccessful) {
                    throw Exception("Upload failed: ${response.code} - $responseBody")
                }

                val jsonObject = JSONObject(responseBody)

                if (!jsonObject.has("success") || !jsonObject.getBoolean("success")) {
                    val errorMsg = if (jsonObject.has("error")) {
                        jsonObject.getJSONObject("error").optString("message", "Unknown error")
                    } else {
                        "Upload failed"
                    }
                    throw Exception(errorMsg)
                }

                val dataObject = jsonObject.getJSONObject("data")
                val imageUrl = dataObject.getString("url")

                Log.d("InventoryRepository", "Upload successful! URL: $imageUrl")

                imageUrl
            } catch (e: Exception) {
                Log.e("InventoryRepository", "Error uploading to ImgBB: ${e.message}", e)
                throw e
            }
        }
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