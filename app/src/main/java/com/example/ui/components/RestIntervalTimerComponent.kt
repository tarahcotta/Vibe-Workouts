package com.example.ui.components

import android.os.CountDownTimer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Timer10
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.example.ui.theme.BoneDensityGold
import com.example.ui.theme.PostureTeal
import kotlinx.coroutines.delay

/**
 * Built-In Interval Timer UI Card Component for tracking rest periods between sets.
 * Placed directly inside the workout screen or floating container.
 */
@Composable
fun BuiltInIntervalTimerCard(
    activeExerciseName: String = "",
    targetRestSeconds: Int = 90,
    isRunning: Boolean = false,
    remainingSeconds: Int = 90,
    alertMode: String = "Sound + Vibrate",
    onAlertModeChange: (String) -> Unit = {},
    onTogglePlayPause: () -> Unit = {},
    onResetTimer: (Int) -> Unit = {},
    onAdjustSeconds: (Int) -> Unit = {},
    onPresetSelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(TimerMode.COUNTDOWN) }
    var showPresets by remember { mutableStateOf(false) }
    val progress = if (targetRestSeconds > 0) {
        ((targetRestSeconds - remainingSeconds).toFloat() / targetRestSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val isFinished = remainingSeconds == 0 && targetRestSeconds > 0
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(isFinished) {
        if (isFinished) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("built_in_interval_timer_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFinished) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. Clean Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = CircleShape,
                        color = if (isFinished) MaterialTheme.colorScheme.primary else PostureTeal,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isFinished) Icons.Default.NotificationsActive else Icons.Default.Timer,
                                contentDescription = "Timer Icon",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (isFinished) "Rest Complete!" else "Rest Interval Timer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (activeExerciseName.isNotBlank()) "Target: $activeExerciseName (${targetRestSeconds}s)" else "Target Recovery: ${targetRestSeconds}s",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Mode & Sound Toggles grouped cleanly
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha =.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Sound Mode Toggle
                        IconButton(
                            onClick = {
                                val nextMode = when (alertMode) {
                                    "Sound + Vibrate" -> "Vibrate Only"
                                    "Vibrate Only" -> "Silent"
                                    else -> "Sound + Vibrate"
                                }
                                onAlertModeChange(nextMode)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = when (alertMode) {
                                    "Sound + Vibrate" -> Icons.Default.VolumeUp
                                    "Vibrate Only" -> Icons.Default.Vibration
                                    else -> Icons.Default.VolumeOff
                                },
                                contentDescription = "Alert Mode: $alertMode",
                                modifier = Modifier.size(16.dp),
                                tint = when (alertMode) {
                                    "Silent" -> MaterialTheme.colorScheme.error
                                    "Vibrate Only" -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                        }

                        // Timer Mode (Countdown / Stopwatch) Toggle
                        IconButton(
                            onClick = {
                                mode = if (mode == TimerMode.COUNTDOWN) TimerMode.STOPWATCH else TimerMode.COUNTDOWN
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text(
                                text = if (mode == TimerMode.COUNTDOWN) "⏳" else "⏱️",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Linear Progress Bar (Countdown mode only)
            if (mode == TimerMode.COUNTDOWN) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isFinished) MaterialTheme.colorScheme.primary else BoneDensityGold,
                    trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            // 3. Central Timer Display & Play/Reset Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val m = remainingSeconds / 60
                val s = remainingSeconds % 60
                val displayTime = String.format("%02d:%02d", m, s)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = displayTime,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isFinished) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Play/Pause Button (Primary Action)
                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                color = if (isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .testTag("interval_timer_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause Timer",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Reset Button
                    IconButton(
                        onClick = { onResetTimer(targetRestSeconds) },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("interval_timer_reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = "Reset Timer",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progressive Disclosure Toggle Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPresets = !showPresets }
                    .testTag("toggle_presets_button"),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showPresets) "Hide Presets & Adjustments" else "Configure Rest Time (${targetRestSeconds}s) & Presets",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = if (showPresets) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            AnimatedVisibility(visible = showPresets) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "QUICK ADJUST",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .clickable { onAdjustSeconds(-15) }
                                    .testTag("interval_timer_minus_15")
                            ) {
                                Text(
                                    text = "-15s",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .clickable { onAdjustSeconds(15) }
                                    .testTag("interval_timer_plus_15")
                            ) {
                                Text(
                                    text = "+15s",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "REST DURATION PRESETS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomFlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalSpacing = 6.dp,
                        verticalSpacing = 6.dp
                    ) {
                        val presets = listOf(
                            45 to "45s Hypertrophy",
                            60 to "60s Endurance",
                            90 to "90s Bone Density",
                            120 to "120s Strength",
                            180 to "180s Heavy"
                        )
                        presets.forEach { (seconds, label) ->
                            val isSelected = targetRestSeconds == seconds
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) else null,
                                modifier = Modifier
                                    .clickable { onPresetSelected(seconds) }
                                    .testTag("interval_preset_$seconds")
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


fun parseRestPeriodToSeconds(restStr: String): Int {
    val clean = restStr.lowercase().trim()
    return when {
        clean.contains("min") -> {
            val num = clean.replace(Regex("[^0-9.]"), "").toFloatOrNull() ?: 2f
            (num * 60).toInt()
        }
        clean.contains("sec") || clean.contains("s") -> {
            val num = clean.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 90
            num
        }
        else -> {
            clean.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 90
        }
    }
}

enum class TimerMode {
    COUNTDOWN,
    STOPWATCH
}

@Composable
fun FloatingRestTimerBar(
    exerciseName: String,
    initialSeconds: Int,
    isActive: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(TimerMode.COUNTDOWN) }
    var secondsRemaining by remember { mutableIntStateOf(initialSeconds) }
    var totalTargetSeconds by remember { mutableIntStateOf(initialSeconds) }
    var stopwatchElapsedSeconds by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(true) }
    var isFinished by remember { mutableStateOf(false) }

    // Reset when a new initialSeconds or exercise arrives
    LaunchedEffect(initialSeconds, exerciseName) {
        totalTargetSeconds = initialSeconds
        secondsRemaining = initialSeconds
        stopwatchElapsedSeconds = 0
        isRunning = true
        isFinished = false
    }

    // Countdown Timer logic
    DisposableEffect(isRunning, mode, secondsRemaining) {
        var timer: CountDownTimer? = null
        if (isRunning && mode == TimerMode.COUNTDOWN && secondsRemaining > 0) {
            timer = object : CountDownTimer((secondsRemaining * 1000).toLong(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    secondsRemaining = (millisUntilFinished / 1000).toInt()
                }

                override fun onFinish() {
                    secondsRemaining = 0
                    isRunning = false
                    isFinished = true
                }
            }.start()
        }
        onDispose { timer?.cancel() }
    }

    // Stopwatch logic
    LaunchedEffect(isRunning, mode) {
        while (isRunning && mode == TimerMode.STOPWATCH) {
            delay(1000L)
            stopwatchElapsedSeconds++
        }
    }

    AnimatedVisibility(
        visible = isActive,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("floating_rest_timer_bar"),
            shape = RoundedCornerShape(20.dp),
            color = if (isFinished) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(
                width = 2.dp,
                color = if (isFinished) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Top Info Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFinished) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isFinished) Icons.Default.NotificationsActive else Icons.Default.Timer,
                                contentDescription = "Icon",
                                tint = if (isFinished) Color.White else MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = if (isFinished) "Rest Complete! Ready for Next Set" else "Rest Interval • $exerciseName",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (mode == TimerMode.COUNTDOWN) "Target Rest: ${totalTargetSeconds}s" else "Open Stopwatch Recovery",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close Timer")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Timer Display & Controls Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Big Time Display
                    val displayTime = if (mode == TimerMode.COUNTDOWN) {
                        val m = secondsRemaining / 60
                        val s = secondsRemaining % 60
                        String.format("%02d:%02d", m, s)
                    } else {
                        val m = stopwatchElapsedSeconds / 60
                        val s = stopwatchElapsedSeconds % 60
                        String.format("%02d:%02d", m, s)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = displayTime,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isFinished) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Play/Pause Button
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.clickable {
                                isRunning = !isRunning
                                if (isFinished) isFinished = false
                            }
                        ) {
                            Box(modifier = Modifier.padding(8.dp)) {
                                Icon(
                                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Reset
                        IconButton(onClick = {
                            secondsRemaining = totalTargetSeconds
                            stopwatchElapsedSeconds = 0
                            isFinished = false
                            isRunning = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = "Reset",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Adjustment Chips
                    if (mode == TimerMode.COUNTDOWN) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable {
                                    secondsRemaining = (secondsRemaining - 15).coerceAtLeast(0)
                                }
                            ) {
                                Text(
                                    text = "-15s",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.clickable {
                                    secondsRemaining += 15
                                    isFinished = false
                                }
                            ) {
                                Text(
                                    text = "+15s",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Presets & Mode Toggle Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(60, 90, 120, 180).forEach { preset ->
                            val isSel = mode == TimerMode.COUNTDOWN && totalTargetSeconds == preset
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.clickable {
                                    mode = TimerMode.COUNTDOWN
                                    totalTargetSeconds = preset
                                    secondsRemaining = preset
                                    isFinished = false
                                    isRunning = true
                                }
                            ) {
                                Text(
                                    text = "${preset}s",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Mode Switcher
                    TextButton(
                        onClick = {
                            mode = if (mode == TimerMode.COUNTDOWN) TimerMode.STOPWATCH else TimerMode.COUNTDOWN
                            isRunning = true
                        }
                    ) {
                        Text(
                            text = if (mode == TimerMode.COUNTDOWN) "Switch to Stopwatch ⏱️" else "Switch to Countdown ⏳",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
