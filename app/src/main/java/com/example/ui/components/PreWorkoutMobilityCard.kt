package com.example.ui.components

import android.os.CountDownTimer
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.MobilityDrill
import com.example.data.MobilityRoutineManager
import com.example.data.WorkoutExerciseEntity

@Composable
fun PreWorkoutMobilityCard(
    exercises: List<WorkoutExerciseEntity>,
    modifier: Modifier = Modifier,
    onWarmupCompleted: () -> Unit = {}
) {
    val drills = remember(exercises) { MobilityRoutineManager.generateTailoredWarmup(exercises) }
    var expanded by remember { mutableStateOf(false) }
    val completedDrills = remember { mutableStateListOf<Int>() }

    // Optional 60s drill timer state
    var activeTimerDrillIndex by remember { mutableIntStateOf(-1) }
    var secondsRemaining by remember { mutableLongStateOf(60L) }
    var isTimerRunning by remember { mutableStateOf(false) }

    DisposableEffect(activeTimerDrillIndex, isTimerRunning) {
        var timer: CountDownTimer? = null
        if (isTimerRunning && activeTimerDrillIndex >= 0) {
            timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    secondsRemaining = millisUntilFinished / 1000
                }

                override fun onFinish() {
                    secondsRemaining = 0
                    isTimerRunning = false
                    if (!completedDrills.contains(activeTimerDrillIndex)) {
                        completedDrills.add(activeTimerDrillIndex)
                    }
                    if (completedDrills.size == drills.size) {
                        onWarmupCompleted()
                    }
                }
            }.start()
        }
        onDispose {
            timer?.cancel()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pre_workout_mobility_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Card Title Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = "Dynamic Mobility",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "5-Min Pre-Workout Dynamic Mobility",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${completedDrills.size}/${drills.size} drills completed • Tailored to today's lifts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Warmup",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Quick Status Pill & Action Button
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (completedDrills.size == drills.size) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (completedDrills.size == drills.size) Icons.Default.CheckCircle else Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (completedDrills.size == drills.size) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (completedDrills.size == drills.size) "Joints Primed & Ready!" else "Joint Lubrication & Warmup",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (completedDrills.size == drills.size) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.testTag("toggle_mobility_details_btn")
                ) {
                    Text(
                        text = if (expanded) "Hide Drills" else "View Drills",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Expanded Mobility Drills Section
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Spacer(modifier = Modifier.height(14.dp))

                    drills.forEachIndexed { index, drill ->
                        val isDone = completedDrills.contains(index)
                        val isCurrentTimer = activeTimerDrillIndex == index

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .clickable {
                                    if (isDone) completedDrills.remove(index) else completedDrills.add(
                                        index
                                    )
                                    if (completedDrills.size == drills.size) onWarmupCompleted()
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDone) MaterialTheme.colorScheme.primaryContainer.copy(
                                    alpha = 0.35f
                                ) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isDone) MaterialTheme.colorScheme.primary else Color.Transparent
                                                )
                                                .border(
                                                    width = 2.dp,
                                                    color = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isDone) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Completed",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = drill.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${drill.targetArea} • ${drill.durationOrReps}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }

                                    // Quick 60s drill timer button
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isCurrentTimer && isTimerRunning) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.clickable {
                                            if (isCurrentTimer && isTimerRunning) {
                                                isTimerRunning = false
                                            } else {
                                                activeTimerDrillIndex = index
                                                secondsRemaining = 60L
                                                isTimerRunning = true
                                            }
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 4.dp
                                            ),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (isCurrentTimer && isTimerRunning) Icons.Default.Timer else Icons.Default.PlayArrow,
                                                contentDescription = "Start 60s Timer",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isCurrentTimer) "${secondsRemaining}s" else "60s",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = drill.instructionCue,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "💡 Longevity Benefit: ${drill.primaryBenefit}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mark all complete / Reset
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (completedDrills.size == drills.size) {
                            OutlinedButton(
                                onClick = { completedDrills.clear() },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reset Warmup")
                            }
                        } else {
                            Button(
                                onClick = {
                                    completedDrills.clear()
                                    completedDrills.addAll(drills.indices)
                                    onWarmupCompleted()
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Mark Dynamic Warmup Complete")
                            }
                        }
                    }
                }
            }
        }
    }
}
