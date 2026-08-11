with open("app/src/main/java/com/example/ui/screens/OnboardingScreen.kt", "r") as f:
    content = f.read()

# Fix Step 1 Color
content = content.replace("primaryColor = Color(0xFF084E72)", "primaryColor = Color(0xFF4FC3F7)")
# Fix Step 2 Color
content = content.replace("primaryColor = Color(0xFFC45A00)", "primaryColor = Color(0xFFFFB74D)")
# Fix Step 3 Color
content = content.replace("primaryColor = Color(0xFF006C4C)", "primaryColor = Color(0xFF81C784)")
# Fix Step 4 Color
content = content.replace("primaryColor = Color(0xFF533F93)", "primaryColor = Color(0xFFB39DDB)")

# Fix "Smart Micro-Progressions" title wrapping issue
content = content.replace('title = "Smart Micro-Progressions"', 'title = "Smart\\nMicro-Progressions"')

with open("app/src/main/java/com/example/ui/screens/OnboardingScreen.kt", "w") as f:
    f.write(content)
