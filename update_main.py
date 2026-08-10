with open("app/src/main/java/com/example/ui/MainContainer.kt", "r") as f:
    content = f.read()

content = content.replace("                    ExerciseLibraryScreen(\n                        onSelectExerciseForWorkout", "                    ExerciseLibraryScreen(\n                        viewModel = viewModel,\n                        onSelectExerciseForWorkout")

with open("app/src/main/java/com/example/ui/MainContainer.kt", "w") as f:
    f.write(content)
