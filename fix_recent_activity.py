import re

with open('app/src/main/java/com/example/ui/screens/RecentActivitySummaryScreen.kt', 'r') as f:
    content = f.read()

# Make sure to import IllustrativeEmptyState
if "import com.example.ui.components.IllustrativeEmptyState" not in content:
    content = content.replace("import com.example.ui.components.PersonalBestNotificationBanner", "import com.example.ui.components.PersonalBestNotificationBanner\nimport com.example.ui.components.IllustrativeEmptyState")

empty_state_old = """            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) "No workout activity matching '$searchQuery'" else "No recent workout activity found for this time range.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onStartNewWorkout,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Start First Workout")
                    }
                }
            }"""

empty_state_new = """            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                IllustrativeEmptyState(
                    icon = Icons.Default.History,
                    title = if (searchQuery.isNotBlank()) "No Matches Found" else "Ready to Get Started?",
                    description = if (searchQuery.isNotBlank()) "We couldn't find any workouts matching '${searchQuery}'." else "Your workout history is empty for this time range. Start logging your sessions to see your progress here!",
                    actionButton = {
                        Button(
                            onClick = onStartNewWorkout,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("Start Your First Workout", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }"""

content = content.replace(empty_state_old, empty_state_new)

with open('app/src/main/java/com/example/ui/screens/RecentActivitySummaryScreen.kt', 'w') as f:
    f.write(content)

