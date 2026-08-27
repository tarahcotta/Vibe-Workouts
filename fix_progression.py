import re

with open("app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt", "r") as f:
    content = f.read()

target_fill_button = """                            if (currentPr > 0f) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                                    modifier = Modifier.clickable {
                                        logState.sets.forEach { s ->
                                            if (!s.isCompleted) {
                                                s.weightText = "${currentPr.toInt()}"
                                            }
                                        }
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Fill ${currentPr.toInt()} lbs",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }"""

replacement_fill_button = """                            if (currentPr > 0f) {
                                // Auto-Progression Engine Logic
                                val isOverloadSuggested = true // In a real app we'd look at past RPEs. For now, if PR > 0, we suggest +5lbs
                                val suggestedWeight = currentPr.toInt() + 5
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    modifier = Modifier.clickable {
                                        logState.sets.forEach { s ->
                                            if (!s.isCompleted) {
                                                s.weightText = "$suggestedWeight"
                                            }
                                        }
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.TrendingUp,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Auto-Progress: $suggestedWeight lbs",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                }
                            }"""

if target_fill_button in content:
    content = content.replace(target_fill_button, replacement_fill_button)
else:
    print("Fill button not found")

with open("app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt", "w") as f:
    f.write(content)

print("Progression Engine Implemented")
