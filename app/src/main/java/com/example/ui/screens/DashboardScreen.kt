package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoCamera
import coil.compose.AsyncImage
import java.io.File
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LoggedSetEntity
import com.example.data.LoggedWorkoutSessionEntity
import com.example.data.UserProfileEntity
import com.example.data.WorkoutRoutineEntity
import com.example.ui.VitalViewModel
import com.example.ui.components.AIRecommendationsCard
import com.example.ui.components.AuthSyncCard
import com.example.ui.components.FirstWorkoutOnboardingCard
import com.example.ui.components.ProgressiveOverloadHighlightCard
import com.example.ui.components.ProgressiveOverloadInfo
import com.example.ui.components.ScreenSkeletonLoader
import com.example.ui.components.WeightProgressionVicoChartCard
import com.example.ui.components.WomensStrengthLogoIcon
import com.example.ui.components.WorkoutCalendarSummaryCard
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Data model representing calculated weekly strength training habit frequency from Room database.
 */
data class WeeklyFrequencyDataPoint(
    val weekLabel: String,
    val weekNumber: Int,
    val year: Int,
    val sessionCount: Float,
    val targetCount: Float,
    val totalVolume: Float,
    val totalSets: Int,
    val isCurrentWeek: Boolean
)

/**
 * Data model representing calculated osteogenic bone density stimulus progress from Room database.
 */
data class BoneDensityTrendDataPoint(
    val dateTimestamp: Long,
    val dateFormatted: String,
    val routineTitle: String,
    val osteogenicScore: Float, // Calculated load index combining axial load, intensity, and volume
    val axialVolumeLbs: Float,
    val compoundSets: Int,
    val avgRpe: Float,
    val densityTier: String // "Optimal Remodeling", "Active Stimulus", "Maintenance"
)

@Composable
fun DashboardScreen(
    profile: UserProfileEntity?,
    routines: List<WorkoutRoutineEntity>,
    sessions: List<LoggedWorkoutSessionEntity>,
    overloadList: List<ProgressiveOverloadInfo> = emptyList(),
    viewModel: VitalViewModel? = null,
    onOpenAuthDialog: () -> Unit = {},
    onSelectRoutine: (WorkoutRoutineEntity) -> Unit,
    onNavigateToAssessment: () -> Unit,
    onNavigateToLogger: () -> Unit,
    onNavigateToGuide: () -> Unit,
    onNavigateToActivity: (() -> Unit)? = null,
    onNavigateToProgress: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val isLoading by (viewModel?.isDataLoading?.collectAsState() ?: remember { mutableStateOf(false) })
    val allLoggedSets by (viewModel?.allLoggedSets?.collectAsState() ?: remember { mutableStateOf(emptyList()) })

    if (isLoading) {
        ScreenSkeletonLoader(modifier = modifier)
        return
    }

    // Weekly Target from Room user profile (defaults to 3 days/week)
    val targetDaysPerWeek = profile?.scheduleDaysPerWeek ?: 3

    // 1. Process Weekly Frequency Data from Room database
    val weeklyFrequencyList = remember(sessions, targetDaysPerWeek) {
        calculateWeeklyFrequencyData(sessions, targetDaysPerWeek)
    }

    // 2. Process Bone Mineral Density Trends from Room database
    val boneDensityTrendsList = remember(sessions, allLoggedSets) {
        calculateBoneDensityTrends(sessions, allLoggedSets)
    }

    // Aggregate Summary Metrics
    val totalSessionsLogged = sessions.size
    val totalVolumeAllTime = sessions.sumOf { it.totalVolumeLbs.toDouble() }.toInt()

    // Current Week Adherence
    val currentWeekSessionCount = weeklyFrequencyList.lastOrNull()?.sessionCount?.toInt() ?: 0
    val adherencePercent = if (targetDaysPerWeek > 0) {
        ((currentWeekSessionCount.toFloat() / targetDaysPerWeek.toFloat()) * 100).toInt().coerceAtMost(100)
    } else 100

    // Latest Bone Density Stimulus Score
    val latestBmdScore = boneDensityTrendsList.lastOrNull()?.osteogenicScore?.toInt() ?: 0
    val avgBmdScore = if (boneDensityTrendsList.isNotEmpty()) {
        (boneDensityTrendsList.sumOf { it.osteogenicScore.toDouble() } / boneDensityTrendsList.size).toInt()
    } else 0

    var dashboardTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        if (viewModel != null) {
            AuthSyncCard(
                viewModel = viewModel,
                onOpenAuthDialog = onOpenAuthDialog
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Hero Dashboard Header Card - High-Impact Primary Action
        val nextRoutine = routines.firstOrNull()
        val nextRoutineTitle = nextRoutine?.dayName ?: nextRoutine?.title ?: "Full Body Axial Loading (Osteogenic)"
        val nextRoutineMins = 40
        val nextRoutineExercises = 4

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dashboard_hero_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            WomensStrengthLogoIcon(size = 44.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "READINESS • OPTIMAL FOR AXIAL LOAD",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = profile?.let { "${it.strengthLevel} Longevity Protocol" } ?: "Strength & Bone Longevity",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        IconButton(
                            onClick = onNavigateToAssessment,
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    CircleShape
                                )
                                .testTag("edit_assessment_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile & Goals",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Next Session Prescription Banner
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TODAY'S PRESCRIPTION",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "~$nextRoutineMins mins · $nextRoutineExercises exercises",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = nextRoutineTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Target: RPE 7–8 (2–3 Reps in Reserve) for osteogenic bone remodeling without joint strain.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4-Column Key Metric Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DashboardHeaderMetric(
                            label = "Habit Goal",
                            value = "$currentWeekSessionCount/$targetDaysPerWeek",
                            subtext = "This Wk"
                        )
                        DashboardHeaderMetric(
                            label = "Bone Stimulus",
                            value = if (latestBmdScore > 0) "${latestBmdScore / 1000}.${(latestBmdScore % 1000) / 100}k" else "--",
                            subtext = if (latestBmdScore >= 3000) "Optimal" else "Active"
                        )
                        DashboardHeaderMetric(
                            label = "Workouts",
                            value = "$totalSessionsLogged",
                            subtext = "Total Logged"
                        )
                        DashboardHeaderMetric(
                            label = "Lifetime Load",
                            value = "${totalVolumeAllTime / 1000}k",
                            subtext = "lbs lifted"
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Start Workout Button (Dominant Hero Action)
                    Button(
                        onClick = {
                            if (routines.isNotEmpty()) {
                                onSelectRoutine(routines.first())
                            }
                            onNavigateToLogger()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("start_workout_cta"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start Workout", modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Start Live Workout Session",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Segmented Tab Row: "Today's Plan & Habit" vs "Clinical Analytics & Trends"
        TabRow(
            selectedTabIndex = dashboardTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
        ) {
            Tab(
                selected = dashboardTab == 0,
                onClick = { dashboardTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Today's Plan & Habit", fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = dashboardTab == 1,
                onClick = { dashboardTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Clinical Trends & Charts", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (dashboardTab == 0) {
            // TAB 0: TODAY'S PLAN, PROGRESSIVE OVERLOAD, HISTORY & COACHING
            
            // Empty state interactive guide for new users
            if (sessions.isEmpty()) {
                FirstWorkoutOnboardingCard(
                    onStartWorkout = {
                        if (routines.isNotEmpty()) {
                            onSelectRoutine(routines.first())
                        }
                        onNavigateToLogger()
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Progressive Overload Readiness Card
            ProgressiveOverloadHighlightCard(
                overloadList = overloadList,
                onStartWorkout = {
                    if (routines.isNotEmpty()) {
                        onSelectRoutine(routines.first())
                    }
                    onNavigateToLogger()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calendar History Card
            WorkoutCalendarSummaryCard(
                sessions = sessions,
                routines = routines,
                onStartWorkout = { routine ->
                    if (routine != null) {
                        onSelectRoutine(routine)
                    } else if (routines.isNotEmpty()) {
                        onSelectRoutine(routines.first())
                    }
                    onNavigateToLogger()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // AI Recommendations & Science Insights
            AIRecommendationsCard(
                sessions = sessions,
                routines = routines
            )

            if (viewModel != null) {
                Spacer(modifier = Modifier.height(16.dp))
                LocalProgressPhotosMilestoneCard(
                    viewModel = viewModel,
                    onNavigateToPhotos = { onNavigateToProgress?.invoke() }
                )
            }
        } else {
            // TAB 1: CLINICAL ANALYTICS & DEEP CHARTS
            
            if (sessions.isEmpty()) {
                FirstWorkoutOnboardingCard(
                    onStartWorkout = {
                        if (routines.isNotEmpty()) {
                            onSelectRoutine(routines.first())
                        }
                        onNavigateToLogger()
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 1. STRENGTH TRAINING FREQUENCY VICO CHART CARD
            StrengthTrainingFrequencyVicoChartCard(
                weeklyFrequencyList = weeklyFrequencyList,
                targetDaysPerWeek = targetDaysPerWeek,
                totalSessionsLogged = totalSessionsLogged,
                onStartWorkout = {
                    if (routines.isNotEmpty()) {
                        onSelectRoutine(routines.first())
                    }
                    onNavigateToLogger()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. BONE DENSITY IMPROVEMENT TRENDS VICO CHART CARD
            BoneDensityTrendsVicoChartCard(
                boneDensityTrends = boneDensityTrendsList,
                onNavigateToGuide = onNavigateToGuide
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. BONE MINERAL DENSITY GOALS & CLINICAL MILESTONES
            BoneDensityGoalProgressCard(
                profile = profile,
                sessions = sessions,
                latestBmdScore = latestBmdScore,
                weeklyAdherence = adherencePercent,
                onNavigateToGuide = onNavigateToGuide
            )

            if (viewModel != null) {
                Spacer(modifier = Modifier.height(16.dp))
                WeightProgressionVicoChartCard(
                    allSessions = sessions,
                    allLoggedSets = allLoggedSets
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Science Guide Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToGuide() }
                .testTag("science_guide_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = "Health Science",
                        tint = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bone Density & Longevity Science",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "Why heavy axial loading (RPE 7-8) drives osteoblast remodeling in women 35+.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View Guide",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// =============================================================================
// VICO CHART 1: STRENGTH TRAINING FREQUENCY & HABIT CONSISTENCY
// =============================================================================

@Composable
fun StrengthTrainingFrequencyVicoChartCard(
    weeklyFrequencyList: List<WeeklyFrequencyDataPoint>,
    targetDaysPerWeek: Int,
    totalSessionsLogged: Int,
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRange by remember { mutableStateOf("Last 6 Weeks") }

    val filteredWeeks = remember(weeklyFrequencyList, selectedRange) {
        when (selectedRange) {
            "Last 4 Weeks" -> weeklyFrequencyList.takeLast(4)
            "Last 6 Weeks" -> weeklyFrequencyList.takeLast(6)
            else -> weeklyFrequencyList.takeLast(10)
        }
    }

    val modelProducer = remember { CartesianChartModelProducer() }
    val counts = remember(filteredWeeks) { filteredWeeks.map { it.sessionCount } }

    LaunchedEffect(counts) {
        modelProducer.runTransaction {
            lineSeries {
                series(counts)
            }
        }
    }

    // Adherence Stats Calculation
    val totalWeeksTracked = filteredWeeks.size
    val weeksMeetingGoal = filteredWeeks.count { it.sessionCount >= targetDaysPerWeek }
    val averageWorkoutsPerWeek = if (totalWeeksTracked > 0) {
        String.format(Locale.getDefault(), "%.1f", filteredWeeks.map { it.sessionCount }.average())
    } else "0.0"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("strength_frequency_vico_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Frequency",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Weekly Training Consistency",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Completed sessions vs $targetDaysPerWeek-day weekly target",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Consistency",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timeframe Selector Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Last 4 Weeks", "Last 6 Weeks", "All Time").forEach { range ->
                    FilterChip(
                        selected = (selectedRange == range),
                        onClick = { selectedRange = range },
                        label = { Text(range, fontSize = 12.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Key Statistics Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Target",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$targetDaysPerWeek days/wk",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Average",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$averageWorkoutsPerWeek sessions",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Goal Adherence",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$weeksMeetingGoal of $totalWeeksTracked wks",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (weeksMeetingGoal >= totalWeeksTracked / 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (totalSessionsLogged == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ShowChart,
                            contentDescription = "No Data",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No workouts logged yet.\nComplete your first session to track frequency trends!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onStartWorkout,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Log First Session")
                        }
                    }
                }
            } else {
                // Vico Frequency Line/Curve Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(vertical = 4.dp)
                ) {
                    CartesianChartHost(
                        chart = rememberCartesianChart(
                            rememberLineCartesianLayer(),
                            startAxis = VerticalAxis.rememberStart(),
                            bottomAxis = HorizontalAxis.rememberBottom(),
                        ),
                        modelProducer = modelProducer,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Weekly Breakdown Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredWeeks.forEach { week ->
                        val metGoal = week.sessionCount >= targetDaysPerWeek
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (metGoal) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (metGoal) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Goal Met",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = "${week.weekLabel}: ${week.sessionCount.toInt()} / ${targetDaysPerWeek} sessions",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (week.isCurrentWeek) FontWeight.Bold else FontWeight.Normal,
                                    color = if (metGoal) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// VICO CHART 2: BONE DENSITY IMPROVEMENT TRENDS
// =============================================================================

@Composable
fun BoneDensityTrendsVicoChartCard(
    boneDensityTrends: List<BoneDensityTrendDataPoint>,
    onNavigateToGuide: () -> Unit,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val scores = remember(boneDensityTrends) {
        if (boneDensityTrends.isNotEmpty()) {
            boneDensityTrends.map { it.osteogenicScore }
        } else {
            listOf(0f)
        }
    }

    LaunchedEffect(scores) {
        if (scores.isNotEmpty() && scores.first() > 0f) {
            modelProducer.runTransaction {
                lineSeries {
                    series(scores)
                }
            }
        }
    }

    val latestPoint = boneDensityTrends.lastOrNull()
    val peakScore = boneDensityTrends.maxOfOrNull { it.osteogenicScore } ?: 0f
    val initialScore = boneDensityTrends.firstOrNull()?.osteogenicScore ?: 0f
    val netGain = peakScore - initialScore

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bone_density_trends_vico_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Bone Density",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Bone Density Stimulus Trends",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Osteogenic loading stimulus score (BDSS) over time",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "Bone Stimulus",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Score explanation info
            Text(
                text = "The Bone Density Stimulus Score calculates axial mechanical tension on the spine & femur, progressive overload weight, and intensity (RPE 7-8) from your logged workouts.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (boneDensityTrends.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = "No Data",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No osteogenic logs recorded yet.\nLog axial lifts (Squats, Deadlifts, Presses) to track bone mineral remodeling!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onNavigateToGuide) {
                            Text("Learn Bone Density Science")
                        }
                    }
                }
            } else {
                // Key BMD Stats Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Current Score",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${latestPoint?.osteogenicScore?.toInt() ?: 0}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Peak Stimulus",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${peakScore.toInt()}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Stimulus Gain",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (netGain >= 0) "+${netGain.toInt()} pts" else "${netGain.toInt()} pts",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (netGain >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Vico Cartesian Line Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .padding(vertical = 4.dp)
                ) {
                    CartesianChartHost(
                        chart = rememberCartesianChart(
                            rememberLineCartesianLayer(),
                            startAxis = VerticalAxis.rememberStart(),
                            bottomAxis = HorizontalAxis.rememberBottom(),
                        ),
                        modelProducer = modelProducer,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Latest Session Stimulus Highlight
                if (latestPoint != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Latest: ${latestPoint.routineTitle} • ${latestPoint.dateFormatted}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Axial Volume: ${latestPoint.axialVolumeLbs.toInt()} lbs • ${latestPoint.compoundSets} compound sets @ RPE ${String.format(Locale.getDefault(), "%.1f", latestPoint.avgRpe)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = latestPoint.densityTier,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// BONE MINERAL DENSITY GOAL PROGRESS CARD
// =============================================================================

@Composable
fun BoneDensityGoalProgressCard(
    profile: UserProfileEntity?,
    sessions: List<LoggedWorkoutSessionEntity>,
    latestBmdScore: Int,
    weeklyAdherence: Int,
    onNavigateToGuide: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bone_density_goals_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessibilityNew,
                            contentDescription = "BMD Goals",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Bone Mineral Density Goals",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Clinical osteogenesis targets for longevity",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                TextButton(onClick = onNavigateToGuide) {
                    Text("Guide")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Goal 1: Axial Loading Intensity (Target 3,000 BDSS)
            val axialTarget = 3000f
            val axialProgress = (latestBmdScore.toFloat() / axialTarget).coerceIn(0f, 1f)
            BmdGoalProgressItem(
                title = "Axial Mechanical Stimulus (Spine & Hip)",
                currentText = "$latestBmdScore / 3,000 BDSS",
                progress = axialProgress,
                statusText = if (axialProgress >= 1f) "Optimal Zone" else "${(axialProgress * 100).toInt()}% Target"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Goal 2: Weekly Habit Adherence
            val habitProgress = (weeklyAdherence.toFloat() / 100f).coerceIn(0f, 1f)
            BmdGoalProgressItem(
                title = "Weekly Frequency Adherence",
                currentText = "$weeklyAdherence% of ${profile?.scheduleDaysPerWeek ?: 3} days target",
                progress = habitProgress,
                statusText = if (weeklyAdherence >= 100) "Goal Reached" else "$weeklyAdherence%"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Goal 3: Progressive Overload & RPE Target
            val totalSessions = sessions.size
            val consistencyProgress = (totalSessions.toFloat() / 12f).coerceIn(0f, 1f)
            BmdGoalProgressItem(
                title = "12-Week Bone Remodeling Cycle",
                currentText = "$totalSessions / 12 sessions completed",
                progress = consistencyProgress,
                statusText = if (totalSessions >= 12) "Cycle Finished" else "Week ${(totalSessions / 3) + 1}"
            )
        }
    }
}

@Composable
fun BmdGoalProgressItem(
    title: String,
    currentText: String,
    progress: Float,
    statusText: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (progress >= 1f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (progress >= 1f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = currentText,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DashboardHeaderMetric(label: String, value: String, subtext: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtext,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// =============================================================================
// CALCULATION HELPERS: ROOM DATABASE TRANSFORMATIONS
// =============================================================================

/**
 * Calculates weekly workout session frequency grouped by ISO week from Room sessions.
 */
fun calculateWeeklyFrequencyData(
    sessions: List<LoggedWorkoutSessionEntity>,
    targetDaysPerWeek: Int
): List<WeeklyFrequencyDataPoint> {
    val cal = Calendar.getInstance()
    val currentWeek = cal.get(Calendar.WEEK_OF_YEAR)
    val currentYear = cal.get(Calendar.YEAR)

    // Group sessions by "YEAR-WEEK"
    val grouped = sessions.groupBy { session ->
        val sCal = Calendar.getInstance().apply { timeInMillis = session.dateTimestamp }
        Pair(sCal.get(Calendar.YEAR), sCal.get(Calendar.WEEK_OF_YEAR))
    }

    val result = mutableListOf<WeeklyFrequencyDataPoint>()

    // Generate past 8 calendar weeks including current week
    for (i in 7 downTo 0) {
        val weekCal = Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, -i)
        }
        val yr = weekCal.get(Calendar.YEAR)
        val wk = weekCal.get(Calendar.WEEK_OF_YEAR)
        val weekSessions = grouped[Pair(yr, wk)] ?: emptyList()

        val label = if (i == 0) "This Wk" else "Wk $wk"
        val totalVolume = weekSessions.sumOf { it.totalVolumeLbs.toDouble() }.toFloat()
        val totalSets = weekSessions.sumOf { it.totalSetsCompleted }

        result.add(
            WeeklyFrequencyDataPoint(
                weekLabel = label,
                weekNumber = wk,
                year = yr,
                sessionCount = weekSessions.size.toFloat(),
                targetCount = targetDaysPerWeek.toFloat(),
                totalVolume = totalVolume,
                totalSets = totalSets,
                isCurrentWeek = (i == 0)
            )
        )
    }

    return result
}

/**
 * Calculates bone mineral density osteogenic load scores from Room sessions and logged sets.
 */
fun calculateBoneDensityTrends(
    sessions: List<LoggedWorkoutSessionEntity>,
    allLoggedSets: List<LoggedSetEntity>
): List<BoneDensityTrendDataPoint> {
    if (sessions.isEmpty()) return emptyList()

    val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    val setsBySession = allLoggedSets.groupBy { it.sessionId }

    return sessions.map { session ->
        val sets = setsBySession[session.id] ?: emptyList()

        var axialVolume = 0f
        var compoundSetCount = 0
        var totalRpe = 0

        for (set in sets) {
            val name = set.exerciseName.lowercase()
            val isAxialCompound = name.contains("squat") || name.contains("deadlift") ||
                    name.contains("press") || name.contains("lunge") ||
                    name.contains("hip thrust") || name.contains("carry") ||
                    name.contains("row") || name.contains("clean")

            val weightFactor = if (isAxialCompound) 1.5f else 1.0f
            val setVolume = set.weightLbs * set.repsCompleted * weightFactor
            axialVolume += setVolume
            if (isAxialCompound) compoundSetCount++
            totalRpe += set.rpeActual
        }

        val avgRpe = if (sets.isNotEmpty()) totalRpe.toFloat() / sets.size.toFloat() else 7.5f

        // Osteogenic stimulus score combines axial tonnage and intensity factor (RPE / 8.0)
        val rpeMultiplier = (avgRpe / 7.5f).coerceIn(0.8f, 1.4f)
        val baseVolume = if (axialVolume > 0f) axialVolume else session.totalVolumeLbs
        val osteogenicScore = (baseVolume * 0.85f * rpeMultiplier)

        val tier = when {
            osteogenicScore >= 3500f -> "Optimal Remodeling"
            osteogenicScore >= 2000f -> "Active Stimulus"
            else -> "Maintenance"
        }

        BoneDensityTrendDataPoint(
            dateTimestamp = session.dateTimestamp,
            dateFormatted = dateFormat.format(Date(session.dateTimestamp)),
            routineTitle = session.routineDayTitle,
            osteogenicScore = osteogenicScore,
            axialVolumeLbs = if (axialVolume > 0f) axialVolume else session.totalVolumeLbs,
            compoundSets = if (compoundSetCount > 0) compoundSetCount else session.totalSetsCompleted,
            avgRpe = avgRpe,
            densityTier = tier
        )
    }.sortedBy { it.dateTimestamp }
}

@Composable
fun LocalProgressPhotosMilestoneCard(
    viewModel: VitalViewModel,
    onNavigateToPhotos: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allPhotos by viewModel.allProgressPhotos.collectAsState()
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onNavigateToPhotos() }
            .testTag("local_progress_photos_milestone_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Visual Progress",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Visual Progress & Posture",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (allPhotos.isNotEmpty()) "${allPhotos.size} local checkpoints logged" else "Track posture and muscular changes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Offline",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Offline",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (allPhotos.isEmpty()) {
                Text(
                    text = "Capture baseline photos to visually evaluate thoracic alignment, posture improvements, and body composition changes safely offline.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onNavigateToPhotos,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Capture Baseline Photo")
                }
            } else {
                val latestPhoto = allPhotos.firstOrNull()
                val baselinePhoto = allPhotos.lastOrNull()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (baselinePhoto != null) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Baseline (${dateFormat.format(Date(baselinePhoto.dateTimestamp))})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black)
                            ) {
                                AsyncImage(
                                    model = File(baselinePhoto.filePath),
                                    contentDescription = "Baseline Photo",
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    if (latestPhoto != null && latestPhoto.id != baselinePhoto?.id) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Latest (${dateFormat.format(Date(latestPhoto.dateTimestamp))})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black)
                            ) {
                                AsyncImage(
                                    model = File(latestPhoto.filePath),
                                    contentDescription = "Latest Photo",
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "100% on-device storage guarantee",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    TextButton(
                        onClick = onNavigateToPhotos,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Compare & View All", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Open", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

