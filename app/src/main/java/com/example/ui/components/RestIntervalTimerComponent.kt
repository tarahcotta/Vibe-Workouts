package com.example.ui.components

import android.os.CountDownTimer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

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
                                contentDescription = null,
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
                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
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
