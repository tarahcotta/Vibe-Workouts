package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logged_workout_sessions")
data class LoggedWorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineDayTitle: String,
    val dateTimestamp: Long = System.currentTimeMillis(),
    val totalVolumeLbs: Float = 0f,
    val totalSetsCompleted: Int = 0,
    val overallFeel: String = "Strong & Energized", // "Strong & Energized", "Challenging but Good", "Joint Discomfort / Scaled"
    val notes: String = ""
)

@Entity(tableName = "logged_sets")
data class LoggedSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val exerciseName: String,
    val setNumber: Int,
    val weightLbs: Float,
    val repsCompleted: Int,
    val rpeActual: Int, // 1 to 10
    val jointFeel: String = "Comfortable" // "Comfortable", "Mild Tension", "Joint Strain"
)
