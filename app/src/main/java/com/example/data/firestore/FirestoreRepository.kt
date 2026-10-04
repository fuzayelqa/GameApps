package com.example.data.firestore

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.database.GameRecordEntity
import com.example.data.models.GameStatistics
import com.example.data.models.UserProfile
import com.example.data.models.UserSettingsSync
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

private const val TAG = "FirestoreRepository"

class FirestoreRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    // Secondary constructor accepting Context to resolve either default or named custom database ID
    constructor(context: Context, auth: FirebaseAuth = FirebaseAuth.getInstance()) : this(
        try {
            val dbId = context.applicationContext.getString(R.string.firestore_database_id)
            if (dbId.isNotBlank() && dbId != "(default)") {
                FirebaseFirestore.getInstance(dbId)
            } else {
                FirebaseFirestore.getInstance()
            }
        } catch (_: Exception) {
            FirebaseFirestore.getInstance()
        },
        auth
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("User is not authenticated")
    }

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> = runCatching {
        val uid = requireUserId()
        val data = mapOf(
            "userId" to uid,
            "email" to profile.email,
            "displayName" to profile.displayName,
            "isPremium" to profile.isPremium,
            "isEmailVerified" to profile.isEmailVerified,
            "createdAt" to profile.createdAt,
            "updatedAt" to System.currentTimeMillis()
        )
        db.collection("users").document(uid).set(data, SetOptions.merge()).await()
        Unit
    }.onFailure { Log.e(TAG, "Failed to save user profile", it) }

    suspend fun getUserProfile(): Result<UserProfile?> = runCatching {
        val uid = requireUserId()
        val snapshot = db.collection("users").document(uid).get().await()
        if (snapshot.exists()) {
            UserProfile(
                userId = snapshot.getString("userId") ?: uid,
                email = snapshot.getString("email") ?: "",
                displayName = snapshot.getString("displayName") ?: "",
                isPremium = snapshot.getBoolean("isPremium") ?: false,
                isEmailVerified = snapshot.getBoolean("isEmailVerified") ?: false,
                createdAt = snapshot.getLong("createdAt") ?: System.currentTimeMillis(),
                updatedAt = snapshot.getLong("updatedAt") ?: System.currentTimeMillis()
            )
        } else {
            null
        }
    }.onFailure { Log.e(TAG, "Failed to get user profile", it) }

    suspend fun syncStatistics(localStats: GameStatistics): Result<GameStatistics> = runCatching {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("statistics").document("stats")
        val cloudSnapshot = docRef.get().await()

        val merged = if (cloudSnapshot.exists()) {
            val cloudHighScore = cloudSnapshot.getLong("highScore")?.toInt() ?: 0
            val cloudHighestLevel = cloudSnapshot.getLong("highestLevel")?.toInt() ?: 1
            val cloudLongestSnake = cloudSnapshot.getLong("longestSnake")?.toInt() ?: 3
            val cloudTotalGames = cloudSnapshot.getLong("totalGames")?.toInt() ?: 0
            val cloudFood = cloudSnapshot.getLong("foodCollected")?.toInt() ?: 0
            val cloudBestTime = cloudSnapshot.getLong("bestGameTimeSeconds")?.toInt() ?: 0

            // Conflict resolution: Never overwrite a better high score with a lower score!
            GameStatistics(
                userId = uid,
                highScore = maxOf(localStats.highScore, cloudHighScore),
                highestLevel = maxOf(localStats.highestLevel, cloudHighestLevel),
                longestSnake = maxOf(localStats.longestSnake, cloudLongestSnake),
                totalGames = maxOf(localStats.totalGames, cloudTotalGames),
                foodCollected = maxOf(localStats.foodCollected, cloudFood),
                bestGameTimeSeconds = maxOf(localStats.bestGameTimeSeconds, cloudBestTime),
                updatedAt = System.currentTimeMillis()
            )
        } else {
            localStats.copy(userId = uid, updatedAt = System.currentTimeMillis())
        }

        val data = mapOf(
            "userId" to uid,
            "highScore" to merged.highScore,
            "highestLevel" to merged.highestLevel,
            "longestSnake" to merged.longestSnake,
            "totalGames" to merged.totalGames,
            "foodCollected" to merged.foodCollected,
            "bestGameTimeSeconds" to merged.bestGameTimeSeconds,
            "updatedAt" to merged.updatedAt
        )
        docRef.set(data, SetOptions.merge()).await()
        merged
    }.onFailure { Log.e(TAG, "Failed to sync statistics", it) }

    suspend fun saveGameRecord(record: GameRecordEntity): Result<Unit> = runCatching {
        val uid = requireUserId()
        val data = mapOf(
            "id" to record.id,
            "userId" to uid,
            "score" to record.score,
            "level" to record.level,
            "snakeLength" to record.snakeLength,
            "foodCollected" to record.foodCollected,
            "gameDurationSeconds" to record.gameDurationSeconds,
            "gameMode" to record.gameMode,
            "date" to record.date
        )
        db.collection("users").document(uid).collection("records").document(record.id).set(data).await()
        Unit
    }.onFailure { Log.e(TAG, "Failed to save game record", it) }

    suspend fun syncSettings(settings: UserSettingsSync): Result<Unit> = runCatching {
        val uid = requireUserId()
        val data = mapOf(
            "userId" to uid,
            "controlType" to settings.controlType,
            "difficulty" to settings.difficulty,
            "vibrationEnabled" to settings.vibrationEnabled,
            "soundEnabled" to settings.soundEnabled,
            "musicEnabled" to settings.musicEnabled,
            "theme" to settings.theme,
            "snakeSkin" to settings.snakeSkin,
            "updatedAt" to System.currentTimeMillis()
        )
        db.collection("users").document(uid).collection("settings").document("prefs").set(data, SetOptions.merge()).await()
        Unit
    }.onFailure { Log.e(TAG, "Failed to sync settings", it) }

    suspend fun deleteUserData(): Result<Unit> = runCatching {
        val uid = requireUserId()
        // Delete stats doc
        db.collection("users").document(uid).collection("statistics").document("stats").delete().await()
        // Delete settings doc
        db.collection("users").document(uid).collection("settings").document("prefs").delete().await()
        // Delete user doc
        db.collection("users").document(uid).delete().await()
        Unit
    }.onFailure { Log.e(TAG, "Failed to delete user cloud data", it) }
}
