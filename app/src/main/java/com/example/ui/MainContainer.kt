package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.example.data.WorkoutRoutineEntity
import com.example.ui.screens.ActiveLoggerScreen
import com.example.ui.screens.AssessmentScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LongevityGuideScreen
import com.example.ui.screens.ProgressAnalyticsScreen
import com.example.ui.screens.RoutineTableScreen

enum class NavDestination(
    val route: String,
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    HOME("home", "Dashboard", Icons.Filled.Home, Icons.Outlined.Home),
    TABLE("table", "Routine Table", Icons.Filled.GridOn, Icons.Outlined.GridOn),
    LOGGER("logger", "Live Logger", Icons.Filled.PlayCircleFilled, Icons.Outlined.PlayCircle),
    PROGRESS("progress", "Analytics", Icons.Filled.Timeline, Icons.Outlined.Timeline),
    GUIDE("guide", "Science Guide", Icons.Filled.HealthAndSafety, Icons.Outlined.HealthAndSafety),
    ASSESSMENT("assessment", "Assessment", Icons.Filled.Assignment, Icons.Outlined.Assignment)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    viewModel: VitalViewModel,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(NavDestination.HOME) }

    val profile by viewModel.userProfile.collectAsState()
    val routines by viewModel.activeRoutines.collectAsState()
    val sessions by viewModel.allSessions.collectAsState()
    val selectedRoutine by viewModel.selectedRoutine.collectAsState()
    val exercises by viewModel.selectedRoutineExercises.collectAsState()

    val navItems = listOf(
        NavDestination.HOME,
        NavDestination.TABLE,
        NavDestination.LOGGER,
        NavDestination.PROGRESS,
        NavDestination.GUIDE
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentDestination) {
                            NavDestination.HOME -> "VitalStrength • Longevity Coach"
                            NavDestination.TABLE -> "Longevity Workout Layout"
                            NavDestination.LOGGER -> "Live Workout Logger"
                            NavDestination.PROGRESS -> "Progressive Overload & History"
                            NavDestination.GUIDE -> "Longevity & Health Science"
                            NavDestination.ASSESSMENT -> "Strength & Health Assessment"
                        },
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
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
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
                        onSelectRoutine = { routine ->
                            viewModel.selectRoutine(routine)
                            currentDestination = NavDestination.TABLE
                        },
                        onNavigateToAssessment = { currentDestination = NavDestination.ASSESSMENT },
                        onNavigateToLogger = { currentDestination = NavDestination.LOGGER },
                        onNavigateToGuide = { currentDestination = NavDestination.GUIDE }
                    )
                }

                NavDestination.TABLE -> {
                    RoutineTableScreen(
                        routines = routines,
                        selectedRoutine = selectedRoutine ?: routines.firstOrNull(),
                        exercises = exercises,
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
                        exercises = exercises,
                        onSaveSession = { title, loggedSets, feel, notes ->
                            viewModel.logWorkoutSession(title, loggedSets, feel, notes)
                        },
                        onCancel = { currentDestination = NavDestination.HOME }
                    )
                }

                NavDestination.PROGRESS -> {
                    ProgressAnalyticsScreen(
                        viewModel = viewModel,
                        sessions = sessions,
                        onDeleteSession = { sessionId ->
                            viewModel.deleteSession(sessionId)
                        }
                    )
                }

                NavDestination.GUIDE -> {
                    LongevityGuideScreen()
                }

                NavDestination.ASSESSMENT -> {
                    AssessmentScreen(
                        currentProfile = profile,
                        onSaveProfile = { newProfile ->
                            viewModel.saveUserProfile(newProfile)
                        },
                        onNavigateToRoutines = {
                            currentDestination = NavDestination.TABLE
                        }
                    )
                }
            }
        }
    }
}
