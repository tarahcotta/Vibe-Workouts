package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.os.CountDownTimer
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExerciseLibraryRepository
import com.example.data.LoggedSetEntity
import com.example.data.WorkoutExerciseEntity
import com.example.data.WorkoutRoutineEntity
import com.example.ui.components.AnimatedSetCompletionButton
import com.example.ui.components.BaselineCalibrationDialog
import com.example.ui.components.BuiltInIntervalTimerCard
import com.example.ui.components.ExerciseFormIllustrationBox
import com.example.ui.components.ExerciseSubstitutionDialog
import com.example.ui.components.PersonalBestNotificationBanner
import com.example.ui.components.PersonalRecordCelebrationDialog
import com.example.ui.components.PreWorkoutMobilityCard
import com.example.ui.components.ProgressiveOverloadTag
import com.example.ui.components.RpeEffortCalibrationDialog
import com.example.ui.components.SmartWarmupDialog
import com.example.ui.components.WarmupSetStep
import com.example.ui.components.WorkoutExitConfirmationDialog
import com.example.ui.components.WorkoutFocusHud

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
    val haptic = LocalHapticFeedback.current
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
    var showPrCelebrationDialog by remember { mutableStateOf(false) }

    // Routine Title
    val routineTitle = routine?.dayName ?: "Live Longevity Workout"

    // Exercise log states
    val exerciseLogs = remember(exercises) {
        val list = mutableStateListOf<ExerciseLogState>()
        exercises.forEach { ex ->
            val setInputs = mutableStateListOf<SetLogInput>()
            val initialWeight = personalBests[ex.exerciseName]?.let { if (it > 0f) "${it.toInt()}" else null } ?: "25"
            for (s in 1..ex.sets) {
                setInputs.add(
                    SetLogInput(
                        setNumber = s,
                        weightText = initialWeight,
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
    var showIncompleteSetsWarning by remember { mutableStateOf(false) }
    var showRpeInfoDialog by remember { mutableStateOf(false) }
    var activeFormDemoExercise by remember { mutableStateOf<String?>(null) }
    var rpePickerSet by remember { mutableStateOf<Pair<SetLogInput, String>?>(null) }
    
    // Deeper Redesign & Safety States
    var isFocusHudMode by remember { mutableStateOf(false) }
    var activeFocusExerciseIndex by remember { mutableIntStateOf(0) }
    var swapExerciseDialogTarget by remember { mutableStateOf<String?>(null) }
    var rpeCalibrationDialogSet by remember { mutableStateOf<Pair<SetLogInput, String>?>(null) }
    var showExitConfirmationDialog by remember { mutableStateOf(false) }
    var smartWarmupDialogTarget by remember { mutableStateOf<Pair<String, Float>?>(null) }
    var baselineCalibrationTarget by remember { mutableStateOf<String?>(null) }

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

    val totalCompletedSets = exerciseLogs.sumOf { log -> log.sets.count { it.isCompleted } }
    val totalPrescribedSets = exerciseLogs.sumOf { log -> log.sets.size }
    val isFinishEnabled = totalCompletedSets > 0

    val executeSaveAndComplete = {
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
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 3.dp,
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
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
                                text = "$totalCompletedSets of $totalPrescribedSets sets logged · Target RPE 7–8",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (totalCompletedSets > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                if (totalCompletedSets > 0) {
                                    showExitConfirmationDialog = true
                                } else {
                                    onCancel()
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("cancel_workout_button")
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close Workout")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // View Mode Switch: Focus HUD vs Detailed List
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clickable { isFocusHudMode = false },
                            shape = RoundedCornerShape(8.dp),
                            color = if (!isFocusHudMode) MaterialTheme.colorScheme.primary else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Full List View",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isFocusHudMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .clickable { isFocusHudMode = true },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isFocusHudMode) MaterialTheme.colorScheme.primary else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (isFocusHudMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Focus HUD (In-Workout)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isFocusHudMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (isFinishEnabled) {
                        Button(
                            onClick = {
                                val uncompletedCount = exerciseLogs.flatMap { it.sets }.count { !it.isCompleted }
                                if (uncompletedCount > 0) {
                                    showIncompleteSetsWarning = true
                                } else {
                                    executeSaveAndComplete()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("finish_workout_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Finish")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Finish Workout · $totalCompletedSets of $totalPrescribedSets Sets Done",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // High-contrast guidance state (WCAG AA compliant)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Pending Check",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Log at least 1 set to complete session",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (isFocusHudMode) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
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

                WorkoutFocusHud(
                    exerciseLogs = exerciseLogs,
                    activeExerciseIndex = activeFocusExerciseIndex,
                    onSelectExerciseIndex = { activeFocusExerciseIndex = it },
                    onOpenSwapDialog = { exName -> swapExerciseDialogTarget = exName },
                    onOpenRpeDialog = { set, exName -> rpeCalibrationDialogSet = Pair(set, exName) },
                    onStartRestTimer = { seconds, exName -> startRestTimer(seconds, exName) },
                    isTimerRunning = isTimerRunning,
                    timerRemainingSeconds = timerRemainingSeconds,
                    onAdjustTimer = { delta -> timerRemainingSeconds = (timerRemainingSeconds + delta).coerceAtLeast(0) },
                    onSkipTimer = {
                        timerRemainingSeconds = 0
                        isTimerRunning = false
                    },
                    onCompleteSet = { setInput ->
                        val currentEx = exerciseLogs.getOrNull(activeFocusExerciseIndex)
                        if (currentEx != null) {
                            val currentPr = personalBests[currentEx.exerciseName] ?: 0f
                            val loggedWeight = setInput.weightText.toFloatOrNull() ?: 0f
                            if (loggedWeight > currentPr && currentPr > 0f) {
                                prNotificationExercise = currentEx.exerciseName
                                prNotificationNewWeight = loggedWeight
                                prNotificationOldMax = currentPr
                                showPrBanner = true
                                showPrCelebrationDialog = true
                            }
                        }
                    }
                )
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Live Personal Best Notification Banner
                PersonalBestNotificationBanner(
                    exerciseName = prNotificationExercise ?: "",
                    newWeightLbs = prNotificationNewWeight,
                    previousMaxLbs = prNotificationOldMax,
                    isVisible = showPrBanner,
                    onDismiss = { showPrBanner = false }
                )

            // Built-In Rest Interval Timer Component (Collapsible UX & Direct Quick Controls)
            var isTimerExpanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isTimerRunning) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f).clickable { isTimerExpanded = !isTimerExpanded },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isTimerRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = "Timer",
                                        tint = if (isTimerRunning) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isTimerRunning) "Resting: ${timerRemainingSeconds}s" else "Rest Interval Timer",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTimerRunning) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isTimerRunning) activeTimerExerciseName.ifBlank { "Next Set Prep" } else "Auto-adjusts based on set RPE",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isTimerRunning) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isTimerRunning) {
                            // On-the-Fly Direct Rest Timer Adjusters
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                    modifier = Modifier.clickable {
                                        timerRemainingSeconds = (timerRemainingSeconds - 15).coerceAtLeast(0)
                                    }
                                ) {
                                    Text(
                                        text = "-15s",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                    modifier = Modifier.clickable {
                                        timerRemainingSeconds += 30
                                    }
                                ) {
                                    Text(
                                        text = "+30s",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = {
                                        timerRemainingSeconds = 0
                                        isTimerRunning = false
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Skip Rest",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        } else {
                            IconButton(
                                onClick = { isTimerExpanded = !isTimerExpanded },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isTimerExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Toggle",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = isTimerExpanded) {
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
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Exercise Cards Loop
            exerciseLogs.forEachIndexed { exIndex, logState ->
                val (cleanTitle, altName) = parseExerciseName(logState.exerciseName)
                val currentPr = personalBests[logState.exerciseName] ?: 0f

                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("exercise_log_card_$exIndex"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Exercise Header
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
                                                text = "${exIndex + 1}",
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

                            GoalBadge(goal = logState.primaryGoal)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Actions & Tags (Form Guide, PR Tag, Quick Fill)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
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

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
                                modifier = Modifier.clickable { swapExerciseDialogTarget = logState.exerciseName }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Swap Exercise",
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Swap",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                                modifier = Modifier.clickable { 
                                    val currentWeight = logState.sets.firstOrNull()?.weightText?.toFloatOrNull() ?: currentPr
                                    smartWarmupDialogTarget = Pair(logState.exerciseName, if (currentWeight > 0f) currentWeight else 95f)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FitnessCenter,
                                        contentDescription = "Warm-Up Ladder",
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Warmup",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }

                            // Safe Baseline Load Calibrator Chip
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
                                modifier = Modifier.clickable { 
                                    baselineCalibrationTarget = logState.exerciseName
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = "Calibrate Safe Working Weight",
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Calibrate",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }

                            ProgressiveOverloadTag(
                                currentPrLbs = currentPr,
                                isReadyForIncrement = currentPr > 0f && logState.sets.all { it.isCompleted && it.rpe <= 8 }
                            )

                            if (currentPr > 0f) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                                    modifier = Modifier.clickable {
                                        logState.sets.forEach { s ->
                                            if (!s.isCompleted) {
                                                s.weightText = "${currentPr.toInt()}"
                                            }
                                        }
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Fill ${currentPr.toInt()} lbs",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                        }

                        // Structured Coaching Cue Box
                        if (logState.coachingCues.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Joint Safety Cue",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = logState.coachingCues,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Column Headers Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Set", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Lbs", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(72.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reps", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.width(64.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Joint Feel", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Done", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.width(48.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

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
                                    // Set Number Indicator
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
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

                                    // Weight Input Field
                                    OutlinedTextField(
                                        value = setInput.weightText,
                                        onValueChange = { setInput.weightText = it },
                                        modifier = Modifier
                                            .width(72.dp)
                                            .testTag("weight_input_${exIndex}_$setIndex"),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Reps Input Field
                                    OutlinedTextField(
                                        value = setInput.repsText,
                                        onValueChange = { setInput.repsText = it },
                                        modifier = Modifier
                                            .width(64.dp)
                                            .testTag("reps_input_${exIndex}_$setIndex"),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Accessible Interactive Joint Feel Selector Pill (WCAG Compliant) with Semantic Iconography
                                    val jointBg = when (setInput.jointFeel) {
                                        "Comfortable" -> MaterialTheme.colorScheme.tertiaryContainer
                                        "Mild Tension" -> MaterialTheme.colorScheme.secondaryContainer
                                        else -> MaterialTheme.colorScheme.errorContainer
                                    }
                                    val jointFg = when (setInput.jointFeel) {
                                        "Comfortable" -> MaterialTheme.colorScheme.onTertiaryContainer
                                        "Mild Tension" -> MaterialTheme.colorScheme.onSecondaryContainer
                                        else -> MaterialTheme.colorScheme.onErrorContainer
                                    }
                                    val jointIcon = when (setInput.jointFeel) {
                                        "Comfortable" -> Icons.Default.Shield
                                        "Mild Tension" -> Icons.Default.Warning
                                        else -> Icons.Default.Warning
                                    }
                                    val jointLabel = when (setInput.jointFeel) {
                                        "Comfortable" -> "Normal"
                                        "Mild Tension" -> "Tension"
                                        else -> "Strain"
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .clickable {
                                                setInput.jointFeel = when (setInput.jointFeel) {
                                                    "Comfortable" -> "Mild Tension"
                                                    "Mild Tension" -> "Joint Strain"
                                                    else -> "Comfortable"
                                                }
                                            },
                                        shape = RoundedCornerShape(10.dp),
                                        color = jointBg,
                                        border = BorderStroke(1.dp, jointFg.copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = jointIcon,
                                                contentDescription = null,
                                                tint = jointFg,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "$jointLabel ▾",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = jointFg,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Complete Set Animated Button (Spring pop, ripple burst, auto-propagates forward)
                                    AnimatedSetCompletionButton(
                                        isCompleted = setInput.isCompleted,
                                        onToggle = {
                                            setInput.isCompleted = !setInput.isCompleted
                                            if (setInput.isCompleted) {
                                                // Auto-populate subsequent sets if they have default values
                                                logState.sets.forEachIndexed { i, s ->
                                                    if (i > setIndex && !s.isCompleted) {
                                                        s.weightText = setInput.weightText
                                                        s.repsText = setInput.repsText
                                                    }
                                                }
                                                val calculatedRest = when {
                                                    setInput.rpe >= 9 -> 120
                                                    setInput.rpe == 8 -> 90
                                                    else -> 60
                                                }
                                                startRestTimer(calculatedRest, logState.exerciseName)
                                                val currentWeight = setInput.weightText.toFloatOrNull() ?: 0f
                                                val previousMax = personalBests[logState.exerciseName] ?: 0f
                                                if (currentWeight > 0f && (previousMax == 0f || currentWeight > previousMax)) {
                                                    prNotificationExercise = logState.exerciseName
                                                    prNotificationNewWeight = currentWeight
                                                    prNotificationOldMax = previousMax
                                                    showPrBanner = true
                                                    showPrCelebrationDialog = true
                                                }
                                            }
                                        },
                                        size = 48.dp,
                                        testTag = "check_set_${exIndex}_$setIndex"
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Micro-Loading Steppers & Target RPE / RIR Selector Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 42.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Tactile Micro-Loading Chips
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf(-5f, 2.5f, 5f).forEach { delta ->
                                            Surface(
                                                modifier = Modifier.clickable {
                                                    val current = setInput.weightText.toFloatOrNull() ?: 0f
                                                    val newWeight = (current + delta).coerceAtLeast(0f)
                                                    setInput.weightText = if (newWeight % 1f == 0f) "${newWeight.toInt()}" else "$newWeight"
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                            ) {
                                                Text(
                                                    text = if (delta > 0f) "+$delta" else "$delta",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    // Contextual RPE & RIR Decoder Pill with direct info helper
                                    val rirCount = (10 - setInput.rpe).coerceAtLeast(0)
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (setInput.rpe in 7..8) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                        modifier = Modifier.clickable {
                                            rpePickerSet = Pair(setInput, logState.exerciseName)
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "RPE ${setInput.rpe} (${rirCount} RIR) ▾",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (setInput.rpe in 7..8) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Set Row Actions: Add Set & Apply Set 1 to All
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
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
                                modifier = Modifier.testTag("add_set_button_$exIndex")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Set", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Set", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                            }

                            if (logState.sets.size > 1) {
                                Surface(
                                    modifier = Modifier.clickable {
                                        val firstSet = logState.sets.firstOrNull()
                                        if (firstSet != null) {
                                            logState.sets.forEachIndexed { i, s ->
                                                if (i > 0) {
                                                    s.weightText = firstSet.weightText
                                                    s.repsText = firstSet.repsText
                                                }
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Match Sets",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Copy Set 1 to All",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Voice-to-Text Form Notes
                        OutlinedTextField(
                            value = logState.formNotes,
                            onValueChange = { logState.formNotes = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("exercise_form_notes_$exIndex"),
                            label = { Text("${cleanTitle} Post-Set Notes") },
                            placeholder = { Text("Tap mic to dictate form feedback or observations...") },
                            textStyle = MaterialTheme.typography.bodySmall,
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Dictate form notes for $cleanTitle")
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

            // Overall Session Feel & Notes Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
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
                            label = { Text(feel, fontWeight = if (overallFeel == feel) FontWeight.Bold else FontWeight.Normal) },
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
                        shape = RoundedCornerShape(12.dp),
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
}

    // Modal RPE Selector Dialog
    if (rpePickerSet != null) {
        val (targetSet, exerciseName) = rpePickerSet!!
        AlertDialog(
            onDismissRequest = { rpePickerSet = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Target RPE (Intensity)", fontWeight = FontWeight.Bold)
                    IconButton(
                        onClick = { showRpeInfoDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "RPE & RIR Guide",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            text = {
                Column {
                    Text(
                        text = "Rate of Perceived Exertion for Set ${targetSet.setNumber} on $exerciseName:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val rpeOptions = listOf(
                        Triple(6, "RPE 6 · 4+ RIR", "Warmup / speed work. Very low neural fatigue."),
                        Triple(7, "RPE 7 · 3 RIR", "Crisp velocity. Excellent form threshold for beginners."),
                        Triple(8, "RPE 8 · 2 RIR", "Optimal Osteogenic Zone · High axial load without breakdown."),
                        Triple(9, "RPE 9 · 1 RIR", "Near failure. 1 grinding rep left in reserve."),
                        Triple(10, "RPE 10 · 0 RIR", "Absolute max limit. Zero reps remaining.")
                    )

                    rpeOptions.forEach { (valRpe, title, sub) ->
                        val isSelected = targetSet.rpe == valRpe
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    targetSet.rpe = valRpe
                                    rpePickerSet = null
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (valRpe == 8) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    if (valRpe == 8) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primary
                                        ) {
                                            Text(
                                                text = "RECOMMENDED",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = sub,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { rpePickerSet = null }) {
                    Text("Cancel", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Comprehensive RPE & RIR Guide Dialog
    if (showRpeInfoDialog) {
        AlertDialog(
            onDismissRequest = { showRpeInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "RPE Guide",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("RPE & RIR Explained", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "RPE (Rate of Perceived Exertion) measures workout intensity. RIR (Reps in Reserve) is how many more clean reps you could complete before failure.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Text(
                        text = "Why RPE 7–8 for Bone Density?",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Clinical research shows that loading bones at 70–85% 1RM (RPE 7–8, 2–3 reps in reserve) creates peak osteogenic mechanotransduction while protecting spinal and joint integrity.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Text(
                        text = "Intensity Scale Quick Reference:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    listOf(
                        "RPE 6 (4+ RIR)" to "Light warmup. Weight moves briskly.",
                        "RPE 7 (3 RIR)" to "Moderate load. Bar speed is crisp.",
                        "RPE 8 (2 RIR)" to "Ideal Working Zone. 2 reps left in reserve.",
                        "RPE 9 (1 RIR)" to "Near maximal effort. 1 grinding rep left.",
                        "RPE 10 (0 RIR)" to "Absolute failure. No more reps possible."
                    ).forEach { (scale, desc) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "• $scale: ",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showRpeInfoDialog = false }) {
                    Text("Got It", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Incomplete Sets Warning Dialog
    if (showIncompleteSetsWarning) {
        val uncompletedSets = exerciseLogs.flatMap { it.sets }.count { !it.isCompleted }
        AlertDialog(
            onDismissRequest = { showIncompleteSetsWarning = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Incomplete Sets",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = "Finish Workout Early?",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = "You have $uncompletedSets set(s) left unlogged. Would you like to finish and record your $totalCompletedSets completed set(s), or return to logging?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showIncompleteSetsWarning = false
                        executeSaveAndComplete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Finish & Save Completed", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showIncompleteSetsWarning = false }) {
                    Text("Keep Training", fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Workout Completion Summary Dialog
    if (showCompletionDialog) {
        AlertDialog(
            onDismissRequest = { onCancel() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Workout Complete",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Workout Complete!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Great session! $totalCompletedSets sets recorded and saved to your on-device progression history.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Osteogenic Stimulus Logged",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Optimal axial load signals bone remodeling. Keep up the weekly consistency!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Return to Dashboard", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
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
                    exerciseName = activeFormDemoExercise ?: "Compound Lift"
                )
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Exercise Substitution / Spine-Sparing Alternatives Dialog
    if (swapExerciseDialogTarget != null) {
        ExerciseSubstitutionDialog(
            currentExerciseName = swapExerciseDialogTarget ?: "",
            onDismiss = { swapExerciseDialogTarget = null },
            onSelectAlternative = { newExerciseName ->
                val targetName = swapExerciseDialogTarget
                val foundIndex = exerciseLogs.indexOfFirst { it.exerciseName == targetName }
                if (foundIndex != -1) {
                    val currentSets = exerciseLogs[foundIndex].sets
                    val defaultEx = ExerciseLibraryRepository.exercises.firstOrNull { it.name.equals(newExerciseName, ignoreCase = true) }
                    exerciseLogs[foundIndex] = ExerciseLogState(
                        exerciseName = newExerciseName,
                        primaryGoal = defaultEx?.targetBonesAndJoints ?: "Osteogenic Compound",
                        coachingCues = defaultEx?.proFormTips?.joinToString("; ") ?: "Maintain neutral spine; Controlled tempo",
                        sets = currentSets
                    )
                }
                swapExerciseDialogTarget = null
            }
        )
    }

    // Comprehensive Interactive RPE & RIR Calibration Dialog
    if (rpeCalibrationDialogSet != null) {
        val (targetSet, exName) = rpeCalibrationDialogSet!!
        RpeEffortCalibrationDialog(
            currentRpe = targetSet.rpe,
            exerciseName = exName,
            onDismiss = { rpeCalibrationDialogSet = null },
            onSelectRpe = { selectedRpe ->
                targetSet.rpe = selectedRpe
                rpeCalibrationDialogSet = null
            }
        )
    }

    // Workout Exit Disambiguation Dialog
    if (showExitConfirmationDialog) {
        WorkoutExitConfirmationDialog(
            routineTitle = routineTitle,
            completedSetsCount = totalCompletedSets,
            totalSetsCount = totalPrescribedSets,
            onSavePartial = {
                showExitConfirmationDialog = false
                executeSaveAndComplete()
            },
            onDiscard = {
                showExitConfirmationDialog = false
                onCancel()
            },
            onKeepTraining = {
                showExitConfirmationDialog = false
            },
            onDismiss = {
                showExitConfirmationDialog = false
            }
        )
    }

    // Smart Warm-Up Progression Ladder Dialog
    if (smartWarmupDialogTarget != null) {
        val (targetExName, targetWorkingWeight) = smartWarmupDialogTarget!!
        SmartWarmupDialog(
            exerciseName = targetExName,
            workingWeightLbs = targetWorkingWeight,
            onDismiss = { smartWarmupDialogTarget = null },
            onApplyWarmupSets = { warmupSteps ->
                val foundIndex = exerciseLogs.indexOfFirst { it.exerciseName == targetExName }
                if (foundIndex != -1) {
                    val exLog = exerciseLogs[foundIndex]
                    // Prepend warmup sets to the existing sets
                    val newSets = mutableStateListOf<SetLogInput>()
                    warmupSteps.forEachIndexed { idx, step ->
                        newSets.add(
                            SetLogInput(
                                setNumber = idx + 1,
                                weightText = step.targetWeightLbs.toInt().toString(),
                                repsText = step.recommendedReps.toString(),
                                rpe = 5 + idx,
                                jointFeel = "Warmup",
                                isCompleted = false
                            )
                        )
                    }
                    // Append remaining working sets
                    exLog.sets.forEachIndexed { sIdx, existingSet ->
                        newSets.add(
                            SetLogInput(
                                setNumber = warmupSteps.size + sIdx + 1,
                                weightText = existingSet.weightText,
                                repsText = existingSet.repsText,
                                rpe = existingSet.rpe,
                                jointFeel = existingSet.jointFeel,
                                isCompleted = existingSet.isCompleted
                            )
                        )
                    }
                    exerciseLogs[foundIndex] = exLog.copy(sets = newSets)
                }
                smartWarmupDialogTarget = null
            }
        )
    }

    // Full-Screen / Modal Personal Record Confetti Celebration Dialog
    if (showPrCelebrationDialog && prNotificationExercise != null) {
        PersonalRecordCelebrationDialog(
            exerciseName = prNotificationExercise ?: "",
            newWeightLbs = prNotificationNewWeight,
            previousMaxLbs = prNotificationOldMax,
            onDismiss = { showPrCelebrationDialog = false }
        )
    }

    // Safe Baseline Strength Calibration Dialog
    if (baselineCalibrationTarget != null) {
        val targetExName = baselineCalibrationTarget!!
        BaselineCalibrationDialog(
            exerciseName = targetExName,
            onDismiss = { baselineCalibrationTarget = null },
            onApplyStartingWeight = { calibratedWeight ->
                val foundIndex = exerciseLogs.indexOfFirst { it.exerciseName == targetExName }
                if (foundIndex != -1) {
                    val exLog = exerciseLogs[foundIndex]
                    exLog.sets.forEach { setInput ->
                        if (!setInput.isCompleted) {
                            setInput.weightText = "${calibratedWeight.toInt()}"
                        }
                    }
                }
                baselineCalibrationTarget = null
            }
        )
    }
}

/**
 * Parses names like "Trap Bar / Dumbbell RDL" into primary title & subtitle alternative.
 */
private fun parseExerciseName(rawName: String): Pair<String, String?> {
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
