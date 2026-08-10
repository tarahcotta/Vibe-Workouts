with open("app/src/main/java/com/example/ui/screens/OnboardingScreen.kt", "r") as f:
    content = f.read()

# Fix 1: Interactive Bone Remodeling Simulator Colors
# Color(0xFFD32F2F) -> Color(0xFFE57373)
# Color(0xFFED6C02) -> Color(0xFFFFB74D)
# Color(0xFF2E7D32) -> Color(0xFF81C784)

content = content.replace("Color(0xFFD32F2F)", "Color(0xFFE57373)")
content = content.replace("Color(0xFFED6C02)", "Color(0xFFFFB74D)")
content = content.replace("Color(0xFF2E7D32)", "Color(0xFF81C784)")

# Fix 2: Subtitle readability
content = content.replace(
"""        Text(
            text = step.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )""",
"""        Text(
            text = step.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )""")

# Add SliderDefaults import if needed
if "import androidx.compose.material3.SliderDefaults" not in content:
    content = content.replace("import androidx.compose.material3.Slider", "import androidx.compose.material3.Slider\nimport androidx.compose.material3.SliderDefaults")

# Fix 3: Slider colors
content = content.replace(
"""            Slider(
                value = loadLevel,
                onValueChange = { loadLevel = it },
                valueRange = 0f..2f,
                steps = 1,
                modifier = Modifier.testTag("onboarding_bone_density_slider")
            )""",
"""            Slider(
                value = loadLevel,
                onValueChange = { loadLevel = it },
                valueRange = 0f..2f,
                steps = 1,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                ),
                modifier = Modifier.testTag("onboarding_bone_density_slider")
            )""")

# Fix 4: Next button contrast
content = content.replace(
"""                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (pagerState.currentPage == steps.size - 1) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primaryContainer,
                                contentColor = if (pagerState.currentPage == steps.size - 1) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onPrimaryContainer
                            ),""",
"""                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),""")

with open("app/src/main/java/com/example/ui/screens/OnboardingScreen.kt", "w") as f:
    f.write(content)
