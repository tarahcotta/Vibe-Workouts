package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LoggedSetEntity
import com.example.data.LoggedWorkoutSessionEntity
import com.example.data.UserProfileEntity
import com.example.data.VitalDatabase
import com.example.data.VitalRepository
import com.example.data.WorkoutExerciseEntity
import com.example.data.WorkoutRoutineEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VitalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VitalRepository

    val userProfile: StateFlow<UserProfileEntity?>
    val activeRoutines: StateFlow<List<WorkoutRoutineEntity>>
    val allSessions: StateFlow<List<LoggedWorkoutSessionEntity>>

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
}
