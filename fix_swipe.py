import re

with open("app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt", "r") as f:
    content = f.read()

# Remove 'Done' column header
target_header = 'Text("Done", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.width(48.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)'
if target_header in content:
    content = content.replace(target_header, "")
    # also remove preceding Spacer
    content = content.replace('Spacer(modifier = Modifier.width(6.dp))\n                            \n                        }\n                        HorizontalDivider', '                        }\n                        HorizontalDivider')

# Wrap Row in SwipeToCompleteWrapper
target_row_start = """                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {"""

replacement_row_start = """                                com.example.ui.components.SwipeToCompleteWrapper(
                                    isCompleted = setInput.isCompleted,
                                    onComplete = {
                                        setInput.isCompleted = true
                                        // Auto-populate subsequent sets if they have default values
                                        logState.sets.forEachIndexed { i, s ->
                                            if (i > setIndex && !s.isCompleted) {
                                                s.weightText = setInput.weightText
                                                s.repsText = setInput.repsText
                                            }
                                        }
                                        val calculatedRest = when {
                                            setInput.rpe >= 9 -> 120
                                            setInput.rpe == 8 -> 90
                                            else -> 60
                                        }
                                        startRestTimer(calculatedRest, logState.exerciseName)
                                        val currentWeight = setInput.weightText.toFloatOrNull() ?: 0f
                                        val previousMax = personalBests[logState.exerciseName] ?: 0f
                                        if (currentWeight > 0f && (previousMax == 0f || currentWeight > previousMax)) {
                                            prNotificationExercise = logState.exerciseName
                                            prNotificationNewWeight = currentWeight
                                            prNotificationOldMax = previousMax
                                            showPrBanner = true
                                            showPrCelebrationDialog = true
                                        }
                                    }
                                ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {"""

if target_row_start in content:
    content = content.replace(target_row_start, replacement_row_start)
else:
    print("Row start not found")

# Remove AnimatedSetCompletionButton and close the SwipeToCompleteWrapper
target_button = """                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Complete Set Animated Button (Spring pop, ripple burst, auto-propagates forward)
                                    AnimatedSetCompletionButton(
                                        isCompleted = setInput.isCompleted,
                                        onToggle = {
                                            setInput.isCompleted = !setInput.isCompleted
                                            if (setInput.isCompleted) {
                                                // Auto-populate subsequent sets if they have default values
                                                logState.sets.forEachIndexed { i, s ->
                                                    if (i > setIndex && !s.isCompleted) {
                                                        s.weightText = setInput.weightText
                                                        s.repsText = setInput.repsText
                                                    }
                                                }
                                                val calculatedRest = when {
                                                    setInput.rpe >= 9 -> 120
                                                    setInput.rpe == 8 -> 90
                                                    else -> 60
                                                }
                                                startRestTimer(calculatedRest, logState.exerciseName)
                                                val currentWeight = setInput.weightText.toFloatOrNull() ?: 0f
                                                val previousMax = personalBests[logState.exerciseName] ?: 0f
                                                if (currentWeight > 0f && (previousMax == 0f || currentWeight > previousMax)) {
                                                    prNotificationExercise = logState.exerciseName
                                                    prNotificationNewWeight = currentWeight
                                                    prNotificationOldMax = previousMax
                                                    showPrBanner = true
                                                    showPrCelebrationDialog = true
                                                }
                                            }
                                        },
                                        size = 48.dp,
                                        testTag = "check_set_${exIndex}_$setIndex"
                                    )
                                }"""

replacement_button = """                                }
                                }"""

if target_button in content:
    content = content.replace(target_button, replacement_button)
else:
    print("Button not found")

with open("app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt", "w") as f:
    f.write(content)

print("Swipe to complete implemented")
