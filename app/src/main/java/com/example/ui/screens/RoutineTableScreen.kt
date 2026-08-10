package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.mutableStateOf
import com.example.ui.components.ExerciseFormIllustrationBox
import com.example.ui.components.FloatingRestTimerBar
import com.example.ui.components.PreWorkoutMobilityCard
import com.example.ui.components.ProgressiveOverloadTag
import com.example.ui.components.parseRestPeriodToSeconds
import com.example.data.WorkoutExerciseEntity
import com.example.data.WorkoutRoutineEntity
import com.example.ui.theme.BoneDensityGold
import com.example.ui.theme.EmeraldTertiaryLight
import com.example.ui.theme.JointSafetyCoral
import com.example.ui.theme.PostureTeal

@Composable
fun RoutineTableScreen(
    routines: List<WorkoutRoutineEntity>,
    selectedRoutine: WorkoutRoutineEntity?,
    exercises: List<WorkoutExerciseEntity>,
    personalBests: Map<String, Float> = emptyMap(),
    onSelectRoutine: (WorkoutRoutineEntity) -> Unit,
    onStartLogging: (WorkoutRoutineEntity) -> Unit,
    onRegenerateProgram: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember(routines, selectedRoutine) {
        val index = routines.indexOfFirst { it.id == selectedRoutine?.id }
        mutableIntStateOf(if (index >= 0) index else 0)
    }

    var activeFormDemoExercise by remember { mutableStateOf<String?>(null) }
    var activeTimerExerciseName by remember { mutableStateOf<String?>(null) }
    var activeTimerInitialSeconds by remember { mutableIntStateOf(90) }
    var isTimerActive by remember { mutableStateOf(false) }

    val activeRoutine = if (routines.isNotEmpty() && selectedTabIndex < routines.size) {
        routines[selectedTabIndex]
    } else selectedRoutine

    val verticalScrollState = rememberScrollState()
    val tableHorizontalScrollState = rememberScrollState()

    // Box wrapper to anchor floating rest timer overlay
    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Main Screen Content
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
        // Top Day Selector Header
        if (routines.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                routines.forEachIndexed { index, r ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            onSelectRoutine(r)
                        },
                        text = {
                            Text(
                                text = "Day ${index + 1}",
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
                .padding(16.dp)
        ) {
            if (activeRoutine != null) {
                // Routine Header Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("routine_header_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = "Icon",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = activeRoutine.dayName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = activeRoutine.focusSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Tailored 5-Minute Pre-Workout Dynamic Mobility Warmup
                PreWorkoutMobilityCard(
                    exercises = exercises,
                    onWarmupCompleted = {}
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Section Label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Session Exercises",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${exercises.size} Exercises",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Table Container
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("workout_table_container"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(tableHorizontalScrollState)
                            .padding(12.dp)
                    ) {
                        // Table Header Row
                        Row(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableHeaderCell("Exercise", 160.dp)
                            TableHeaderCell("Primary Goal", 140.dp)
                            TableHeaderCell("Sets", 60.dp, TextAlign.Center)
                            TableHeaderCell("Reps", 100.dp, TextAlign.Center)
                            TableHeaderCell("Rest", 80.dp, TextAlign.Center)
                            TableHeaderCell("Focus / Form Cues", 200.dp)
                            TableHeaderCell("Form Demo", 100.dp, TextAlign.Center)
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                        )

                        // Table Content Rows
                        if (exercises.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .width(750.dp)
                                    .padding(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.material3.CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            exercises.forEachIndexed { idx, ex ->
                                Row(
                                    modifier = Modifier
                                        .background(
                                            if (idx % 2 == 0) MaterialTheme.colorScheme.surface
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                        )
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Column 1: Exercise
                                    Column(modifier = Modifier.width(160.dp)) {
                                        Text(
                                            text = ex.exerciseName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = ex.rpe,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            val prWeight = personalBests[ex.exerciseName] ?: 0f
                                            if (prWeight > 0f) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                ProgressiveOverloadTag(
                                                    currentPrLbs = prWeight,
                                                    isReadyForIncrement = true
                                                )
                                            }
                                        }
                                    }

                                    // Column 2: Primary Goal Badge
                                    Box(modifier = Modifier.width(140.dp)) {
                                        GoalBadge(goal = ex.primaryGoal)
                                    }

                                    // Column 3: Sets
                                    Text(
                                        text = "${ex.sets}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(60.dp),
                                        textAlign = TextAlign.Center
                                    )

                                    // Column 4: Reps
                                    Text(
                                        text = ex.repRange,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.width(100.dp),
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    // Column 5: Rest Interval (Interactive Timer Launcher)
                                    Box(
                                        modifier = Modifier.width(80.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
                                            modifier = Modifier.clickable {
                                                activeTimerExerciseName = ex.exerciseName
                                                activeTimerInitialSeconds = parseRestPeriodToSeconds(ex.restPeriod)
                                                isTimerActive = true
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Timer,
                                                    contentDescription = "Start Rest Timer",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = ex.restPeriod,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            }
                                        }
                                    }

                                    // Column 6: Focus / Cues
                                    Text(
                                        text = ex.coachingCues,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.width(200.dp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    // Column 7: Form Demo Action
                                    Box(
                                        modifier = Modifier.width(100.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            modifier = Modifier.clickable {
                                                activeFormDemoExercise = ex.exerciseName
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.SlowMotionVideo,
                                                    contentDescription = "View Form",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Demo",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                    }
                                }

                                if (idx < exercises.size - 1) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scroll Indicator Tip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Swipe table horizontally to view full parameters & coaching cues",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onRegenerateProgram,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("regenerate_program_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Icon")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Regenerate Program", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No routine selected.")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Floating Interactive Rest Timer Bar
    FloatingRestTimerBar(
        exerciseName = activeTimerExerciseName ?: "Rest Interval",
        initialSeconds = activeTimerInitialSeconds,
        isActive = isTimerActive,
        onDismiss = { isTimerActive = false },
        modifier = Modifier.align(Alignment.BottomCenter)
    )

    // Sticky Bottom Action (only show when timer is not active)
    if (!isTimerActive && activeRoutine != null) {
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = { onStartLogging(activeRoutine) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp)
                    .testTag("sticky_log_session_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Logging This Session", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

    // Popup Form Illustration Dialog
    if (activeFormDemoExercise != null) {
        AlertDialog(
            onDismissRequest = { activeFormDemoExercise = null },
            confirmButton = {
                TextButton(onClick = { activeFormDemoExercise = null }) {
                    Text("Close Demonstration", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                ExerciseFormIllustrationBox(
                    exerciseName = activeFormDemoExercise ?: "Barbell Compound Lift"
                )
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun TableHeaderCell(text: String, width: androidx.compose.ui.unit.Dp, textAlign: TextAlign = TextAlign.Start) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.width(width),
        textAlign = textAlign
    )
}

@Composable
fun GoalBadge(goal: String) {
    val (bgColor, textColor) = when {
        goal.contains("Bone", ignoreCase = true) -> Pair(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        goal.contains("Posture", ignoreCase = true) -> Pair(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
        goal.contains("Balance", ignoreCase = true) -> Pair(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
        goal.contains("Grip", ignoreCase = true) -> Pair(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = goal,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
