import re

# 1. Update MainContainer.kt
with open('app/src/main/java/com/example/ui/MainContainer.kt', 'r') as f:
    main_content = f.read()

main_content = main_content.replace(
    "if (currentDestination != NavDestination.ONBOARDING) {\n                CenterAlignedTopAppBar(",
    "if (currentDestination != NavDestination.ONBOARDING && currentDestination != NavDestination.LOGGER) {\n                CenterAlignedTopAppBar("
)

with open('app/src/main/java/com/example/ui/MainContainer.kt', 'w') as f:
    f.write(main_content)

# 2. Update RestIntervalTimerComponent.kt
with open('app/src/main/java/com/example/ui/components/RestIntervalTimerComponent.kt', 'r') as f:
    timer_content = f.read()

old_play_pause = """                    // Play/Pause Button
                    Button(
                        onClick = onTogglePlayPause,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("interval_timer_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause Timer",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }"""

new_play_pause = """                    // Play/Pause Button
                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = if (isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .testTag("interval_timer_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause Timer",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }"""

timer_content = timer_content.replace(old_play_pause, new_play_pause)

old_minus = """                        Text(
                            text = "-15s",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )"""

new_minus = """                        Text(
                            text = "-15s",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            maxLines = 1,
                            softWrap = false
                        )"""
timer_content = timer_content.replace(old_minus, new_minus)

old_plus = """                        Text(
                            text = "+15s",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )"""
new_plus = """                        Text(
                            text = "+15s",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            maxLines = 1,
                            softWrap = false
                        )"""
timer_content = timer_content.replace(old_plus, new_plus)

# Let's fix the Timer button row weights so it doesn't wrap text, or we make the Row layout a bit more flexible.
# Actually, by putting maxLines=1 and softWrap=false, it might just fit. But let's reduce the spacer widths.
timer_content = timer_content.replace("Spacer(modifier = Modifier.width(14.dp))", "Spacer(modifier = Modifier.width(8.dp))")

with open('app/src/main/java/com/example/ui/components/RestIntervalTimerComponent.kt', 'w') as f:
    f.write(timer_content)

# 3. Update ActiveLoggerScreen.kt
with open('app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt', 'r') as f:
    logger_content = f.read()

# Make the title bigger (headlineMedium or headlineSmall) and adjust the X button size.
old_header = """                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = routineTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Track your progress and listen to your body",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = onCancel,
                            modifier = Modifier.size(48.dp) // WCAG touch target
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel Workout")
                        }
                    }"""

new_header = """                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = routineTitle,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Track your progress and listen to your body",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = onCancel,
                            modifier = Modifier.size(48.dp) // WCAG touch target
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel Workout")
                        }
                    }"""
logger_content = logger_content.replace(old_header, new_header)

with open('app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt', 'w') as f:
    f.write(logger_content)

print("Done UX fixes")
