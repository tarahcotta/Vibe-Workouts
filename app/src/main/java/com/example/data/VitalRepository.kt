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


    val bookmarkedExercises: Flow<List<BookmarkedExerciseEntity>> = dao.getBookmarkedExercises()
    val allProgressPhotos: Flow<List<ProgressPhotoEntity>> = dao.getAllProgressPhotos()

    suspend fun addBookmark(exerciseId: String) {
        dao.insertBookmarkedExercise(BookmarkedExerciseEntity(exerciseId))
    }

    suspend fun removeBookmark(exerciseId: String) {
        dao.deleteBookmarkedExercise(exerciseId)
    }

    suspend fun addProgressPhoto(photo: ProgressPhotoEntity): Long {
        return dao.insertProgressPhoto(photo)
    }

    suspend fun deleteProgressPhoto(id: Long, filePath: String) {
        LocalPhotoStorageManager.deleteLocalPhotoFile(filePath)
        dao.deleteProgressPhoto(id)
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

        val existingSessions = dao.getAllSessions().firstOrNull()
        if (existingSessions.isNullOrEmpty()) {
            val now = System.currentTimeMillis()
            val dayMs = 86400000L

            // Week -5 session
            val session1Id = dao.insertSession(
                LoggedWorkoutSessionEntity(
                    routineDayTitle = "Day 1: Lower Body & Density",
                    dateTimestamp = now - (35 * dayMs),
                    totalVolumeLbs = 1050f,
                    totalSetsCompleted = 3,
                    overallFeel = "Strong",
                    notes = "Baseline weight established."
                )
            )
            dao.insertSets(
                listOf(
                    LoggedSetEntity(sessionId = session1Id, exerciseName = "Goblet Squat", setNumber = 1, weightLbs = 35f, repsCompleted = 10, rpeActual = 7, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session1Id, exerciseName = "Goblet Squat", setNumber = 2, weightLbs = 35f, repsCompleted = 10, rpeActual = 7, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session1Id, exerciseName = "Goblet Squat", setNumber = 3, weightLbs = 35f, repsCompleted = 10, rpeActual = 8, jointFeel = "Comfortable")
                )
            )

            // Week -4 session
            val session2Id = dao.insertSession(
                LoggedWorkoutSessionEntity(
                    routineDayTitle = "Day 2: Posterior Chain & Back",
                    dateTimestamp = now - (28 * dayMs),
                    totalVolumeLbs = 2760f,
                    totalSetsCompleted = 3,
                    overallFeel = "Great",
                    notes = "Good hip hinge form on hex bar."
                )
            )
            dao.insertSets(
                listOf(
                    LoggedSetEntity(sessionId = session2Id, exerciseName = "Hex Bar Deadlift", setNumber = 1, weightLbs = 115f, repsCompleted = 8, rpeActual = 7, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session2Id, exerciseName = "Hex Bar Deadlift", setNumber = 2, weightLbs = 115f, repsCompleted = 8, rpeActual = 8, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session2Id, exerciseName = "Hex Bar Deadlift", setNumber = 3, weightLbs = 115f, repsCompleted = 8, rpeActual = 8, jointFeel = "Comfortable")
                )
            )

            // Week -3 session
            val session3Id = dao.insertSession(
                LoggedWorkoutSessionEntity(
                    routineDayTitle = "Day 1: Lower Body & Density",
                    dateTimestamp = now - (21 * dayMs),
                    totalVolumeLbs = 1950f,
                    totalSetsCompleted = 6,
                    overallFeel = "Strong",
                    notes = "Increased Goblet Squat load by 5 lbs."
                )
            )
            dao.insertSets(
                listOf(
                    LoggedSetEntity(sessionId = session3Id, exerciseName = "Goblet Squat", setNumber = 1, weightLbs = 40f, repsCompleted = 10, rpeActual = 7, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session3Id, exerciseName = "Goblet Squat", setNumber = 2, weightLbs = 40f, repsCompleted = 10, rpeActual = 8, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session3Id, exerciseName = "Goblet Squat", setNumber = 3, weightLbs = 40f, repsCompleted = 10, rpeActual = 8, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session3Id, exerciseName = "Dumbbell Overhead Press", setNumber = 1, weightLbs = 25f, repsCompleted = 10, rpeActual = 7, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session3Id, exerciseName = "Dumbbell Overhead Press", setNumber = 2, weightLbs = 25f, repsCompleted = 10, rpeActual = 8, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session3Id, exerciseName = "Dumbbell Overhead Press", setNumber = 3, weightLbs = 25f, repsCompleted = 10, rpeActual = 8, jointFeel = "Comfortable")
                )
            )

            // Week -2 session
            val session4Id = dao.insertSession(
                LoggedWorkoutSessionEntity(
                    routineDayTitle = "Day 2: Posterior Chain & Back",
                    dateTimestamp = now - (14 * dayMs),
                    totalVolumeLbs = 3240f,
                    totalSetsCompleted = 3,
                    overallFeel = "Solid",
                    notes = "Hex Bar up to 135 lbs!"
                )
            )
            dao.insertSets(
                listOf(
                    LoggedSetEntity(sessionId = session4Id, exerciseName = "Hex Bar Deadlift", setNumber = 1, weightLbs = 135f, repsCompleted = 8, rpeActual = 7, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session4Id, exerciseName = "Hex Bar Deadlift", setNumber = 2, weightLbs = 135f, repsCompleted = 8, rpeActual = 8, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session4Id, exerciseName = "Hex Bar Deadlift", setNumber = 3, weightLbs = 135f, repsCompleted = 8, rpeActual = 8, jointFeel = "Comfortable")
                )
            )

            // Week -1 session
            val session5Id = dao.insertSession(
                LoggedWorkoutSessionEntity(
                    routineDayTitle = "Day 1: Lower Body & Density",
                    dateTimestamp = now - (7 * dayMs),
                    totalVolumeLbs = 2250f,
                    totalSetsCompleted = 6,
                    overallFeel = "Excellent",
                    notes = "Squatting 45 lbs smoothly."
                )
            )
            dao.insertSets(
                listOf(
                    LoggedSetEntity(sessionId = session5Id, exerciseName = "Goblet Squat", setNumber = 1, weightLbs = 45f, repsCompleted = 10, rpeActual = 7, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session5Id, exerciseName = "Goblet Squat", setNumber = 2, weightLbs = 45f, repsCompleted = 10, rpeActual = 8, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session5Id, exerciseName = "Goblet Squat", setNumber = 3, weightLbs = 45f, repsCompleted = 10, rpeActual = 8, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session5Id, exerciseName = "Dumbbell Overhead Press", setNumber = 1, weightLbs = 30f, repsCompleted = 10, rpeActual = 8, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session5Id, exerciseName = "Dumbbell Overhead Press", setNumber = 2, weightLbs = 30f, repsCompleted = 10, rpeActual = 8, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session5Id, exerciseName = "Dumbbell Overhead Press", setNumber = 3, weightLbs = 30f, repsCompleted = 10, rpeActual = 8, jointFeel = "Comfortable")
                )
            )

            // Recent session (2 days ago)
            val session6Id = dao.insertSession(
                LoggedWorkoutSessionEntity(
                    routineDayTitle = "Day 2: Posterior Chain & Back",
                    dateTimestamp = now - (2 * dayMs),
                    totalVolumeLbs = 3600f,
                    totalSetsCompleted = 3,
                    overallFeel = "Peak Effort",
                    notes = "Hex Bar hit 150 lbs!"
                )
            )
            dao.insertSets(
                listOf(
                    LoggedSetEntity(sessionId = session6Id, exerciseName = "Hex Bar Deadlift", setNumber = 1, weightLbs = 150f, repsCompleted = 8, rpeActual = 8, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session6Id, exerciseName = "Hex Bar Deadlift", setNumber = 2, weightLbs = 150f, repsCompleted = 8, rpeActual = 8, jointFeel = "Comfortable"),
                    LoggedSetEntity(sessionId = session6Id, exerciseName = "Hex Bar Deadlift", setNumber = 3, weightLbs = 150f, repsCompleted = 8, rpeActual = 8, jointFeel = "Comfortable")
                )
            )
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
