with open("app/src/main/java/com/example/ui/screens/OnboardingScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
"""                        Column(horizontalAlignment = Alignment.End) {
                            Text("Recommended Today:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF81C784))
                            Text("37.5 lbs × 10 reps (+2.5 lbs)", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                        }""",
"""                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.2f)) {
                            Text("Recommended Today:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF81C784))
                            Text("37.5 lbs × 10 reps (+2.5 lbs)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                        }""")

content = content.replace(
"""                        Column {
                            Text("Previous Target:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("35 lbs × 10 reps", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        }""",
"""                        Column(modifier = Modifier.weight(1f)) {
                            Text("Previous Target:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("35 lbs × 10 reps", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }""")


with open("app/src/main/java/com/example/ui/screens/OnboardingScreen.kt", "w") as f:
    f.write(content)
