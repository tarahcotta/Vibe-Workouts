package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.os.CountDownTimer
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Mic
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.runtime.mutableFloatStateOf
import com.example.ui.components.BuiltInIntervalTimerCard
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
    val sets: MutableList<SetLogInput> = mutableStateListOf(),
    var formNotes: String = ""
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
    val context = LocalContext.current

    var activeDictationCallback by remember { mutableStateOf<((String) -> Unit)?>(null) }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                activeDictationCallback?.invoke(spokenText)
            }
        }
    }

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
                        repsText = ex.repRange.substringBefore("-").filter { it.isDigit() }.ifEmpty { "8" },
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
    var targetRestSeconds by remember { mutableIntStateOf(90) }
    var timerRemainingSeconds by remember { mutableIntStateOf(90) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var activeTimerExerciseName by remember { mutableStateOf("") }

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

    fun startRestTimer(seconds: Int, exerciseName: String = "") {
        targetRestSeconds = seconds
        timerRemainingSeconds = seconds
        if (exerciseName.isNotBlank()) {
            activeTimerExerciseName = exerciseName
        }
        isTimerRunning = true
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
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
                                text = "Track your progress and listen to your body",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = onCancel,
                            modifier = Modifier.size(48.dp) // WCAG touch target
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel Workout")
                        }
                    }
                }
            }
        },
        bottomBar = {
            val totalCompletedSets = exerciseLogs.sumOf { log -> log.sets.count { it.isCompleted } }
            val isFinishEnabled = totalCompletedSets > 0

            Surface(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 16.dp
            ) {
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

                        val combinedNotes = buildString {
                            exerciseLogs.forEach { log ->
                                if (log.formNotes.isNotBlank()) {
                                    append("[${log.exerciseName}]: ${log.formNotes}\n")
                                }
                            }
                            if (notesText.isNotBlank()) {
                                append(notesText)
                            }
                        }.trim()

                        onSaveSession(routineTitle, allLoggedSets, overallFeel, combinedNotes)
                        showCompletionDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp)
                        .testTag("finish_workout_button"),
                    enabled = isFinishEnabled,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFinishEnabled) "Finish & Save Session" else "Log a set to finish",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Live Personal Best Notification Banner
            PersonalBestNotificationBanner(
                exerciseName = prNotificationExercise ?: "",
                newWeightLbs = prNotificationNewWeight,
                previousMaxLbs = prNotificationOldMax,
                isVisible = showPrBanner,
                onDismiss = { showPrBanner = false }
            )

            // Built-In Rest Interval Timer Component (Now Collapsible UX)
            var isTimerExpanded by remember { mutableStateOf(false) }
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
                    .clickable { isTimerExpanded = !isTimerExpanded },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isTimerRunning) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) 
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Icon",
                                tint = if (isTimerRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isTimerRunning) "Resting: ${timerRemainingSeconds}s" else "Rest Timer",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isTimerExpanded) "Collapse" else "Expand View",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = if (isTimerExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    AnimatedVisibility(visible = isTimerExpanded || isTimerRunning) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            BuiltInIntervalTimerCard(
                                activeExerciseName = activeTimerExerciseName,
                                targetRestSeconds = targetRestSeconds,
                                isRunning = isTimerRunning,
                                remainingSeconds = timerRemainingSeconds,
                                onTogglePlayPause = { isTimerRunning = !isTimerRunning },
                                onResetTimer = { newTarget ->
                                    timerRemainingSeconds = newTarget
                                    isTimerRunning = true
                                },
                                onAdjustSeconds = { delta ->
                                    timerRemainingSeconds = (timerRemainingSeconds + delta).coerceAtLeast(0)
                                },
                                onPresetSelected = { seconds ->
                                    targetRestSeconds = seconds
                                    timerRemainingSeconds = seconds
                                    isTimerRunning = true
                                }
                            )
                        }
                    }
                }
            }

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
                        Column(modifier = Modifier.fillMaxWidth()) {
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

                                GoalBadge(goal = logState.primaryGoal)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.clickable { activeFormDemoExercise = logState.exerciseName }
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

                                val currentPr = personalBests[logState.exerciseName] ?: 0f
                                ProgressiveOverloadTag(
                                    currentPrLbs = currentPr,
                                    isReadyForIncrement = currentPr > 0f && logState.sets.all { it.isCompleted && it.rpe <= 8 }
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
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Lbs", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(72.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reps", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(72.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Joint Feel", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Log", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.width(48.dp))
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))

                        // Set Rows
                        logState.sets.forEachIndexed { setIndex, setInput ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
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
                                        .width(72.dp)
                                        .testTag("weight_input_${exIndex}_$setIndex"),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                // Reps input
                                OutlinedTextField(
                                    value = setInput.repsText,
                                    onValueChange = { setInput.repsText = it },
                                    modifier = Modifier
                                        .width(72.dp)
                                        .testTag("reps_input_${exIndex}_$setIndex"),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                )

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
                                        "Comfortable" -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
                                        "Mild Tension" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
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
                                            startRestTimer(targetRestSeconds, logState.exerciseName)
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
                                        .size(48.dp) // WCAG minimum touch target
                                        .background(
                                            if (setInput.isCompleted) MaterialTheme.colorScheme.primary 
                                            else MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .testTag("check_set_${exIndex}_$setIndex")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Complete Set",
                                        tint = if (setInput.isCompleted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                // RPE Slider
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 42.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "RPE: ${setInput.rpe}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(60.dp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Slider(
                                        value = setInput.rpe.toFloat(),
                                        onValueChange = { setInput.rpe = it.toInt() },
                                        valueRange = 1f..10f,
                                        steps = 8,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Add Set button & Per-Exercise Voice Form Notes
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Icon", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Set", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Per-Exercise Voice-to-Text Form Observations Field
                        OutlinedTextField(
                            value = logState.formNotes,
                            onValueChange = { logState.formNotes = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("exercise_form_notes_$exIndex"),
                            label = { Text("${logState.exerciseName} Post-Set Form Notes") },
                            placeholder = { Text("Tap mic to dictate form feedback or observations...") },
                            textStyle = MaterialTheme.typography.bodySmall,
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Dictate form notes for ${logState.exerciseName}")
                                        }
                                        activeDictationCallback = { text ->
                                            logState.formNotes = if (logState.formNotes.isNotBlank()) "${logState.formNotes} $text" else text
                                        }
                                        try {
                                            speechLauncher.launch(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Voice dictation not supported on this device", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.testTag("dictate_exercise_notes_$exIndex")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Dictate Form Notes",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )
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
                        placeholder = { Text("e.g. Felt great on Goblet Squats, increased weight +5 lbs") },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Dictate workout session coaching notes")
                                    }
                                    activeDictationCallback = { text ->
                                        notesText = if (notesText.isNotBlank()) "$notesText $text" else text
                                    }
                                    try {
                                        speechLauncher.launch(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Voice dictation not supported on this device", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.testTag("dictate_session_notes_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Dictate Notes",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
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
