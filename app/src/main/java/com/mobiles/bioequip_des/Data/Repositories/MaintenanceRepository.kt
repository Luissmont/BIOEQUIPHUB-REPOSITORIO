package com.mobiles.bioequip_des.Data.Repositories

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mobiles.bioequip_des.Data.Models.MaintenanceUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class MaintenanceRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val client = OkHttpClient()
    private val imgbbApiKey = "888e44ec20adacd66c87a01513b336bf"

    suspend fun createMaintenanceUpdate(
        update: MaintenanceUpdate,
        photoUri: Uri?,
        context: Context
    ): Result<MaintenanceUpdate> {
        return try {
            val updateRef = firestore.collection("maintenance_updates").document()
            var newUpdate = update.copy(id = updateRef.id)

            if (photoUri != null) {
                val photoUrl = uploadToImgBB(photoUri, context)
                newUpdate = newUpdate.copy(photoUrl = photoUrl)
            }

            updateRef.set(newUpdate).await()

            Result.success(newUpdate)
        } catch (e: Exception) {
            Log.e("MaintenanceRepository", "Error creating update", e)
            Result.failure(e)
        }
    }

    private suspend fun uploadToImgBB(photoUri: Uri, context: Context): String {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("MaintenanceRepository", "Starting upload for URI: $photoUri")

                val inputStream = context.contentResolver.openInputStream(photoUri)
                    ?: throw Exception("Cannot open input stream")

                val bytes = ByteArrayOutputStream().use { output ->
                    inputStream.copyTo(output)
                    output.toByteArray()
                }
                inputStream.close()

                Log.d("MaintenanceRepository", "Image size: ${bytes.size} bytes")

                if (bytes.size > 32 * 1024 * 1024) {
                    throw Exception("Image too large: ${bytes.size} bytes")
                }

                val base64Image = Base64.encodeToString(bytes, Base64.NO_WRAP)

                val formBody = FormBody.Builder()
                    .add("key", imgbbApiKey)
                    .add("image", base64Image)
                    .add("name", "maintenance_${System.currentTimeMillis()}")
                    .build()

                val request = Request.Builder()
                    .url("https://api.imgbb.com/1/upload")
                    .post(formBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: throw Exception("Empty response")

                if (!response.isSuccessful) {
                    throw Exception("Upload failed: ${response.code} - $responseBody")
                }

                val jsonObject = JSONObject(responseBody)

                if (!jsonObject.has("success") || !jsonObject.getBoolean("success")) {
                    throw Exception("Upload failed")
                }

                val dataObject = jsonObject.getJSONObject("data")
                val imageUrl = dataObject.getString("url")

                Log.d("MaintenanceRepository", "Upload successful! URL: $imageUrl")

                imageUrl
            } catch (e: Exception) {
                Log.e("MaintenanceRepository", "Error uploading to ImgBB: ${e.message}", e)
                throw e
            }
        }
    }


}