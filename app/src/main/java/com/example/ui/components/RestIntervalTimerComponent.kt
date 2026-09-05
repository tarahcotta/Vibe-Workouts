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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
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
 * Modern, High-Impact Rest Interval Timer Modal Sheet Content.
 * Formatted cleanly for BottomSheet presentation with large tabular countdown digits,
 * ergonomic oversized touch controls, immediately accessible rest presets, and alert audio toggles.
 */
@Composable
fun RestIntervalTimerModalSheetContent(
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
    onClose: () -> Unit = {},
    showCloseButton: Boolean = true,
    showDoneButton: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isFinished = remainingSeconds == 0 && targetRestSeconds > 0
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(isFinished) {
        if (isFinished) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    val progress = if (targetRestSeconds > 0) {
        ((targetRestSeconds - remainingSeconds).toFloat() / targetRestSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val m = remainingSeconds / 60
    val s = remainingSeconds % 60
    val displayTime = String.format("%02d:%02d", m, s)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("built_in_interval_timer_card"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Top Header Row: Icon + Title + Exercise Context + Close Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isFinished) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFinished) Icons.Default.CheckCircle else Icons.Default.Timer,
                        contentDescription = "Timer",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isFinished) "Rest Complete!" else "Rest Interval Timer",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (activeExerciseName.isNotBlank()) "Target: $activeExerciseName • Recovery"
                        else "Target Recovery: ${targetRestSeconds}s • Bone Remodeling",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (showCloseButton) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 2. Central Hero Timer Display Container
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = if (isFinished) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            border = BorderStroke(
                1.dp,
                if (isFinished) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Status Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when {
                        isFinished -> MaterialTheme.colorScheme.primaryContainer
                        isRunning -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else -> MaterialTheme.colorScheme.surface
                    },
                    border = BorderStroke(
                        1.dp,
                        when {
                            isFinished -> MaterialTheme.colorScheme.primary
                            isRunning -> MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                            else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when {
                                isFinished -> Icons.Default.CheckCircle
                                isRunning -> Icons.Default.Timer
                                else -> Icons.Default.Pause
                            },
                            contentDescription = null,
                            tint = if (isFinished || isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when {
                                isFinished -> "REST COMPLETE • READY TO LIFT"
                                isRunning -> "RECOVERY ACTIVE"
                                else -> "TIMER PAUSED"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = if (isFinished || isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Hero Digits Display (Big, high-contrast, gym-legible)
                Text(
                    text = displayTime,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = if (isFinished) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Linear Progress Bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isFinished) MaterialTheme.colorScheme.primary else BoneDensityGold,
                    trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Ergonomic Gym Control Buttons Row: [-15s] [Play/Pause FAB] [+15s] [Reset]
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quick Minus 15s
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onAdjustSeconds(-15)
                            }
                            .testTag("interval_timer_minus_15"),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                        tonalElevation = 1.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "-15",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Main Action Play/Pause Hero Button
                    Surface(
                        modifier = Modifier
                            .size(66.dp)
                            .clip(CircleShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onTogglePlayPause()
                            }
                            .testTag("interval_timer_play_pause_button"),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause Timer",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    // Quick Plus 15s
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onAdjustSeconds(15)
                            }
                            .testTag("interval_timer_plus_15"),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        tonalElevation = 1.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "+15",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Reset Button
                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onResetTimer(targetRestSeconds)
                            }
                            .testTag("interval_timer_reset_button"),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = "Reset Timer",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Quick Presets Section (Always visible, clean, 2-row grid)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("toggle_presets_button"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "REST DURATION PRESETS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            ) {
                Text(
                    text = "Target: ${targetRestSeconds}s",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val presetListRow1 = listOf(
            Triple(45, "45s", "Hypertrophy"),
            Triple(60, "60s", "Endurance"),
            Triple(90, "90s", "Bone Density ⭐")
        )
        val presetListRow2 = listOf(
            Triple(120, "2 min", "Strength"),
            Triple(180, "3 min", "Heavy Axial"),
            Triple(240, "4 min", "Max Effort")
        )

        listOf(presetListRow1, presetListRow2).forEach { rowPresets ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowPresets.forEach { (seconds, timeLabel, purposeLabel) ->
                    val isSelected = targetRestSeconds == seconds
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onPresetSelected(seconds)
                            }
                            .testTag("interval_preset_$seconds"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                        ),
                        tonalElevation = if (isSelected) 3.dp else 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = timeLabel,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = purposeLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. Alert Notifications Mode (Sound, Vibrate, Silent Segmented Selector)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ALERT NOTIFICATIONS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val alertModes = listOf(
                Pair("Sound + Vibrate", Icons.Default.VolumeUp),
                Pair("Vibrate Only", Icons.Default.Vibration),
                Pair("Silent", Icons.Default.VolumeOff)
            )

            alertModes.forEach { (modeName, icon) ->
                val isSelected = alertMode == modeName
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onAlertModeChange(modeName)
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = modeName,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (modeName) {
                                "Sound + Vibrate" -> "Sound"
                                "Vibrate Only" -> "Vibrate"
                                else -> "Silent"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 5. Done / Resume Workout CTA Button
        if (showDoneButton) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isFinished) "Start Next Set"
                        else if (isRunning) "Resume Workout (Timer Running)"
                        else "Done",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (isFinished) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("built_in_interval_timer_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        RestIntervalTimerModalSheetContent(
            activeExerciseName = activeExerciseName,
            targetRestSeconds = targetRestSeconds,
            isRunning = isRunning,
            remainingSeconds = remainingSeconds,
            alertMode = alertMode,
            onAlertModeChange = onAlertModeChange,
            onTogglePlayPause = onTogglePlayPause,
            onResetTimer = onResetTimer,
            onAdjustSeconds = onAdjustSeconds,
            onPresetSelected = onPresetSelected,
            onClose = {},
            showCloseButton = false,
            showDoneButton = false,
            modifier = Modifier.padding(16.dp)
        )
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
