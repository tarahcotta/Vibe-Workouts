import re

with open('app/src/main/java/com/example/ui/screens/ProgressAnalyticsScreen.kt', 'r') as f:
    content = f.read()

# Imports
if "import com.example.ui.components.IllustrativeEmptyState" not in content:
    content = content.replace("import com.example.ui.components.WeightProgressionVicoChartCard", "import com.example.ui.components.WeightProgressionVicoChartCard\nimport com.example.ui.components.IllustrativeEmptyState")

if "import androidx.compose.material.icons.filled.DirectionsRun" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.Delete", "import androidx.compose.material.icons.filled.Delete\nimport androidx.compose.material.icons.filled.DirectionsRun\nimport androidx.compose.material.icons.filled.ShowChart")

empty_state_1_old = """            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Your workout history log will appear here after your first session.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }"""

empty_state_1_new = """            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                IllustrativeEmptyState(
                    icon = Icons.Default.DirectionsRun,
                    title = "Awaiting Your Triumphs",
                    description = "Your workout history log will appear here after your first session. Lace up those shoes!",
                    modifier = Modifier.padding(12.dp)
                )
            }"""

empty_state_2_old = """                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No workout volume logged yet.\nComplete sessions to view your load-bearing chart!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }"""

empty_state_2_new = """                IllustrativeEmptyState(
                    icon = Icons.Default.ShowChart,
                    title = "Ready to Chart Your Progress?",
                    description = "No workout volume logged yet.\\nComplete sessions to view your load-bearing chart and see your gains over time!",
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                )"""

content = content.replace(empty_state_1_old, empty_state_1_new)
content = content.replace(empty_state_2_old, empty_state_2_new)

with open('app/src/main/java/com/example/ui/screens/ProgressAnalyticsScreen.kt', 'w') as f:
    f.write(content)

