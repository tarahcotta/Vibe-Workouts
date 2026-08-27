import re
import os

# 1. Update DashboardScreen.kt to add DashboardEmptyState and use it
with open("app/src/main/java/com/example/ui/screens/DashboardScreen.kt", "r") as f:
    dashboard_content = f.read()

# Add DashboardEmptyState to empty states area (or at the top/bottom)
empty_state_composable = """
@Composable
fun DashboardEmptyState(onStartWorkoutClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dashboard_empty_state_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp).fillMaxWidth()
        ) {
            Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Your Journey Begins Here", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Complete your first workout to generate your baseline bone density and strength metrics.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onStartWorkoutClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Start Recommended Routine", fontWeight = FontWeight.Bold)
            }
        }
    }
}
"""

if "DashboardEmptyState" not in dashboard_content:
    dashboard_content += empty_state_composable

# Replace the condition in TAB 1 to show empty state instead of charts
target_tab1_empty = """            if (sessions.isEmpty()) {
                FirstWorkoutOnboardingCard(
                    onStartWorkout = {
                        if (routines.isNotEmpty()) {
                            onSelectRoutine(routines.first())
                        }
                        onNavigateToLogger()
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 1. STRENGTH TRAINING FREQUENCY VICO CHART CARD"""

replacement_tab1_empty = """            if (sessions.isEmpty()) {
                DashboardEmptyState(
                    onStartWorkoutClick = {
                        if (routines.isNotEmpty()) {
                            onSelectRoutine(routines.first())
                        }
                        onNavigateToLogger()
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else {

            // 1. STRENGTH TRAINING FREQUENCY VICO CHART CARD"""

if target_tab1_empty in dashboard_content:
    dashboard_content = dashboard_content.replace(target_tab1_empty, replacement_tab1_empty)
    # find where to close the else block in TAB 1
    target_tab1_close = """                WeightProgressionVicoChartCard(
                    allSessions = sessions,
                    allLoggedSets = allLoggedSets
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))"""
    
    replacement_tab1_close = """                WeightProgressionVicoChartCard(
                    allSessions = sessions,
                    allLoggedSets = allLoggedSets
                )
            }
            } // Close else block
        }

        Spacer(modifier = Modifier.height(20.dp))"""
    dashboard_content = dashboard_content.replace(target_tab1_close, replacement_tab1_close)

with open("app/src/main/java/com/example/ui/screens/DashboardScreen.kt", "w") as f:
    f.write(dashboard_content)

print("Dashboard updated")
