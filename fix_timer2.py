import re

with open("app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt", "r") as f:
    content = f.read()

# Remove from scrollable area:
# from "            // Built-In Rest Interval Timer Component (Collapsible UX & Direct Quick Controls)" 
# to the end of the Card. Let's find the string exactly.
start_idx = content.find("            // Built-In Rest Interval Timer Component (Collapsible UX & Direct Quick Controls)")
if start_idx != -1:
    end_pattern = "                    AnimatedVisibility(visible = isTimerExpanded) {"
    end_idx = content.find(end_pattern, start_idx)
    if end_idx != -1:
        # find the end of the AnimatedVisibility block
        close_braces = 0
        current_idx = end_idx + len(end_pattern)
        found_end = -1
        # we need to find 3 closing braces
        for i in range(current_idx, len(content)):
            if content[i] == '{':
                close_braces += 1
            elif content[i] == '}':
                close_braces -= 1
                if close_braces == -3: # since we started inside the block but didn't count the first brace
                    found_end = i
                    break
        # it's easier to just do string matching.
        full_pattern = content[start_idx:content.find("            // 5-Minute Pre-Workout Dynamic Mobility Routine", start_idx)]
        content = content.replace(full_pattern, "")

# Insert into topBar
insert_pattern = """                                    color = if (isFocusHudMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },"""

sticky_timer_code = """                                    color = if (isFocusHudMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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

if insert_pattern in content:
    content = content.replace(insert_pattern, sticky_timer_code)
else:
    print("Warning: insert pattern not found for topbar")

with open("app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt", "w") as f:
    f.write(content)

print("Timer moved to Sticky Header")
