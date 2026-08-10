with open("app/src/main/java/com/example/ui/screens/ExerciseLibraryScreen.kt", "r") as f:
    content = f.read()

old_reset_condition = "if (searchQuery.isNotEmpty() || selectedCategory != HealthFocusCategory.ALL || selectedEquipment != \"All\") {"
new_reset_condition = "if (searchQuery.isNotEmpty() || selectedCategory != HealthFocusCategory.ALL || selectedEquipment != \"All\" || showOnlySaved) {"

content = content.replace(old_reset_condition, new_reset_condition)

old_reset_action = """                        searchQuery = ""
                        selectedCategory = HealthFocusCategory.ALL
                        selectedEquipment = "All"
                    }"""

new_reset_action = """                        searchQuery = ""
                        selectedCategory = HealthFocusCategory.ALL
                        selectedEquipment = "All"
                        showOnlySaved = false
                    }"""

content = content.replace(old_reset_action, new_reset_action)

with open("app/src/main/java/com/example/ui/screens/ExerciseLibraryScreen.kt", "w") as f:
    f.write(content)
