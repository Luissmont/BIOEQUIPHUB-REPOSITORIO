package com.mobiles.bioequip_des.Data.Repositories

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mobiles.bioequip_des.Data.Models.Report
import kotlinx.coroutines.tasks.await

class ReportRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun createReport(
        report: Report,
        equipmentId: String
    ): Result<Report> {
        return try {
            val reportRef = firestore.collection("reports").document()
            val newReport = report.copy(id = reportRef.id)

            reportRef.set(newReport).await()

            firestore.collection("equipment").document(equipmentId)
                .update("status", "fuera_servicio")
                .await()

            Result.success(newReport)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error creating report", e)
            Result.failure(e)
        }
    }

    suspend fun getActiveReport(equipmentId: String): Result<Report?> {
        return try {
            val querySnapshot = firestore.collection("reports")
                .whereEqualTo("equipmentId", equipmentId)
                .whereIn("status", listOf("pending", "in_maintenance"))
                .get()
                .await()

            val report = querySnapshot.documents.firstOrNull()?.toObject(Report::class.java)

            Result.success(report)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error getting active report", e)
            Result.failure(e)
        }
    }

    suspend fun startMaintenance(
        reportId: String,
        userId: String,
        equipmentId: String
    ): Result<Unit> {
        return try {
            firestore.collection("reports").document(reportId)
                .update(
                    mapOf(
                        "status" to "in_maintenance",
                        "startedMaintenanceAt" to System.currentTimeMillis(),
                        "startedMaintenanceBy" to userId
                    )
                )
                .await()

            firestore.collection("equipment").document(equipmentId)
                .update("status", "mantenimiento")
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error starting maintenance", e)
            Result.failure(e)
        }
    }

    suspend fun getEquipmentReports(equipmentId: String): Result<List<Report>> {
        return try {
            val querySnapshot = firestore.collection("reports")
                .whereEqualTo("equipmentId", equipmentId)
                .get()
                .await()

            val reports = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Report::class.java)
            }.sortedByDescending { it.createdAt }

            Result.success(reports)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error getting equipment reports", e)
            Result.failure(e)
        }
    }

    suspend fun getEquipmentInterventionsHistory(equipmentId: String): Result<List<Report>> {
        return try {
            val querySnapshot = firestore.collection("reports")
                .whereEqualTo("equipmentId", equipmentId)
                .whereEqualTo("status", "resolved")
                .get()
                .await()

            val reports = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Report::class.java)
            }.sortedByDescending { it.createdAt }

            Result.success(reports)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error getting interventions history", e)
            Result.failure(e)
        }
    }

    suspend fun getUserReports(userId: String): Result<List<Report>> {
        return try {
            val querySnapshot = firestore.collection("reports")
                .whereEqualTo("reportedBy", userId)
                .whereIn("status", listOf("pending", "in_maintenance"))
                .get()
                .await()

            val reports = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Report::class.java)
            }.sortedByDescending { it.createdAt }

            Result.success(reports)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error getting user reports", e)
            Result.failure(e)
        }
    }
}