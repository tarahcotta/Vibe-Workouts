with open("app/src/main/java/com/example/ui/screens/ExerciseLibraryScreen.kt", "r") as f:
    content = f.read()

# Import YouTubeVideoPlayer
if "import com.example.ui.components.YouTubeVideoPlayer" not in content:
    content = content.replace(
        "import com.example.ui.components.CustomFlowRow",
        "import com.example.ui.components.CustomFlowRow\nimport com.example.ui.components.YouTubeVideoPlayer"
    )

# Insert YouTubeVideoPlayer after the Header Title & Close (which is in the first `item`)
target_search = """        item {
            // Category & Equipment Tags"""

replacement = """        item {
            YouTubeVideoPlayer(
                videoUrl = exercise.videoUrl,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            )
        }

        item {
            // Category & Equipment Tags"""

content = content.replace(target_search, replacement)

with open("app/src/main/java/com/example/ui/screens/ExerciseLibraryScreen.kt", "w") as f:
    f.write(content)
