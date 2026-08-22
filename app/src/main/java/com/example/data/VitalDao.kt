package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalDao {
    // User Profile
    @Query("SELECT * FROM user_profiles WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    // Workout Routines
    @Query("SELECT * FROM workout_routines WHERE isCurrentActive = 1 ORDER BY id ASC")
    fun getActiveRoutines(): Flow<List<WorkoutRoutineEntity>>

    @Query("SELECT * FROM workout_exercises WHERE routineId = :routineId ORDER BY orderIndex ASC")
    fun getExercisesForRoutine(routineId: Long): Flow<List<WorkoutExerciseEntity>>

    @Query("SELECT * FROM workout_exercises ORDER BY id ASC")
    fun getAllActiveExercises(): Flow<List<WorkoutExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: WorkoutRoutineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<WorkoutExerciseEntity>)

    @Query("DELETE FROM workout_routines")
    suspend fun clearRoutines()

    @Query("DELETE FROM workout_exercises")
    suspend fun clearExercises()

    @Transaction
    suspend fun replaceActiveRoutines(
        routinesWithExercises: List<Pair<WorkoutRoutineEntity, List<WorkoutExerciseEntity>>>
    ) {
        clearExercises()
        clearRoutines()
        routinesWithExercises.forEach { (routine, exercises) ->
            val routineId = insertRoutine(routine)
            val updatedExercises = exercises.map { it.copy(routineId = routineId) }
            insertExercises(updatedExercises)
        }
    }

    // Workout Session Logging
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: LoggedWorkoutSessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<LoggedSetEntity>)

    @Query("SELECT * FROM logged_workout_sessions ORDER BY dateTimestamp DESC")
    fun getAllSessions(): Flow<List<LoggedWorkoutSessionEntity>>

    @Query("SELECT * FROM logged_sets WHERE sessionId = :sessionId ORDER BY setNumber ASC")
    fun getSetsForSession(sessionId: Long): Flow<List<LoggedSetEntity>>

    @Query("SELECT * FROM logged_sets ORDER BY id DESC")
    fun getAllLoggedSets(): Flow<List<LoggedSetEntity>>

    @Query("SELECT MAX(weightLbs) FROM logged_sets WHERE exerciseName = :exerciseName")
    fun getMaxWeightForExercise(exerciseName: String): Flow<Float?>

    @Query("SELECT * FROM logged_sets WHERE exerciseName = :exerciseName ORDER BY id DESC LIMIT 10")
    fun getRecentSetsForExercise(exerciseName: String): Flow<List<LoggedSetEntity>>

    @Query("DELETE FROM logged_workout_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)

    @Query("DELETE FROM logged_sets WHERE sessionId = :sessionId")
    suspend fun deleteSetsForSession(sessionId: Long)

    // Bookmarked Exercises
    @Query("SELECT * FROM bookmarked_exercises")
    fun getBookmarkedExercises(): Flow<List<BookmarkedExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookmarkedExercise(exercise: BookmarkedExerciseEntity)

    @Query("DELETE FROM bookmarked_exercises WHERE exerciseId = :exerciseId")
    suspend fun deleteBookmarkedExercise(exerciseId: String)

    // Localized Progress Photos
    @Query("SELECT * FROM progress_photos ORDER BY dateTimestamp DESC")
    fun getAllProgressPhotos(): Flow<List<ProgressPhotoEntity>>

    @Query("SELECT * FROM progress_photos WHERE poseTag = :poseTag ORDER BY dateTimestamp DESC")
    fun getProgressPhotosByPose(poseTag: String): Flow<List<ProgressPhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressPhoto(photo: ProgressPhotoEntity): Long

    @Query("DELETE FROM progress_photos WHERE id = :id")
    suspend fun deleteProgressPhoto(id: Long)
}
