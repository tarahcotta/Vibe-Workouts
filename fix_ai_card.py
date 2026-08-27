import re

with open("app/src/main/java/com/example/ui/components/AIRecommendationsCard.kt", "r") as f:
    content = f.read()

target_ui = """                Text(
                    text = recommendation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}"""

replacement_ui = """                Text(
                    text = recommendation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { /* Apply action */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Apply to Today's Workout", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}"""

if target_ui in content:
    content = content.replace(target_ui, replacement_ui)
else:
    print("Warning: Target UI not found in AIRecommendationsCard")

with open("app/src/main/java/com/example/ui/components/AIRecommendationsCard.kt", "w") as f:
    f.write(content)

print("AI card updated")
