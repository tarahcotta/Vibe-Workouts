import re

with open("app/src/main/java/com/example/ui/screens/ExerciseLibraryScreen.kt", "r") as f:
    content = f.read()

# Add import
if "import androidx.lifecycle.compose.collectAsStateWithLifecycle" not in content:
    content = content.replace("import androidx.compose.runtime.rememberCoroutineScope", "import androidx.compose.runtime.rememberCoroutineScope\nimport androidx.lifecycle.compose.collectAsStateWithLifecycle\nimport com.example.ui.VitalViewModel")

# Update signature
old_sig = """fun ExerciseLibraryScreen(
    onSelectExerciseForWorkout: ((ExerciseLibraryItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {"""

new_sig = """fun ExerciseLibraryScreen(
    viewModel: VitalViewModel,
    onSelectExerciseForWorkout: ((ExerciseLibraryItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {"""
content = content.replace(old_sig, new_sig)

# Update bookmarkedIds
old_bookmarked = "var bookmarkedIds by remember { mutableStateOf(setOf<String>()) }"
new_bookmarked = "val bookmarkedEntities by viewModel.bookmarkedExercises.collectAsStateWithLifecycle()\n    val bookmarkedIds = remember(bookmarkedEntities) { bookmarkedEntities.map { it.exerciseId }.toSet() }"
content = content.replace(old_bookmarked, new_bookmarked)

# Update onToggleBookmark
old_toggle = """                        onToggleBookmark = {
                            bookmarkedIds = if (isBookmarked) {
                                bookmarkedIds - exercise.id
                            } else {
                                bookmarkedIds + exercise.id
                            }
                        },"""
new_toggle = """                        onToggleBookmark = {
                            if (isBookmarked) {
                                viewModel.removeBookmark(exercise.id)
                            } else {
                                viewModel.addBookmark(exercise.id)
                            }
                        },"""
content = content.replace(old_toggle, new_toggle)

with open("app/src/main/java/com/example/ui/screens/ExerciseLibraryScreen.kt", "w") as f:
    f.write(content)
