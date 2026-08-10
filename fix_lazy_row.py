import re

with open("app/src/main/java/com/example/ui/screens/ExerciseLibraryScreen.kt", "r") as f:
    content = f.read()

bad_chip = """
            FilterChip(
                selected = showOnlySaved,
                onClick = { showOnlySaved = !showOnlySaved },
                label = {
                    Text(
                        text = "Saved",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (showOnlySaved) FontWeight.Bold else FontWeight.Medium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = if (showOnlySaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Saved",
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("category_chip_saved")
            )
"""
good_chip = """            item {
                FilterChip(
                    selected = showOnlySaved,
                    onClick = { showOnlySaved = !showOnlySaved },
                    label = {
                        Text(
                            text = "Saved",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (showOnlySaved) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (showOnlySaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Saved",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("category_chip_saved")
                )
            }
"""

content = content.replace(bad_chip, good_chip)

with open("app/src/main/java/com/example/ui/screens/ExerciseLibraryScreen.kt", "w") as f:
    f.write(content)
