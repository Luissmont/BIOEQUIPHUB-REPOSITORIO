package com.mobiles.bioequip_des.Data.Repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.mobiles.bioequip_des.Data.Models.User
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    fun getCurrentUser(): FirebaseUser? = auth.currentUser


    fun isUserLoggedIn(): Boolean = getCurrentUser() != null

    suspend fun register(
        name: String,
        lastName: String,
        email: String,
        password: String,
        role: String
    ): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Error al crear usuario")


            val user = User(
                uid = firebaseUser.uid,
                name = name,
                lastName = lastName,
                email = email,
                role = role
            )

            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Error al iniciar sesión")

            val userDoc = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            val user = userDoc.toObject(User::class.java)
                ?: throw Exception("Usuario no encontrado en base de datos")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getCurrentUserData(): Result<User> {
        return try {
            val firebaseUser = getCurrentUser() ?: throw Exception("No hay usuario logueado")

            val userDoc = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            val user = userDoc.toObject(User::class.java)
                ?: throw Exception("Usuario no encontrado")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}