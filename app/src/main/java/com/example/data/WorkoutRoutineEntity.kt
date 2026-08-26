package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_routines")
data class WorkoutRoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val dayName: String, // e.g. "Day 1: Heavy Axial Load & Upper Back Posture"
    val focusSummary: String, // e.g. "Bone mineral stimulus, scapular retraction, core brace"
    val createdAt: Long = System.currentTimeMillis(),
    val isCurrentActive: Boolean = true
)

@Entity(tableName = "workout_exercises")
data class WorkoutExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineId: Long,
    val exerciseName: String,
    val primaryGoal: String, // e.g. "Bone Density", "Posture", "Balance", "Grip Strength", "Core"
    val sets: Int,
    val repRange: String, // e.g. "6-8 reps", "10-12 reps", "30s hold"
    val restPeriod: String, // e.g. "90-120s", "60s"
    val rpe: String, // e.g. "RPE 7-8"
    val coachingCues: String, // e.g. "Push floor away through heels. Keep spine neutral."
    val orderIndex: Int,
    val userNotes: String = "" // Optional note-taking field for how they felt, equipment used, or form adjustments
)
