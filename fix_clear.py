with open("app/src/main/java/com/example/ui/screens/ExerciseLibraryScreen.kt", "r") as f:
    content = f.read()

old_clear = """                        onClick = {
                            searchQuery = ""
                            selectedCategory = HealthFocusCategory.ALL
                            selectedEquipment = "All"
                        }"""
new_clear = """                        onClick = {
                            searchQuery = ""
                            selectedCategory = HealthFocusCategory.ALL
                            selectedEquipment = "All"
                            showOnlySaved = false
                        }"""

content = content.replace(old_clear, new_clear)

with open("app/src/main/java/com/example/ui/screens/ExerciseLibraryScreen.kt", "w") as f:
    f.write(content)
