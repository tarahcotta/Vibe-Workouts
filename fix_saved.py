import re

with open("app/src/main/java/com/example/ui/screens/ExerciseLibraryScreen.kt", "r") as f:
    content = f.read()

# Add showOnlySaved state
if "var showOnlySaved by remember" not in content:
    content = content.replace('var selectedEquipment by remember { mutableStateOf("All") }', 
                              'var selectedEquipment by remember { mutableStateOf("All") }\n    var showOnlySaved by remember { mutableStateOf(false) }')

# Update filteredExercises logic
old_filter = """    val filteredExercises = remember(searchQuery, selectedCategory, selectedEquipment) {
        ExerciseLibraryRepository.searchExercises(
            query = searchQuery,
            category = selectedCategory,
            equipmentFilter = selectedEquipment
        )
    }"""

new_filter = """    val filteredExercises = remember(searchQuery, selectedCategory, selectedEquipment, showOnlySaved, bookmarkedIds) {
        val allFiltered = ExerciseLibraryRepository.searchExercises(
            query = searchQuery,
            category = selectedCategory,
            equipmentFilter = selectedEquipment
        )
        if (showOnlySaved) {
            allFiltered.filter { bookmarkedIds.contains(it.id) }
        } else {
            allFiltered
        }
    }"""
content = content.replace(old_filter, new_filter)

# Add FilterChip for "Saved"
saved_chip = """
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

content = content.replace("            items(HealthFocusCategory.values()) { category ->", saved_chip + "            items(HealthFocusCategory.values()) { category ->")

with open("app/src/main/java/com/example/ui/screens/ExerciseLibraryScreen.kt", "w") as f:
    f.write(content)
