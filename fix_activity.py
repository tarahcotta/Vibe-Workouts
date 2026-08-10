import re

with open('app/src/main/java/com/example/ui/screens/RecentActivitySummaryScreen.kt', 'r') as f:
    content = f.read()

# Fix 1: Log Workout button contrast
content = content.replace(
    'colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.primary),',
    'colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),'
)

# Fix 2: Icon touch targets
content = content.replace(
    'modifier = Modifier.size(32.dp)',
    'modifier = Modifier.size(48.dp)'
)

# Fix 3: Exercise Summary Chips Contrast
content = content.replace(
    'color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)',
    'color = MaterialTheme.colorScheme.surfaceVariant'
)

# Fix 4: Exercise Summary text color and typography
content = content.replace(
    'style = MaterialTheme.typography.labelSmall,\n                                color = MaterialTheme.colorScheme.onSurfaceVariant,',
    'style = MaterialTheme.typography.bodySmall,\n                                color = MaterialTheme.colorScheme.onSurfaceVariant,'
)

with open('app/src/main/java/com/example/ui/screens/RecentActivitySummaryScreen.kt', 'w') as f:
    f.write(content)

