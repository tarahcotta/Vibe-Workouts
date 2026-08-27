import re

with open("app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt", "r") as f:
    content = f.read()

# 1. Extract the timer card from scrollable content
target_timer_block = """            // Rest Timer Card
            var isTimerExpanded by remember { mutableStateOf(isTimerRunning) }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .testTag("rest_timer_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { isTimerExpanded = !isTimerExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Timer",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isTimerRunning) "Rest Timer Active" else "Rest Timer",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                if (isTimerRunning) {
                                    Text(
                                        text = "${timerRemainingSeconds / 60}:${(timerRemainingSeconds % 60).toString().padStart(2, '0')} remaining",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        
                        if (isTimerRunning && !isTimerExpanded) {
                            Row {
                                IconButton(
                                    onClick = { isTimerRunning = false },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SlowMotionVideo,
                                        contentDescription = "Pause",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        timerRemainingSeconds = 0
                                        isTimerRunning = false
                                    },
                                    modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Skip Rest",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        } else {
                            IconButton(
                                onClick = { isTimerExpanded = !isTimerExpanded },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isTimerExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Toggle",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = isTimerExpanded) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            BuiltInIntervalTimerCard(
                                activeExerciseName = activeTimerExerciseName,
                                targetRestSeconds = targetRestSeconds,
                                isRunning = isTimerRunning,
                                remainingSeconds = timerRemainingSeconds,
                                onTogglePlayPause = { isTimerRunning = !isTimerRunning },
                                onResetTimer = { newTarget ->
                                    timerRemainingSeconds = newTarget
                                    isTimerRunning = true
                                },
                                onAdjustSeconds = { delta ->
                                    timerRemainingSeconds = (timerRemainingSeconds + delta).coerceAtLeast(0)
                                },
                                onPresetSelected = { seconds ->
                                    targetRestSeconds = seconds
                                    timerRemainingSeconds = seconds
                                    isTimerRunning = true
                                }
                            )
                        }
                    }
                }
            }"""

if target_timer_block in content:
    content = content.replace(target_timer_block, "")
else:
    print("Warning: Timer block not found in scrollable area")


# 2. Insert Timer into topBar
# We will insert it at the very top of Scaffold topBar (or bottom of topBar).
# Let's insert it inside the Surface column in topBar.

target_topbar_end = """                            Text(
                                text = "Focus Mode",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isFocusHudMode) FontWeight.ExtraBold else FontWeight.Normal,
                                color = if (isFocusHudMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },"""

sticky_timer_code = """
                            Text(
                                text = "Focus Mode",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isFocusHudMode) FontWeight.ExtraBold else FontWeight.Normal,
                                color = if (isFocusHudMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    // STICKY REST TIMER HUD
                    AnimatedVisibility(visible = isTimerRunning || timerRemainingSeconds > 0) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            BuiltInIntervalTimerCard(
                                activeExerciseName = activeTimerExerciseName,
                                targetRestSeconds = targetRestSeconds,
                                isRunning = isTimerRunning,
                                remainingSeconds = timerRemainingSeconds,
                                onTogglePlayPause = { isTimerRunning = !isTimerRunning },
                                onResetTimer = { newTarget ->
                                    timerRemainingSeconds = newTarget
                                    isTimerRunning = true
                                },
                                onAdjustSeconds = { delta ->
                                    timerRemainingSeconds = (timerRemainingSeconds + delta).coerceAtLeast(0)
                                },
                                onPresetSelected = { seconds ->
                                    targetRestSeconds = seconds
                                    timerRemainingSeconds = seconds
                                    isTimerRunning = true
                                }
                            )
                        }
                    }
                }
            }
        },"""

if target_topbar_end in content:
    content = content.replace(target_topbar_end, sticky_timer_code)
else:
    print("Warning: target_topbar_end not found")

with open("app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt", "w") as f:
    f.write(content)

print("ActiveLogger sticky timer updated")
