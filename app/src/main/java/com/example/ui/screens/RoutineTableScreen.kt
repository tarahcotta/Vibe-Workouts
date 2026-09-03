package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.WorkoutExerciseEntity
import com.example.data.WorkoutRoutineEntity
import com.example.ui.components.ExerciseVideoPlayerBox
import com.example.ui.components.FloatingRestTimerBar
import com.example.ui.components.PreWorkoutMobilityCard
import com.example.ui.components.ProgressiveOverloadTag
import com.example.ui.components.parseRestPeriodToSeconds

@Composable
fun RoutineTableScreen(
    routines: List<WorkoutRoutineEntity>,
    selectedRoutine: WorkoutRoutineEntity?,
    exercises: List<WorkoutExerciseEntity>,
    personalBests: Map<String, Float> = emptyMap(),
    onSelectRoutine: (WorkoutRoutineEntity) -> Unit,
    onStartLogging: (WorkoutRoutineEntity) -> Unit,
    onRegenerateProgram: () -> Unit,
    onUpdateNotes: (Long, String) -> Unit,
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

    // Box wrapper to anchor floating rest timer overlay & sticky CTA
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Main Screen Content
        Column(modifier = Modifier.fillMaxSize()) {
            // Fixed Top Day Selector Bar (Enclosed in a solid Surface with distinct divider)
            if (routines.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        ScrollableTabRow(
                            selectedTabIndex = selectedTabIndex,
                            edgePadding = 16.dp,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            indicator = { tabPositions ->
                                if (selectedTabIndex < tabPositions.size) {
                                    TabRowDefaults.SecondaryIndicator(
                                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                        color = MaterialTheme.colorScheme.primary,
                                        height = 3.dp
                                    )
                                }
                            },
                            divider = {}
                        ) {
                            routines.forEachIndexed { index, r ->
                                val isSelected = selectedTabIndex == index
                                val dayFocus = when {
                                    r.dayName.contains("Axial", ignoreCase = true) || r.dayName.contains("Lower", ignoreCase = true) || r.dayName.contains("Squat", ignoreCase = true) -> "Lower & Spine"
                                    r.dayName.contains("Hinge", ignoreCase = true) || r.dayName.contains("Grip", ignoreCase = true) || r.dayName.contains("Posterior", ignoreCase = true) || r.dayName.contains("Deadlift", ignoreCase = true) -> "Hinge & Grip"
                                    r.dayName.contains("Upper", ignoreCase = true) || r.dayName.contains("Press", ignoreCase = true) || r.dayName.contains("Pull", ignoreCase = true) -> "Upper & Posture"
                                    r.dayName.contains("Density", ignoreCase = true) || r.dayName.contains("Power", ignoreCase = true) -> "Osteogenic Core"
                                    index == 0 -> "Lower & Spine"
                                    index == 1 -> "Hinge & Grip"
                                    index == 2 -> "Upper & Posture"
                                    else -> "Compound Strength"
                                }
                                Tab(
                                    selected = isSelected,
                                    onClick = {
                                        selectedTabIndex = index
                                        onSelectRoutine(r)
                                    },
                                    text = {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "Day ${index + 1}",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = dayFocus,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                )
                            }
                        }
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Scrollable Content Column with sufficient bottom clearance
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                if (activeRoutine != null) {
                    // Routine Header Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("routine_header_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.FitnessCenter,
                                                contentDescription = "Workout Routine",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = activeRoutine.dayName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "Prescription: 3-4 Sets · Heavy Osteogenic Loading",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = activeRoutine.focusSummary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 5-Minute Pre-Workout Dynamic Mobility Warmup
                    PreWorkoutMobilityCard(
                        exercises = exercises,
                        onWarmupCompleted = {}
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Section Title & Count Badge (Fixed word wrapping bug)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Prescribed Exercises",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Targeting spine, hip & femur bone density stimulus",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (exercises.isEmpty()) "Loading..." else "${exercises.size} Exercises",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vertical List of Responsive Exercise Cards or Skeleton Loaders
                    if (exercises.isEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            repeat(3) {
                                OutlinedCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.outlinedCardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(
                                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                        )
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .width(140.dp)
                                                        .height(18.dp)
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .width(80.dp)
                                                    .height(20.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .width(96.dp)
                                                    .height(24.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .width(64.dp)
                                                    .height(24.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("workout_cards_container")
                        ) {
                            exercises.forEachIndexed { idx, ex ->
                                val (cleanTitle, altName) = parseExerciseTitle(ex.exerciseName)
                                val prWeight = personalBests[ex.exerciseName] ?: 0f

                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("exercise_card_${ex.id}"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.outlinedCardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(
                                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                        )
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        // Header Row: Exercise Name + Goal Badge
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = MaterialTheme.colorScheme.primaryContainer,
                                                        modifier = Modifier.size(24.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Text(
                                                                text = "${idx + 1}",
                                                                style = MaterialTheme.typography.labelSmall,
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                                            )
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = cleanTitle,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }

                                                if (altName != null) {
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = "Alternative: $altName",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                                                        modifier = Modifier.padding(start = 32.dp)
                                                    )
                                                }
                                            }

                                            GoalBadge(goal = ex.primaryGoal)
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Metric Parameter Chips Row (Sets, Reps, RPE, Rest)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Sets & Reps
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant
                                            ) {
                                                Text(
                                                    text = "${ex.sets} Sets × ${ex.repRange}",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }

                                            // RPE Target
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                            ) {
                                                Text(
                                                    text = ex.rpe,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }

                                            // Rest Interval (Interactive)
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
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Timer,
                                                        contentDescription = "Start Rest Timer",
                                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = ex.restPeriod,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                }
                                            }
                                        }

                                        // Optional Personal Record / Progression Tag
                                        if (prWeight > 0f) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "Previous Best: ${prWeight.toInt()} lbs",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                ProgressiveOverloadTag(
                                                    currentPrLbs = prWeight,
                                                    isReadyForIncrement = true
                                                )
                                            }
                                        }

                                        // Coaching Cues & Joint Protection Box
                                        if (ex.coachingCues.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(10.dp),
                                                    verticalAlignment = Alignment.Top
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Shield,
                                                        contentDescription = "Joint Safety & Form",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .padding(top = 1.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = ex.coachingCues,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        lineHeight = 18.sp
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                         // Action Row: Form Demo & Rest Timer Launch
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            FilledTonalButton(
                                                onClick = { activeFormDemoExercise = ex.exerciseName },
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.SmartDisplay,
                                                    contentDescription = "Watch Exercise Video",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Exercise Video",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    activeTimerExerciseName = ex.exerciseName
                                                    activeTimerInitialSeconds = parseRestPeriodToSeconds(ex.restPeriod)
                                                    isTimerActive = true
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.weight(1f),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Timer,
                                                    contentDescription = "Rest Interval Timer",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Rest Timer",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Optional Personal Note-Taking Field for Equipment, Feel, or Form
                                        var isEditingNotes by remember(ex.id) { mutableStateOf(false) }
                                        var noteInputText by remember(ex.id, ex.userNotes) { mutableStateOf(ex.userNotes) }

                                        if (isEditingNotes) {
                                            OutlinedTextField(
                                                value = noteInputText,
                                                onValueChange = { noteInputText = it },
                                                label = { Text("Exercise Notes (Equipment, how you felt, form)") },
                                                placeholder = { Text("e.g. Used 25lb dumbbells, felt great, kept hips square") },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("exercise_note_input_${ex.id}"),
                                                shape = RoundedCornerShape(12.dp),
                                                textStyle = MaterialTheme.typography.bodySmall,
                                                maxLines = 3
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                TextButton(
                                                    onClick = {
                                                        noteInputText = ex.userNotes
                                                        isEditingNotes = false
                                                    }
                                                ) {
                                                    Text("Cancel")
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Button(
                                                    onClick = {
                                                        onUpdateNotes(ex.id, noteInputText.trim())
                                                        isEditingNotes = false
                                                    },
                                                    modifier = Modifier.testTag("save_exercise_note_${ex.id}"),
                                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                                ) {
                                                    Text("Save Note", style = MaterialTheme.typography.labelMedium)
                                                }
                                            }
                                        } else {
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = if (ex.userNotes.isNotBlank()) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { isEditingNotes = true }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Icon(
                                                            imageVector = if (ex.userNotes.isNotBlank()) Icons.Default.Description else Icons.Default.EditNote,
                                                            contentDescription = null,
                                                            tint = if (ex.userNotes.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = if (ex.userNotes.isNotBlank()) "Note: ${ex.userNotes}" else "Add personal note (equipment, how you felt, form)...",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = if (ex.userNotes.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                                            maxLines = 2,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                    TextButton(
                                                        onClick = { isEditingNotes = true },
                                                        modifier = Modifier.height(48.dp),
                                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = if (ex.userNotes.isNotBlank()) "Edit" else "+ Add",
                                                            style = MaterialTheme.typography.labelMedium,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Secondary Action: Regenerate Program
                    OutlinedButton(
                        onClick = onRegenerateProgram,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("regenerate_program_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Regenerate Program")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Regenerate Program from Profile", fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No routine selected.", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                // Generous bottom spacer to prevent sticky CTA from masking the last cards
                Spacer(modifier = Modifier.height(96.dp))
            }
        }

        // Floating Interactive Rest Timer Bar (Anchored above sticky bottom)
        FloatingRestTimerBar(
            exerciseName = activeTimerExerciseName ?: "Rest Interval",
            initialSeconds = activeTimerInitialSeconds,
            isActive = isTimerActive,
            onDismiss = { isTimerActive = false },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Sticky Bottom Action (Full-width Material 3 Surface with shadow elevation)
        if (!isTimerActive && activeRoutine != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = { onStartLogging(activeRoutine) },
                        enabled = exercises.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("sticky_log_session_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        if (exercises.isNotEmpty()) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start Workout")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start Logging This Session",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Loading Prescribed Exercises...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }

    // Exercise Video Player Dialog
    if (activeFormDemoExercise != null) {
        Dialog(
            onDismissRequest = { activeFormDemoExercise = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(vertical = 16.dp)
            ) {
                ExerciseVideoPlayerBox(
                    exerciseName = activeFormDemoExercise ?: "Barbell Compound Lift",
                    onDismiss = { activeFormDemoExercise = null }
                )
            }
        }
    }
}

/**
 * Parses composite names like "Trap Bar / Dumbbell RDL" into a clean title & alternative tag.
 */
private fun parseExerciseTitle(rawName: String): Pair<String, String?> {
    return when {
        rawName.contains(" / ") -> {
            val parts = rawName.split(" / ")
            Pair(parts[0], parts.getOrNull(1))
        }
        rawName.contains(" or ") -> {
            val parts = rawName.split(" or ")
            Pair(parts[0], parts.getOrNull(1))
        }
        else -> Pair(rawName, null)
    }
}

@Composable
fun GoalBadge(goal: String) {
    val (bgColor, textColor) = when {
        goal.contains("Bone", ignoreCase = true) || goal.contains("Spine", ignoreCase = true) || goal.contains("Axial", ignoreCase = true) ->
            Pair(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
        goal.contains("Posture", ignoreCase = true) || goal.contains("Scapular", ignoreCase = true) ->
            Pair(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        goal.contains("Glute", ignoreCase = true) || goal.contains("Sacral", ignoreCase = true) ->
            Pair(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
        goal.contains("Grip", ignoreCase = true) || goal.contains("Core", ignoreCase = true) ->
            Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
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
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
