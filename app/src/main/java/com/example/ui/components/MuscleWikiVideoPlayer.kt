package com.example.ui.components

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.VideoView
import android.media.MediaPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * High-performance Video Player for MuscleWiki MP4 & Streaming Exercise Form Videos.
 * Supports:
 * - Direct video rendering with auto-looping for repetitive exercise analysis
 * - Double-tap fast rewind (-3s) and fast forward (+3s)
 * - Variable playback speed presets (0.25x, 0.5x, 1.0x slow motion)
 * - Scrub bar with timestamp progress
 * - Graceful fallback and error recovery
 */
@Composable
fun MuscleWikiVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    isLooping: Boolean = true
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isPlaying by remember { mutableStateOf(autoPlay) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(1L) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    var seekRewindFeedback by remember { mutableStateOf(false) }
    var seekForwardFeedback by remember { mutableStateOf(false) }

    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var mediaPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }

    // Polling player position
    LaunchedEffect(isPlaying, videoUrl) {
        while (true) {
            videoViewRef?.let { vv ->
                if (vv.isPlaying) {
                    currentPositionMs = vv.currentPosition.toLong()
                    if (vv.duration > 0) {
                        durationMs = vv.duration.toLong()
                    }
                }
            }
            delay(100)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0D1117))
    ) {
        // Native Video View
        AndroidView(
            factory = { ctx ->
                VideoView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    
                    setOnPreparedListener { mp ->
                        mediaPlayerRef = mp
                        isLoading = false
                        hasError = false
                        durationMs = mp.duration.toLong().coerceAtLeast(1L)
                        mp.isLooping = isLooping
                        try {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                mp.playbackParams = mp.playbackParams.setSpeed(playbackSpeed)
                            }
                        } catch (e: Exception) {
                            // Some devices don't support custom playback params on raw streams
                        }
                        if (isPlaying) {
                            start()
                        }
                    }

                    setOnErrorListener { _, what, extra ->
                        isLoading = false
                        hasError = true
                        true // Handled
                    }

                    setOnCompletionListener {
                        if (isLooping) {
                            start()
                        } else {
                            isPlaying = false
                        }
                    }

                    try {
                        setVideoURI(Uri.parse(videoUrl))
                    } catch (e: Exception) {
                        hasError = true
                        isLoading = false
                    }

                    videoViewRef = this
                }
            },
            update = { vv ->
                try {
                    if (vv.tag != videoUrl) {
                        vv.tag = videoUrl
                        isLoading = true
                        hasError = false
                        vv.setVideoURI(Uri.parse(videoUrl))
                    }
                } catch (e: Exception) {
                    hasError = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp),
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Buffering MuscleWiki HD Video...",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Error State
        if (hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartDisplay,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "MuscleWiki Movement Stream Ready",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap to retry or switch angle view",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            hasError = false
                            isLoading = true
                            videoViewRef?.setVideoURI(Uri.parse(videoUrl))
                            videoViewRef?.start()
                        }
                    ) {
                        Text(
                            text = "Reload Feed",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Transparent Touch Layer for Double-Tap Gesture Seeking & Play/Pause
        Row(modifier = Modifier.fillMaxSize()) {
            // Left Half: Double tap rewinds 3 seconds
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(videoUrl) {
                        detectTapGestures(
                            onDoubleTap = {
                                videoViewRef?.let { vv ->
                                    val newPos = (vv.currentPosition - 3000).coerceAtLeast(0)
                                    vv.seekTo(newPos)
                                    currentPositionMs = newPos.toLong()
                                    seekRewindFeedback = true
                                    coroutineScope.launch {
                                        delay(600)
                                        seekRewindFeedback = false
                                    }
                                }
                            },
                            onTap = {
                                videoViewRef?.let { vv ->
                                    if (vv.isPlaying) {
                                        vv.pause()
                                        isPlaying = false
                                    } else {
                                        vv.start()
                                        isPlaying = true
                                    }
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = seekRewindFeedback,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.75f),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.FastRewind, contentDescription = "Rewind", tint = Color.White, modifier = Modifier.size(22.dp))
                            Text("-3s", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Right Half: Double tap forwards 3 seconds
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(videoUrl) {
                        detectTapGestures(
                            onDoubleTap = {
                                videoViewRef?.let { vv ->
                                    val newPos = (vv.currentPosition + 3000).coerceAtMost(vv.duration)
                                    vv.seekTo(newPos)
                                    currentPositionMs = newPos.toLong()
                                    seekForwardFeedback = true
                                    coroutineScope.launch {
                                        delay(600)
                                        seekForwardFeedback = false
                                    }
                                }
                            },
                            onTap = {
                                videoViewRef?.let { vv ->
                                    if (vv.isPlaying) {
                                        vv.pause()
                                        isPlaying = false
                                    } else {
                                        vv.start()
                                        isPlaying = true
                                    }
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = seekForwardFeedback,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.75f),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.FastForward, contentDescription = "Forward", tint = Color.White, modifier = Modifier.size(22.dp))
                            Text("+3s", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // MuscleWiki Badge (Top Left)
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.Black.copy(alpha = 0.7f),
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SmartDisplay,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "MuscleWiki Video",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }

        // Bottom Controls Overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            // Scrub Slider
            val curSec = (currentPositionMs / 1000).toInt()
            val totSec = (durationMs / 1000).toInt().coerceAtLeast(1)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format("%02d:%02d", curSec / 60, curSec % 60),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontSize = 11.sp
                )

                Slider(
                    value = currentPositionMs.toFloat(),
                    onValueChange = { newPos ->
                        currentPositionMs = newPos.toLong()
                        videoViewRef?.seekTo(newPos.toInt())
                    },
                    valueRange = 0f..durationMs.toFloat(),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .height(20.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                    )
                )

                Text(
                    text = String.format("%02d:%02d", totSec / 60, totSec % 60),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }

            // Controls: Play/Pause, Rewind, Forward, Speed Presets (0.25x, 0.5x, 1x)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Play / Pause Button
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                videoViewRef?.let { vv ->
                                    if (vv.isPlaying) {
                                        vv.pause()
                                        isPlaying = false
                                    } else {
                                        vv.start()
                                        isPlaying = true
                                    }
                                }
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Loop state badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.padding(start = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Loop, contentDescription = "Auto-Looping", tint = Color.White, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Auto-Loop", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Speed Preset Chips
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SlowMotionVideo,
                        contentDescription = "Speed",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(12.dp)
                    )
                    listOf(0.25f, 0.5f, 1.0f).forEach { speed ->
                        val isCurrent = playbackSpeed == speed
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isCurrent) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.clickable {
                                playbackSpeed = speed
                                try {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                        mediaPlayerRef?.playbackParams = mediaPlayerRef?.playbackParams?.setSpeed(speed) ?: return@clickable
                                    }
                                } catch (e: Exception) {
                                    // Device parameter handling
                                }
                            }
                        ) {
                            Text(
                                text = "${speed}x",
                                color = if (isCurrent) MaterialTheme.colorScheme.onPrimary else Color.White,
                                fontSize = 10.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(videoUrl) {
        onDispose {
            try {
                mediaPlayerRef?.let { mp ->
                    mp.stop()
                    mp.release()
                }
            } catch (e: Exception) {
                // Suppress MediaPlayer invalid state error (-38, 0)
            }
            try {
                videoViewRef?.stopPlayback()
            } catch (e: Exception) {
                // Ignore cleanup errors
            }
        }
    }
}
