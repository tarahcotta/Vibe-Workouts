package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FitnessCenter
import com.example.ui.components.AuthDialog
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.example.ui.theme.ThemeMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.WorkoutRoutineEntity
import com.example.ui.components.WomensStrengthLogoIcon
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.MenuBook
import com.example.ui.screens.ActiveLoggerScreen
import com.example.ui.screens.AssessmentScreen
import com.example.ui.screens.ExerciseLibraryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LongevityGuideScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PlateCalculatorScreen
import com.example.ui.screens.ProfileSetupScreen
import com.example.ui.screens.ProgressAnalyticsScreen
import com.example.ui.screens.RecentActivitySummaryScreen
import com.example.ui.screens.RoutineTableScreen

enum class NavDestination(
    val route: String,
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    HOME("home", "Dashboard", Icons.Filled.Home, Icons.Outlined.Home),
    LIBRARY("library", "Library", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
    ACTIVITY("activity", "Activity", Icons.Filled.History, Icons.Outlined.History),
    TABLE("table", "Routines", Icons.Filled.GridOn, Icons.Outlined.GridOn),
    LOGGER("logger", "Live Logger", Icons.Filled.PlayCircleFilled, Icons.Outlined.PlayCircle),
    PROGRESS("progress", "Analytics", Icons.Filled.Timeline, Icons.Outlined.Timeline),
    PLATE_CALC("plate_calc", "Plate Calc", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter),
    GUIDE("guide", "Science Guide", Icons.Filled.HealthAndSafety, Icons.Outlined.HealthAndSafety),
    ASSESSMENT("assessment", "Assessment", Icons.Filled.Assignment, Icons.Outlined.Assignment),
    PROFILE_SETUP("profile_setup", "Profile Setup", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    viewModel: VitalViewModel,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(NavDestination.HOME) }
    var customExercisesForSession by remember { mutableStateOf<List<com.example.data.WorkoutExerciseEntity>>(emptyList()) }
    var showThemeMenu by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }

    val themeMode by viewModel.themeMode.collectAsState()
    val hasCompletedOnboarding by viewModel.hasCompletedOnboarding.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val routines by viewModel.activeRoutines.collectAsState()
    val sessions by viewModel.allSessions.collectAsState()
    val selectedRoutine by viewModel.selectedRoutine.collectAsState()
    val exercises by viewModel.selectedRoutineExercises.collectAsState()
    val personalBests by viewModel.personalBests.collectAsState()
    val overloadList by viewModel.progressiveOverloadList.collectAsState()

    if (!hasCompletedOnboarding) {
        OnboardingScreen(
            onCompleteOnboarding = { viewModel.completeOnboarding() },
            modifier = modifier
        )
        return
    }

    val navItems = listOf(
        NavDestination.HOME,
        NavDestination.LIBRARY,
        NavDestination.TABLE,
        NavDestination.LOGGER,
        NavDestination.PROGRESS
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        WomensStrengthLogoIcon(size = 28.dp)
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 10.dp))
                        Text(
                            text = when (currentDestination) {
                                NavDestination.HOME -> "Strength & Longevity"
                                NavDestination.LIBRARY -> "Strength Exercise Library"
                                NavDestination.ACTIVITY -> "Recent Workout Activity"
                                NavDestination.TABLE -> "Routines"
                                NavDestination.LOGGER -> "Live Workout Logger"
                                NavDestination.PROGRESS -> "Analytics"
                                NavDestination.PLATE_CALC -> "Barbell Plate Calculator"
                                NavDestination.GUIDE -> "Longevity & Health Science"
                                NavDestination.ASSESSMENT -> "Strength & Health Assessment"
                                NavDestination.PROFILE_SETUP -> "Firebase Profile Setup"
                            },
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.replayOnboarding() },
                        modifier = Modifier.testTag("onboarding_replay_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Replay Progressive Overload Science Onboarding",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { currentDestination = NavDestination.PROFILE_SETUP },
                        modifier = Modifier.testTag("auth_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Firebase Auth & Cloud Sync",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Box {
                        IconButton(
                            onClick = { showThemeMenu = true },
                            modifier = Modifier.testTag("theme_toggle_button")
                        ) {
                            Icon(
                                imageVector = when (themeMode) {
                                    ThemeMode.LIGHT -> Icons.Default.LightMode
                                    ThemeMode.DARK -> Icons.Default.DarkMode
                                    ThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                                },
                                contentDescription = "Toggle Theme Mode"
                            )
                        }

                        DropdownMenu(
                            expanded = showThemeMenu,
                            onDismissRequest = { showThemeMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Light Mode") },
                                onClick = {
                                    viewModel.setThemeMode(ThemeMode.LIGHT)
                                    showThemeMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.LightMode, contentDescription = null)
                                },
                                modifier = Modifier.testTag("theme_option_light")
                            )
                            DropdownMenuItem(
                                text = { Text("Dark Mode") },
                                onClick = {
                                    viewModel.setThemeMode(ThemeMode.DARK)
                                    showThemeMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.DarkMode, contentDescription = null)
                                },
                                modifier = Modifier.testTag("theme_option_dark")
                            )
                            DropdownMenuItem(
                                text = { Text("System Default") },
                                onClick = {
                                    viewModel.setThemeMode(ThemeMode.SYSTEM)
                                    showThemeMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.SettingsBrightness, contentDescription = null)
                                },
                                modifier = Modifier.testTag("theme_option_system")
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            if (currentDestination != NavDestination.LOGGER && currentDestination != NavDestination.ASSESSMENT) {
                NavigationBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    navItems.forEach { item ->
                        val isSelected = currentDestination == item
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentDestination = item },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            modifier = Modifier.testTag("nav_item_${item.route}")
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (showAuthDialog) {
            AuthDialog(
                viewModel = viewModel,
                onDismiss = { showAuthDialog = false }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                NavDestination.HOME -> {
                    HomeScreen(
                        profile = profile,
                        routines = routines,
                        sessions = sessions,
                        overloadList = overloadList,
                        viewModel = viewModel,
                        onOpenAuthDialog = { showAuthDialog = true },
                        onSelectRoutine = { routine ->
                            viewModel.selectRoutine(routine)
                            currentDestination = NavDestination.TABLE
                        },
                        onNavigateToAssessment = { currentDestination = NavDestination.ASSESSMENT },
                        onNavigateToLogger = { currentDestination = NavDestination.LOGGER },
                        onNavigateToGuide = { currentDestination = NavDestination.GUIDE },
                        onNavigateToActivity = { currentDestination = NavDestination.ACTIVITY }
                    )
                }

                NavDestination.LIBRARY -> {
                    ExerciseLibraryScreen(
                        onSelectExerciseForWorkout = { exercise ->
                            if (routines.isNotEmpty() && selectedRoutine == null) {
                                viewModel.selectRoutine(routines.first())
                            }
                            val newEx = com.example.data.WorkoutExerciseEntity(
                                id = 0,
                                routineId = selectedRoutine?.id ?: routines.firstOrNull()?.id ?: 0L,
                                exerciseName = exercise.name,
                                primaryGoal = exercise.healthFocusCategory.displayName,
                                sets = 3,
                                repRange = "8-12",
                                restPeriod = "90-120s",
                                rpe = "RPE 7-8",
                                coachingCues = exercise.proFormTips.firstOrNull() ?: "",
                                orderIndex = exercises.size + customExercisesForSession.size
                            )
                            customExercisesForSession = customExercisesForSession + newEx
                            currentDestination = NavDestination.LOGGER
                        }
                    )
                }

                NavDestination.ACTIVITY -> {
                    RecentActivitySummaryScreen(
                        viewModel = viewModel,
                        sessions = sessions,
                        onDeleteSession = { sessionId ->
                            viewModel.deleteSession(sessionId)
                        },
                        onStartNewWorkout = {
                            if (routines.isNotEmpty()) {
                                viewModel.selectRoutine(routines.first())
                            }
                            currentDestination = NavDestination.LOGGER
                        }
                    )
                }

                NavDestination.TABLE -> {
                    RoutineTableScreen(
                        routines = routines,
                        selectedRoutine = selectedRoutine ?: routines.firstOrNull(),
                        exercises = exercises,
                        personalBests = personalBests,
                        onSelectRoutine = { viewModel.selectRoutine(it) },
                        onStartLogging = { routine ->
                            viewModel.selectRoutine(routine)
                            currentDestination = NavDestination.LOGGER
                        },
                        onRegenerateProgram = {
                            viewModel.regenerateRoutines()
                        }
                    )
                }

                NavDestination.LOGGER -> {
                    ActiveLoggerScreen(
                        routine = selectedRoutine ?: routines.firstOrNull(),
                        exercises = exercises + customExercisesForSession,
                        personalBests = personalBests,
                        onSaveSession = { title, loggedSets, feel, notes ->
                            viewModel.logWorkoutSession(title, loggedSets, feel, notes)
                            customExercisesForSession = emptyList()
                            currentDestination = NavDestination.HOME
                        },
                        onCancel = { 
                            currentDestination = NavDestination.HOME
                            customExercisesForSession = emptyList()
                        }
                    )
                }

                NavDestination.PROGRESS -> {
                    ProgressAnalyticsScreen(
                        viewModel = viewModel,
                        sessions = sessions,
                        onDeleteSession = { sessionId ->
                            viewModel.deleteSession(sessionId)
                        },
                        onStartWorkout = { routine ->
                            if (routine != null) {
                                viewModel.selectRoutine(routine)
                            }
                            currentDestination = NavDestination.LOGGER
                        }
                    )
                }

                NavDestination.PLATE_CALC -> {
                    PlateCalculatorScreen(
                        onNavigateBack = { currentDestination = NavDestination.HOME }
                    )
                }

                NavDestination.GUIDE -> {
                    LongevityGuideScreen()
                }

                NavDestination.ASSESSMENT -> {
                    AssessmentScreen(
                        currentProfile = profile,
                        themeMode = themeMode,
                        onThemeModeChange = { viewModel.setThemeMode(it) },
                        onSaveProfile = { newProfile ->
                            viewModel.saveUserProfile(newProfile)
                        },
                        onNavigateToRoutines = {
                            currentDestination = NavDestination.TABLE
                        }
                    )
                }

                NavDestination.PROFILE_SETUP -> {
                    ProfileSetupScreen(
                        viewModel = viewModel,
                        onOpenAuthDialog = { showAuthDialog = true },
                        onNavigateBack = { currentDestination = NavDestination.HOME }
                    )
                }
            }
        }
    }
}
