package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarked_exercises")
data class BookmarkedExerciseEntity(
    @PrimaryKey val exerciseId: String
)
