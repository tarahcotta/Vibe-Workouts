package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.runtime.mutableFloatStateOf
import com.example.ui.components.ExerciseFormIllustrationBox
import com.example.ui.components.PersonalBestNotificationBanner
import com.example.ui.components.PreWorkoutMobilityCard
import com.example.ui.components.ProgressiveOverloadTag
import com.example.data.LoggedSetEntity
import com.example.data.WorkoutExerciseEntity
import com.example.data.WorkoutRoutineEntity

data class ExerciseLogState(
    val exerciseName: String,
    val primaryGoal: String,
    val coachingCues: String,
    val sets: MutableList<SetLogInput> = mutableStateListOf()
)

data class SetLogInput(
    var setNumber: Int,
    var weightText: String,
    var repsText: String,
    var rpe: Int = 8,
    var jointFeel: String = "Comfortable",
    var isCompleted: Boolean = false
)

@Composable
fun ActiveLoggerScreen(
    routine: WorkoutRoutineEntity?,
    exercises: List<WorkoutExerciseEntity>,
    personalBests: Map<String, Float> = emptyMap(),
    onSaveSession: (routineTitle: String, sets: List<LoggedSetEntity>, feel: String, notes: String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // PR Notification State
    var prNotificationExercise by remember { mutableStateOf<String?>(null) }
    var prNotificationNewWeight by remember { mutableFloatStateOf(0f) }
    var prNotificationOldMax by remember { mutableFloatStateOf(0f) }
    var showPrBanner by remember { mutableStateOf(false) }

    // Routine Title
    val routineTitle = routine?.dayName ?: "Live Longevity Workout"

    // Exercise log states
    val exerciseLogs = remember(exercises) {
        val list = mutableStateListOf<ExerciseLogState>()
        exercises.forEach { ex ->
            val setInputs = mutableStateListOf<SetLogInput>()
            for (s in 1..ex.sets) {
                setInputs.add(
                    SetLogInput(
                        setNumber = s,
                        weightText = "25",
                        repsText = ex.repRange.filter { it.isDigit() }.take(2).ifEmpty { "8" },
                        rpe = 8,
                        jointFeel = "Comfortable"
                    )
                )
            }
            list.add(
                ExerciseLogState(
                    exerciseName = ex.exerciseName,
                    primaryGoal = ex.primaryGoal,
                    coachingCues = ex.coachingCues,
                    sets = setInputs
                )
            )
        }
        list
    }

    var overallFeel by remember { mutableStateOf("Strong & Energized") }
    var notesText by remember { mutableStateOf("") }
    var showCompletionDialog by remember { mutableStateOf(false) }
    var activeFormDemoExercise by remember { mutableStateOf<String?>(null) }

    // Rest Timer state
    var timerRemainingSeconds by remember { mutableIntStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(false) }

    DisposableEffect(isTimerRunning, timerRemainingSeconds) {
        var timer: CountDownTimer? = null
        if (isTimerRunning && timerRemainingSeconds > 0) {
            timer = object : CountDownTimer((timerRemainingSeconds * 1000).toLong(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timerRemainingSeconds = (millisUntilFinished / 1000).toInt()
                }

                override fun onFinish() {
                    timerRemainingSeconds = 0
                    isTimerRunning = false
                }
            }.start()
        }
        onDispose {
            timer?.cancel()
        }
    }

    fun startRestTimer(seconds: Int) {
        timerRemainingSeconds = seconds
        isTimerRunning = true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sticky Header / Rest Timer
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = routineTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Log sets, weight, reps & joint response",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onCancel) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Active Rest Timer Bar
                if (timerRemainingSeconds > 0 || isTimerRunning) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Rest Timer: ${timerRemainingSeconds}s",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Row {
                                TextButton(onClick = { timerRemainingSeconds += 15 }) {
                                    Text("+15s", style = MaterialTheme.typography.labelSmall)
                                }
                                TextButton(onClick = {
                                    isTimerRunning = false
                                    timerRemainingSeconds = 0
                                }) {
                                    Text("Skip", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Main Form Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Live Personal Best Notification Banner
            PersonalBestNotificationBanner(
                exerciseName = prNotificationExercise ?: "",
                newWeightLbs = prNotificationNewWeight,
                previousMaxLbs = prNotificationOldMax,
                isVisible = showPrBanner,
                onDismiss = { showPrBanner = false }
            )

            // 5-Minute Pre-Workout Dynamic Mobility Routine
            PreWorkoutMobilityCard(
                exercises = exercises,
                modifier = Modifier.padding(bottom = 18.dp)
            )

            exerciseLogs.forEachIndexed { exIndex, logState ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("exercise_log_card_$exIndex"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${exIndex + 1}. ${logState.exerciseName}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .clickable { activeFormDemoExercise = logState.exerciseName }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SlowMotionVideo,
                                            contentDescription = "Form Guide",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Form Guide",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }

                                GoalBadge(goal = logState.primaryGoal)

                                val currentPr = personalBests[logState.exerciseName] ?: 0f
                                ProgressiveOverloadTag(
                                    currentPrLbs = currentPr,
                                    isReadyForIncrement = currentPr > 0f && logState.sets.all { it.isCompleted && it.rpe <= 8 },
                                    modifier = Modifier.padding(start = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Cue: ${logState.coachingCues}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Set Headers Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Set", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
                            Text("Lbs", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(70.dp))
                            Text("Reps", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(70.dp))
                            Text("RPE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp))
                            Text("Joint Feel", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text("Done", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(44.dp))
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Set Rows
                        logState.sets.forEachIndexed { setIndex, setInput ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Set #
                                Box(
                                    modifier = Modifier
                                        .width(36.dp)
                                        .height(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (setInput.isCompleted) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${setInput.setNumber}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (setInput.isCompleted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Weight input
                                OutlinedTextField(
                                    value = setInput.weightText,
                                    onValueChange = { setInput.weightText = it },
                                    modifier = Modifier
                                        .width(64.dp)
                                        .testTag("weight_input_${exIndex}_$setIndex"),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                // Reps input
                                OutlinedTextField(
                                    value = setInput.repsText,
                                    onValueChange = { setInput.repsText = it },
                                    modifier = Modifier
                                        .width(64.dp)
                                        .testTag("reps_input_${exIndex}_$setIndex"),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                // RPE Button
                                Surface(
                                    modifier = Modifier
                                        .width(52.dp)
                                        .height(38.dp)
                                        .clickable {
                                            val nextRpe = if (setInput.rpe >= 10) 6 else setInput.rpe + 1
                                            setInput.rpe = nextRpe
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "@${setInput.rpe}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Joint feel selector
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .clickable {
                                            setInput.jointFeel = when (setInput.jointFeel) {
                                                "Comfortable" -> "Mild Tension"
                                                "Mild Tension" -> "Joint Strain"
                                                else -> "Comfortable"
                                            }
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (setInput.jointFeel) {
                                        "Comfortable" -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                                        "Mild Tension" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                                        else -> MaterialTheme.colorScheme.errorContainer
                                    }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = setInput.jointFeel,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = when (setInput.jointFeel) {
                                                "Comfortable" -> MaterialTheme.colorScheme.onTertiaryContainer
                                                "Mild Tension" -> MaterialTheme.colorScheme.onSecondaryContainer
                                                else -> MaterialTheme.colorScheme.onErrorContainer
                                            },
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Complete Set Checkbox
                                IconButton(
                                    onClick = {
                                        setInput.isCompleted = !setInput.isCompleted
                                        if (setInput.isCompleted) {
                                            startRestTimer(90)
                                            val currentWeight = setInput.weightText.toFloatOrNull() ?: 0f
                                            val previousMax = personalBests[logState.exerciseName] ?: 0f
                                            if (currentWeight > 0f && (previousMax == 0f || currentWeight > previousMax)) {
                                                prNotificationExercise = logState.exerciseName
                                                prNotificationNewWeight = currentWeight
                                                prNotificationOldMax = previousMax
                                                showPrBanner = true
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("check_set_${exIndex}_$setIndex")
                                ) {
                                    Icon(
                                        imageVector = if (setInput.isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                        contentDescription = "Complete Set",
                                        tint = if (setInput.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }

                        // Add Set button
                        TextButton(
                            onClick = {
                                logState.sets.add(
                                    SetLogInput(
                                        setNumber = logState.sets.size + 1,
                                        weightText = logState.sets.lastOrNull()?.weightText ?: "25",
                                        repsText = logState.sets.lastOrNull()?.repsText ?: "8",
                                        rpe = 8
                                    )
                                )
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Set", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Overall Session Feel & Notes
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Session Energy & Joint Response",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val feelOptions = listOf("Strong & Energized", "Challenging but Good", "Joint Discomfort / Scaled")
                    feelOptions.forEach { feel ->
                        FilterChip(
                            selected = overallFeel == feel,
                            onClick = { overallFeel = feel },
                            label = { Text(feel) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("session_notes_input"),
                        label = { Text("Coaching Notes / Joint Observations") },
                        placeholder = { Text("e.g. Felt great on Goblet Squats, increased weight +5 lbs") }
                    )
                }
            }

            // Finish Workout Button
            Button(
                onClick = {
                    val allLoggedSets = mutableListOf<LoggedSetEntity>()
                    exerciseLogs.forEach { log ->
                        log.sets.forEach { setInput ->
                            val w = setInput.weightText.toFloatOrNull() ?: 0f
                            val r = setInput.repsText.toIntOrNull() ?: 0
                            allLoggedSets.add(
                                LoggedSetEntity(
                                    sessionId = 0,
                                    exerciseName = log.exerciseName,
                                    setNumber = setInput.setNumber,
                                    weightLbs = w,
                                    repsCompleted = r,
                                    rpeActual = setInput.rpe,
                                    jointFeel = setInput.jointFeel
                                )
                            )
                        }
                    }
                    onSaveSession(routineTitle, allLoggedSets, overallFeel, notesText)
                    showCompletionDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("finish_workout_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Complete & Log Workout",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showCompletionDialog) {
        AlertDialog(
            onDismissRequest = { onCancel() },
            title = {
                Text(
                    text = "Workout Logged!",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Excellent work! Your sets, weight, reps, and joint safety markers have been recorded in your progressive overload history.")
            },
            confirmButton = {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Return to Dashboard")
                }
            }
        )
    }

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
                    exerciseName = activeFormDemoExercise ?: "Compound Lift"
                )
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}
