with open("app/src/main/java/com/example/ui/VitalViewModel.kt", "r") as f:
    content = f.read()

new_methods = """
    val bookmarkedExercises = repository.bookmarkedExercises.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addBookmark(exerciseId: String) {
        viewModelScope.launch {
            repository.addBookmark(exerciseId)
        }
    }

    fun removeBookmark(exerciseId: String) {
        viewModelScope.launch {
            repository.removeBookmark(exerciseId)
        }
    }

    init {"""

content = content.replace("    init {", new_methods)

with open("app/src/main/java/com/example/ui/VitalViewModel.kt", "w") as f:
    f.write(content)
