package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.ui.theme.ThemeMode
import com.example.data.LoggedSetEntity
import com.example.data.LoggedWorkoutSessionEntity
import com.example.data.UserProfileEntity
import com.example.data.VitalDatabase
import com.example.data.VitalRepository
import com.example.data.WorkoutExerciseEntity
import com.example.data.WorkoutRoutineEntity
import com.example.ui.components.ProgressiveOverloadInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VitalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VitalRepository

    private val sharedPrefs = application.getSharedPreferences("vital_strength_prefs", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(
        when (sharedPrefs.getString("theme_mode", "SYSTEM")) {
            "LIGHT" -> ThemeMode.LIGHT
            "DARK" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    val userProfile: StateFlow<UserProfileEntity?>
    val activeRoutines: StateFlow<List<WorkoutRoutineEntity>>
    val allSessions: StateFlow<List<LoggedWorkoutSessionEntity>>
    val allLoggedSets: StateFlow<List<LoggedSetEntity>>
    val personalBests: StateFlow<Map<String, Float>>
    val progressiveOverloadList: StateFlow<List<ProgressiveOverloadInfo>>

    private val _selectedRoutine = MutableStateFlow<WorkoutRoutineEntity?>(null)
    val selectedRoutine: StateFlow<WorkoutRoutineEntity?> = _selectedRoutine.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedRoutineExercises: StateFlow<List<WorkoutExerciseEntity>>

    init {
        val dao = VitalDatabase.getDatabase(application).vitalDao()
        repository = VitalRepository(dao)

        userProfile = repository.userProfile.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        activeRoutines = repository.activeRoutines.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allSessions = repository.allSessions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allLoggedSets = repository.allLoggedSets.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        personalBests = repository.allLoggedSets.map { sets ->
            sets.groupBy { it.exerciseName }
                .mapValues { entry -> entry.value.maxOfOrNull { it.weightLbs } ?: 0f }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

        progressiveOverloadList = repository.allLoggedSets.map { sets: List<LoggedSetEntity> ->
            val resultList = mutableListOf<ProgressiveOverloadInfo>()
            val groupedByExercise: Map<String, List<LoggedSetEntity>> = sets.groupBy { it.exerciseName }
            for ((exerciseName, setList) in groupedByExercise) {
                var maxWeight = 0f
                var totalRpe = 0
                val recentSets = setList.take(5)
                for (set in setList) {
                    if (set.weightLbs > maxWeight) {
                        maxWeight = set.weightLbs
                    }
                }
                for (set in recentSets) {
                    totalRpe += set.rpeActual
                }
                val avgRpe = if (recentSets.isNotEmpty()) totalRpe / recentSets.size else 8
                val jointFeel = recentSets.firstOrNull()?.jointFeel ?: "Comfortable"
                val lowerName = exerciseName.lowercase()
                val isCompound = lowerName.contains("squat") || lowerName.contains("deadlift") ||
                        lowerName.contains("bench") || lowerName.contains("press") ||
                        lowerName.contains("row") || lowerName.contains("hip thrust") ||
                        lowerName.contains("lunge") || lowerName.contains("clean") ||
                        lowerName.contains("carry") || lowerName.contains("pull")

                val isReady = isCompound && avgRpe <= 8 && jointFeel != "Joint Strain"
                val reasoning = if (isReady) "RPE $avgRpe & $jointFeel joint feedback: primed for +5 lbs increment"
                else if (avgRpe >= 9) "High RPE ($avgRpe) - maintain weight to solidify motor patterns"
                else "Joint response: $jointFeel - preserve current load"

                resultList.add(
                    ProgressiveOverloadInfo(
                        exerciseName = exerciseName,
                        isCompoundLift = isCompound,
                        currentPrLbs = maxWeight,
                        recommendedNextWeightLbs = if (isReady) maxWeight + 5f else maxWeight,
                        lastRpe = avgRpe,
                        lastJointFeel = jointFeel,
                        isReadyToIncrement = isReady,
                        reasoning = reasoning
                    )
                )
            }
            resultList.sortedByDescending { it.isReadyToIncrement }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        @OptIn(ExperimentalCoroutinesApi::class)
        selectedRoutineExercises = _selectedRoutine.flatMapLatest { routine ->
            if (routine != null) {
                repository.getExercisesForRoutine(routine.id)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.ensureInitialDataLoaded()
        }
    }

    fun selectRoutine(routine: WorkoutRoutineEntity) {
        _selectedRoutine.value = routine
    }

    fun saveUserProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }

    fun regenerateRoutines() {
        viewModelScope.launch {
            val profile = userProfile.value ?: UserProfileEntity()
            repository.generateAndSaveRoutines(profile)
        }
    }

    fun logWorkoutSession(
        routineDayTitle: String,
        sets: List<LoggedSetEntity>,
        overallFeel: String,
        notes: String
    ) {
        viewModelScope.launch {
            val totalVolume = sets.sumOf { (it.weightLbs * it.repsCompleted).toDouble() }.toFloat()
            val session = LoggedWorkoutSessionEntity(
                routineDayTitle = routineDayTitle,
                dateTimestamp = System.currentTimeMillis(),
                totalVolumeLbs = totalVolume,
                totalSetsCompleted = sets.size,
                overallFeel = overallFeel,
                notes = notes
            )
            repository.logWorkoutSession(session, sets)
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
        }
    }

    fun getSetsForSession(sessionId: Long) = repository.getSetsForSession(sessionId)
    fun getMaxWeightForExercise(exerciseName: String) = repository.getMaxWeightForExercise(exerciseName)

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        sharedPrefs.edit().putString("theme_mode", mode.name).apply()
    }

    fun toggleThemeMode() {
        val nextMode = when (_themeMode.value) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
        }
        setThemeMode(nextMode)
    }
}
