package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class VitalRepository(private val dao: VitalDao) {

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

    fun getMaxWeightForExercise(exerciseName: String): Flow<Float?> {
        return dao.getMaxWeightForExercise(exerciseName)
    }

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        dao.saveUserProfile(profile)
        // Auto-generate fresh custom routines based on updated profile
        generateAndSaveRoutines(profile)
    }

    suspend fun generateAndSaveRoutines(profile: UserProfileEntity) {
        val routinesWithExercises = RoutineGenerator.generateRoutineForProfile(profile)
        dao.replaceActiveRoutines(routinesWithExercises)
    }

    suspend fun logWorkoutSession(
        session: LoggedWorkoutSessionEntity,
        sets: List<LoggedSetEntity>
    ): Long {
        val sessionId = dao.insertSession(session)
        val updatedSets = sets.map { it.copy(sessionId = sessionId) }
        dao.insertSets(updatedSets)
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
}
