package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "progress_photos")
data class ProgressPhotoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val filePath: String,
    val dateTimestamp: Long = System.currentTimeMillis(),
    val poseTag: String = "Front Posture", // "Front Posture", "Side Alignment", "Back / Scapular", "Muscle Tone", "General"
    val bodyWeightLbs: Float? = null,
    val notes: String = "",
    val isSample: Boolean = false
)

enum class PhotoPoseCategory(val tag: String, val displayName: String, val coachingDescription: String) {
    ALL("All", "All Angles", "View complete visual timeline"),
    FRONT("Front Posture", "Front Posture", "Evaluate shoulder symmetry, ribcage flare & pelvic leveling"),
    SIDE("Side Alignment", "Side Alignment", "Evaluate cervical spine, thoracic curvature & anterior pelvic tilt"),
    BACK("Back / Scapular", "Back & Scapular", "Inspect scapular retraction, latissimus tone & spinal erector density"),
    MUSCLE_TONE("Muscle Tone", "Muscle Tone & Core", "Assess quadriceps, deltoids, gluteal contraction & core definition"),
    GENERAL("General", "General Progress", "Unfiltered daily progress milestones")
}
