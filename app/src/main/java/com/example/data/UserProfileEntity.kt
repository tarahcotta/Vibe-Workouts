package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val strengthLevel: String = "Intermediate", // Beginner, Intermediate, Advanced
    val availableEquipment: String = "Dumbbells & Kettlebells", // Bodyweight/Bands, Dumbbells & Kettlebells, Full Barbell Gym
    val scheduleDaysPerWeek: Int = 3, // 2, 3, or 4
    val jointHistory: String = "Knees, Lower Back", // Comma separated or "None"
    val focusAreas: String = "Bone Density, Posture, Single-Leg Balance, Grip Strength", // Comma separated
    val targetRpeRange: String = "7-8",
    val updatedAt: Long = System.currentTimeMillis()
)
