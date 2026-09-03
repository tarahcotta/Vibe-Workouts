package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.musclewiki.MuscleWikiRepository

/**
 * Universal Exercise Video Player component.
 * Plays direct MuscleWiki exercise form videos with auto-loop, gesture seeking, and speed controls.
 */
@Composable
fun ExerciseVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    exerciseName: String? = null
) {
    val resolvedUrl = if (videoUrl.isNotBlank() && (videoUrl.endsWith(".mp4") || videoUrl.startsWith("http"))) {
        if (videoUrl.contains("youtube.com") && exerciseName != null) {
            MuscleWikiRepository.getVideoUrlForExercise(exerciseName)
        } else {
            videoUrl
        }
    } else if (exerciseName != null) {
        MuscleWikiRepository.getVideoUrlForExercise(exerciseName)
    } else {
        "https://media.musclewiki.com/media/uploads/videos/branded/male-barbell-squat-front.mp4"
    }

    MuscleWikiVideoPlayer(
        videoUrl = resolvedUrl,
        modifier = modifier
    )
}

/**
 * Backwards-compatibility alias for YouTubeVideoPlayer that now renders high performance MuscleWiki streams.
 */
@Composable
fun YouTubeVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    ExerciseVideoPlayer(
        videoUrl = videoUrl,
        modifier = modifier
    )
}
