package com.example.data.musclewiki

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * MuscleWiki API Data Models for Exercise Form Videos & Demonstrations.
 */
@Serializable
data class MuscleWikiExercise(
    val id: Int? = null,
    val name: String,
    val description: String? = null,
    val category: String? = null,
    val difficulty: String? = null,
    val muscles: List<String> = emptyList(),
    @SerialName("primary_muscles")
    val primaryMuscles: List<String> = emptyList(),
    @SerialName("secondary_muscles")
    val secondaryMuscles: List<String> = emptyList(),
    val equipment: String? = null,
    val steps: List<String> = emptyList(),
    @SerialName("video_url")
    val videoUrl: String? = null,
    val videos: List<MuscleWikiVideo> = emptyList(),
    val images: List<String> = emptyList()
)

@Serializable
data class MuscleWikiVideo(
    val url: String,
    val angle: String? = null, // "male_front", "male_side", "female_front", "female_side"
    val gender: String? = null,
    @SerialName("thumbnail_url")
    val thumbnailUrl: String? = null
)

@Serializable
data class MuscleWikiResponse(
    val count: Int = 0,
    val results: List<MuscleWikiExercise> = emptyList()
)
