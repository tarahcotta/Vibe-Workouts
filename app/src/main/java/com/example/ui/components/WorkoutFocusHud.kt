package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.ExerciseLogState
import com.example.ui.screens.SetLogInput
import kotlinx.coroutines.launch

/**
 * Dedicated In-Workout Heads-Up Display (Focus HUD Mode)
 * Reduces in-workout motor and cognitive load with large touch targets,
 * rapid stepper controls, and distraction-free single-exercise logging.
 */
@Composable
fun WorkoutFocusHud(
    exerciseLogs: List<ExerciseLogState>,
    activeExerciseIndex: Int,
    onSelectExerciseIndex: (Int) -> Unit,
    onOpenSwapDialog: (exerciseName: String) -> Unit,
    onOpenRpeDialog: (set: SetLogInput, exerciseName: String) -> Unit,
    onStartRestTimer: (seconds: Int, exerciseName: String) -> Unit,
    isTimerRunning: Boolean,
    timerRemainingSeconds: Int,
    onAdjustTimer: (deltaSeconds: Int) -> Unit,
    onSkipTimer: () -> Unit,
    onCompleteSet: (SetLogInput) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val currentLog = exerciseLogs.getOrNull(activeExerciseIndex) ?: return

    // Find the first uncompleted set or the last set
    val activeSetIndex = remember(currentLog.sets.map { it.isCompleted }, activeExerciseIndex) {
        val firstIncomplete = currentLog.sets.indexOfFirst { !it.isCompleted }
        if (firstIncomplete >= 0) firstIncomplete else (currentLog.sets.size - 1).coerceAtLeast(0)
    }
    val currentSet = currentLog.sets.getOrNull(activeSetIndex)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Exercise Selector Strip (Top Bar)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (activeExerciseIndex > 0) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onSelectExerciseIndex(activeExerciseIndex - 1)
                        }
                    },
                    enabled = activeExerciseIndex > 0
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Exercise",
                        tint = if (activeExerciseIndex > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "EXERCISE ${activeExerciseIndex + 1} OF ${exerciseLogs.size}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = currentLog.exerciseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }

                IconButton(
                    onClick = {
                        if (activeExerciseIndex < exerciseLogs.size - 1) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onSelectExerciseIndex(activeExerciseIndex + 1)
                        }
                    },
                    enabled = activeExerciseIndex < exerciseLogs.size - 1
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Exercise",
                        tint = if (activeExerciseIndex < exerciseLogs.size - 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            }
        }

        // 1-Tap Spine-Safe Exercise Substitution Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.clickable {
                    onOpenSwapDialog(currentLog.exerciseName)
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Swap to Spine-Sparing Variation",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            // Sets completion tracker
            val completedCount = currentLog.sets.count { it.isCompleted }
            Text(
                text = "$completedCount / ${currentLog.sets.size} Sets Done",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (completedCount == currentLog.sets.size) Color(0xFF00E676) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Rest Timer HUD Banner if Active
        if (isTimerRunning) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Resting: ${timerRemainingSeconds / 60}:${String.format("%02d", timerRemainingSeconds % 60)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Recovering for next set",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.clickable { onAdjustTimer(-15) }
                        ) {
                            Text(
                                text = "-15s",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.clickable { onAdjustTimer(+30) }
                        ) {
                            Text(
                                text = "+30s",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                        IconButton(
                            onClick = onSkipTimer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Skip Timer",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Active Focus Card for Current Set
        if (currentSet != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = BorderStroke(2.dp, if (currentSet.isCompleted) Color(0xFF00E676) else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Set Header & Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "ACTIVE SET ${currentSet.setNumber} OF ${currentLog.sets.size}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        if (currentSet.isCompleted) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF00E676).copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF00C853),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "LOGGED",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF00796B)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // WEIGHT STEPPER CONTROL (High Contrast, Large Buttons)
                    Text(
                        text = "Weight Load (Lbs)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = {
                                val cur = currentSet.weightText.toFloatOrNull() ?: 0f
                                val next = (cur - 5f).coerceAtLeast(0f)
                                currentSet.weightText = if (next % 1f == 0f) "${next.toInt()}" else "$next"
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Text("-5", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        OutlinedTextField(
                            value = currentSet.weightText,
                            onValueChange = { currentSet.weightText = it },
                            modifier = Modifier
                                .weight(1.8f)
                                .height(56.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        FilledTonalButton(
                            onClick = {
                                val cur = currentSet.weightText.toFloatOrNull() ?: 0f
                                val next = cur + 5f
                                currentSet.weightText = if (next % 1f == 0f) "${next.toInt()}" else "$next"
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Text("+5", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    // Micro Increment Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        listOf(-2.5f, +2.5f).forEach { delta ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clickable {
                                        val cur = currentSet.weightText.toFloatOrNull() ?: 0f
                                        val next = (cur + delta).coerceAtLeast(0f)
                                        currentSet.weightText = if (next % 1f == 0f) "${next.toInt()}" else "$next"
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                            ) {
                                Text(
                                    text = if (delta > 0) "+${delta} lbs" else "${delta} lbs",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // REPS STEPPER CONTROL
                    Text(
                        text = "Reps Completed",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = {
                                val cur = currentSet.repsText.toIntOrNull() ?: 0
                                currentSet.repsText = "${(cur - 1).coerceAtLeast(0)}"
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Text("-1", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        OutlinedTextField(
                            value = currentSet.repsText,
                            onValueChange = { currentSet.repsText = it },
                            modifier = Modifier
                                .weight(1.8f)
                                .height(56.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        FilledTonalButton(
                            onClick = {
                                val cur = currentSet.repsText.toIntOrNull() ?: 0
                                currentSet.repsText = "${cur + 1}"
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Text("+1", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // RPE & Joint Feel Interactive Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onOpenRpeDialog(currentSet, currentLog.exerciseName)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "RPE ${currentSet.rpe}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = if (currentSet.rpe == 8) "Optimal Stimulus" else "${10 - currentSet.rpe} RIR",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (currentSet.jointFeel == "Comfortable") MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    currentSet.jointFeel = if (currentSet.jointFeel == "Comfortable") "Mild Discomfort" else "Comfortable"
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (currentSet.jointFeel == "Comfortable") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = currentSet.jointFeel,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentSet.jointFeel == "Comfortable") MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        text = if (currentSet.jointFeel == "Comfortable") "Joints Happy" else "Reported",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // PRIMARY COMPLETE BUTTON (High-Impact Large Action with Spring Animation)
                    val coroutineScope = rememberCoroutineScope()
                    val buttonScaleAnim = remember { Animatable(1f) }
                    val animatedButtonColor by animateColorAsState(
                        targetValue = if (currentSet.isCompleted) Color(0xFF00C853) else MaterialTheme.colorScheme.primary,
                        animationSpec = tween(300),
                        label = "hud_btn_color"
                    )

                    Button(
                        onClick = {
                            currentSet.isCompleted = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            coroutineScope.launch {
                                buttonScaleAnim.animateTo(
                                    targetValue = 0.94f,
                                    animationSpec = tween(70)
                                )
                                buttonScaleAnim.animateTo(
                                    targetValue = 1.04f,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                )
                                buttonScaleAnim.animateTo(
                                    targetValue = 1f,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                                )
                            }
                            val restSeconds = when {
                                currentSet.rpe >= 9 -> 120
                                currentSet.rpe == 8 -> 90
                                else -> 60
                            }
                            onStartRestTimer(restSeconds, currentLog.exerciseName)
                            onCompleteSet(currentSet)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .scale(buttonScaleAnim.value)
                            .testTag("focus_hud_log_set_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = animatedButtonColor,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (currentSet.isCompleted) Icons.Default.CheckCircle else Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentSet.isCompleted) "Set ${currentSet.setNumber} Logged • Re-Log" else "Log Set ${currentSet.setNumber} & Rest",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
