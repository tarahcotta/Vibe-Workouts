import re

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "r") as f:
    content = f.read()

import_line = "import com.example.ui.components.AIRecommendationsCard"
if import_line not in content:
    content = content.replace("import com.example.ui.components.AuthSyncCard", 
                              "import com.example.ui.components.AuthSyncCard\nimport com.example.ui.components.AIRecommendationsCard")

# Insert AIRecommendationsCard above the routines
target = """        if (routines.isEmpty()) {"""
replacement = """        // AI Recommendations
        AIRecommendationsCard(
            sessions = sessions,
            routines = routines
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (routines.isEmpty()) {"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "w") as f:
    f.write(content)
