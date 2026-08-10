package com.example.data

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

sealed class SyncStatus {
    object Idle : SyncStatus()
    object Syncing : SyncStatus()
    data class Success(val message: String, val timestamp: Long = System.currentTimeMillis()) : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}

class FirestoreSyncManager(private val context: Context? = null) {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            if (context != null && FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            val apps = if (context != null) FirebaseApp.getApps(context) else emptyList()
            if (apps.isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w("FirestoreSyncManager", "FirebaseFirestore unavailable: ${e.message}")
            null
        }
    }

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    suspend fun saveUserProfileToCloud(userId: String, profile: UserProfileEntity) {
        val db = firestore ?: return
        try {
            _syncStatus.value = SyncStatus.Syncing
            val profileData = mapOf(
                "id" to profile.id,
                "age" to profile.age,
                "strengthLevel" to profile.strengthLevel,
                "availableEquipment" to profile.availableEquipment,
                "scheduleDaysPerWeek" to profile.scheduleDaysPerWeek,
                "jointHistory" to profile.jointHistory,
                "fitnessGoals" to profile.fitnessGoals,
                "focusAreas" to profile.focusAreas,
                "targetRpeRange" to profile.targetRpeRange,
                "updatedAt" to profile.updatedAt
            )
            db.collection("users")
                .document(userId)
                .collection("profile")
                .document("main")
                .set(profileData, SetOptions.merge())
                .await()

            _syncStatus.value = SyncStatus.Success("Profile synced to cloud")
        } catch (e: Exception) {
            Log.e("FirestoreSyncManager", "Error saving profile", e)
            _syncStatus.value = SyncStatus.Error(e.localizedMessage ?: "Failed to sync profile")
        }
    }

    suspend fun fetchUserProfileFromCloud(userId: String): UserProfileEntity? {
        val db = firestore ?: return null
        return try {
            val doc = db.collection("users")
                .document(userId)
                .collection("profile")
                .document("main")
                .get()
                .await()

            if (doc.exists()) {
                UserProfileEntity(
                    id = (doc.getLong("id")?.toInt() ?: 1),
                    age = (doc.getLong("age")?.toInt() ?: 45),
                    strengthLevel = (doc.getString("strengthLevel") ?: "Intermediate"),
                    availableEquipment = (doc.getString("availableEquipment") ?: "Dumbbells & Kettlebells"),
                    scheduleDaysPerWeek = (doc.getLong("scheduleDaysPerWeek")?.toInt() ?: 3),
                    jointHistory = (doc.getString("jointHistory") ?: "None"),
                    fitnessGoals = (doc.getString("fitnessGoals") ?: "Bone Mineral Density, Joint & Cartilage Longevity"),
                    focusAreas = (doc.getString("focusAreas") ?: "Bone Density, Posture"),
                    targetRpeRange = (doc.getString("targetRpeRange") ?: "7-8"),
                    updatedAt = (doc.getLong("updatedAt") ?: System.currentTimeMillis())
                )
            } else null
        } catch (e: Exception) {
            Log.e("FirestoreSyncManager", "Error fetching profile", e)
            null
        }
    }

    suspend fun saveLoggedSessionToCloud(
        userId: String,
        session: LoggedWorkoutSessionEntity,
        sets: List<LoggedSetEntity>
    ) {
        val db = firestore ?: return
        try {
            _syncStatus.value = SyncStatus.Syncing
            val sessionData = mapOf(
                "id" to session.id,
                "routineDayTitle" to session.routineDayTitle,
                "dateTimestamp" to session.dateTimestamp,
                "totalVolumeLbs" to session.totalVolumeLbs,
                "totalSetsCompleted" to session.totalSetsCompleted,
                "overallFeel" to session.overallFeel,
                "notes" to session.notes,
                "sets" to sets.map { set ->
                    mapOf(
                        "id" to set.id,
                        "sessionId" to set.sessionId,
                        "exerciseName" to set.exerciseName,
                        "setNumber" to set.setNumber,
                        "weightLbs" to set.weightLbs,
                        "repsCompleted" to set.repsCompleted,
                        "rpeActual" to set.rpeActual,
                        "jointFeel" to set.jointFeel
                    )
                }
            )

            val docId = if (session.id != 0L) session.id.toString() else session.dateTimestamp.toString()
            db.collection("users")
                .document(userId)
                .collection("sessions")
                .document(docId)
                .set(sessionData, SetOptions.merge())
                .await()

            _syncStatus.value = SyncStatus.Success("Session backed up to cloud")
        } catch (e: Exception) {
            Log.e("FirestoreSyncManager", "Error saving session", e)
            _syncStatus.value = SyncStatus.Error(e.localizedMessage ?: "Failed to sync workout session")
        }
    }

    suspend fun fetchLoggedSessionsFromCloud(userId: String): List<Pair<LoggedWorkoutSessionEntity, List<LoggedSetEntity>>> {
        val db = firestore ?: return emptyList()
        return try {
            val querySnapshot = db.collection("users")
                .document(userId)
                .collection("sessions")
                .get()
                .await()

            querySnapshot.documents.mapNotNull { doc ->
                val sessionId = doc.getLong("id") ?: 0L
                val routineTitle = doc.getString("routineDayTitle") ?: "Workout"
                val timestamp = doc.getLong("dateTimestamp") ?: System.currentTimeMillis()
                val volume = (doc.getDouble("totalVolumeLbs")?.toFloat() ?: 0f)
                val setsCount = (doc.getLong("totalSetsCompleted")?.toInt() ?: 0)
                val overallFeel = doc.getString("overallFeel") ?: "Good"
                val notes = doc.getString("notes") ?: ""

                val sessionEntity = LoggedWorkoutSessionEntity(
                    id = sessionId,
                    routineDayTitle = routineTitle,
                    dateTimestamp = timestamp,
                    totalVolumeLbs = volume,
                    totalSetsCompleted = setsCount,
                    overallFeel = overallFeel,
                    notes = notes
                )

                val rawSets = doc.get("sets") as? List<Map<String, Any>> ?: emptyList()
                val setsList = rawSets.mapIndexed { idx, map ->
                    LoggedSetEntity(
                        id = (map["id"] as? Long) ?: 0L,
                        sessionId = (map["sessionId"] as? Long) ?: sessionId,
                        exerciseName = map["exerciseName"] as? String ?: "Exercise",
                        setNumber = (map["setNumber"] as? Long)?.toInt() ?: (idx + 1),
                        weightLbs = (map["weightLbs"] as? Double)?.toFloat()
                            ?: (map["weightLbs"] as? Long)?.toFloat() ?: 0f,
                        repsCompleted = (map["repsCompleted"] as? Long)?.toInt() ?: 10,
                        rpeActual = (map["rpeActual"] as? Long)?.toInt() ?: 8,
                        jointFeel = map["jointFeel"] as? String ?: "Comfortable"
                    )
                }

                Pair(sessionEntity, setsList)
            }
        } catch (e: Exception) {
            Log.e("FirestoreSyncManager", "Error fetching sessions", e)
            emptyList()
        }
    }

    suspend fun backupAllLocalDataToCloud(
        userId: String,
        profile: UserProfileEntity?,
        sessions: List<LoggedWorkoutSessionEntity>,
        fetchSetsForSession: suspend (Long) -> List<LoggedSetEntity>
    ) {
        val db = firestore ?: return
        try {
            _syncStatus.value = SyncStatus.Syncing

            if (profile != null) {
                saveUserProfileToCloud(userId, profile)
            }

            for (session in sessions) {
                val sets = fetchSetsForSession(session.id)
                saveLoggedSessionToCloud(userId, session, sets)
            }

            _syncStatus.value = SyncStatus.Success("Full sync complete!")
        } catch (e: Exception) {
            Log.e("FirestoreSyncManager", "Full backup failed", e)
            _syncStatus.value = SyncStatus.Error("Cloud backup failed: ${e.localizedMessage}")
        }
    }
}
