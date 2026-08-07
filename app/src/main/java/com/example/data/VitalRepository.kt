package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class VitalRepository(
    private val dao: VitalDao,
    val firestoreSyncManager: FirestoreSyncManager = FirestoreSyncManager()
) {

    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val activeRoutines: Flow<List<WorkoutRoutineEntity>> = dao.getActiveRoutines()
    val allSessions: Flow<List<LoggedWorkoutSessionEntity>> = dao.getAllSessions()
    val allLoggedSets: Flow<List<LoggedSetEntity>> = dao.getAllLoggedSets()

    fun getExercisesForRoutine(routineId: Long): Flow<List<WorkoutExerciseEntity>> {
        return dao.getExercisesForRoutine(routineId)
    }

    fun getSetsForSession(sessionId: Long): Flow<List<LoggedSetEntity>> {
        return dao.getSetsForSession(sessionId)
    }

    suspend fun getSetsForSessionList(sessionId: Long): List<LoggedSetEntity> {
        return dao.getSetsForSession(sessionId).firstOrNull() ?: emptyList()
    }

    fun getMaxWeightForExercise(exerciseName: String): Flow<Float?> {
        return dao.getMaxWeightForExercise(exerciseName)
    }

    suspend fun saveUserProfile(profile: UserProfileEntity, userId: String? = null) {
        dao.saveUserProfile(profile)
        // Auto-generate fresh custom routines based on updated profile
        generateAndSaveRoutines(profile)

        if (!userId.isNullOrBlank()) {
            firestoreSyncManager.saveUserProfileToCloud(userId, profile)
        }
    }

    suspend fun generateAndSaveRoutines(profile: UserProfileEntity) {
        val routinesWithExercises = RoutineGenerator.generateRoutineForProfile(profile)
        dao.replaceActiveRoutines(routinesWithExercises)
    }

    suspend fun logWorkoutSession(
        session: LoggedWorkoutSessionEntity,
        sets: List<LoggedSetEntity>,
        userId: String? = null
    ): Long {
        val sessionId = dao.insertSession(session)
        val updatedSets = sets.map { it.copy(sessionId = sessionId) }
        dao.insertSets(updatedSets)

        if (!userId.isNullOrBlank()) {
            val updatedSession = session.copy(id = sessionId)
            firestoreSyncManager.saveLoggedSessionToCloud(userId, updatedSession, updatedSets)
        }
        return sessionId
    }

    suspend fun deleteSession(sessionId: Long) {
        dao.deleteSetsForSession(sessionId)
        dao.deleteSession(sessionId)
    }

    suspend fun ensureInitialDataLoaded() {
        val currentProfile = dao.getUserProfile().firstOrNull()
        if (currentProfile == null) {
            val defaultProfile = UserProfileEntity()
            dao.saveUserProfile(defaultProfile)
            generateAndSaveRoutines(defaultProfile)
        }
    }

    suspend fun restoreUserDataFromCloud(userId: String) {
        val cloudProfile = firestoreSyncManager.fetchUserProfileFromCloud(userId)
        if (cloudProfile != null) {
            dao.saveUserProfile(cloudProfile)
            generateAndSaveRoutines(cloudProfile)
        }

        val cloudSessions = firestoreSyncManager.fetchLoggedSessionsFromCloud(userId)
        for ((session, sets) in cloudSessions) {
            logWorkoutSession(session, sets)
        }
    }

    suspend fun backupAllLocalDataToCloud(userId: String) {
        val profile = dao.getUserProfile().firstOrNull()
        val sessions = dao.getAllSessions().firstOrNull() ?: emptyList()
        firestoreSyncManager.backupAllLocalDataToCloud(
            userId = userId,
            profile = profile,
            sessions = sessions,
            fetchSetsForSession = { sessionId -> getSetsForSessionList(sessionId) }
        )
    }
}
