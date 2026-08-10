import re

with open("app/src/main/java/com/example/ui/VitalViewModel.kt", "r") as f:
    content = f.read()

bad_var = """    val bookmarkedExercises = repository.bookmarkedExercises.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )"""

content = content.replace(bad_var, "    lateinit var bookmarkedExercises: StateFlow<List<BookmarkedExerciseEntity>>")

init_assignment = """
        bookmarkedExercises = repository.bookmarkedExercises.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
"""
content = content.replace("        userProfile = repository.userProfile.stateIn(", init_assignment + "        userProfile = repository.userProfile.stateIn(")

if "import com.example.data.BookmarkedExerciseEntity" not in content:
    content = content.replace("import com.example.data.UserProfileEntity", "import com.example.data.UserProfileEntity\nimport com.example.data.BookmarkedExerciseEntity")

with open("app/src/main/java/com/example/ui/VitalViewModel.kt", "w") as f:
    f.write(content)
