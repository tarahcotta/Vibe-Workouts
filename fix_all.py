import re

with open("app/src/main/java/com/example/ui/components/AuthDialog.kt", "r") as f:
    content = f.read()

# Add imports
imports = """import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Save
"""
content = content.replace("import androidx.compose.foundation.layout.fillMaxWidth", imports + "import androidx.compose.foundation.layout.fillMaxWidth")

with open("app/src/main/java/com/example/ui/components/AuthDialog.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt", "r") as f:
    content2 = f.read()

content2 = content2.replace("androidx.compose.material.icons.Icons.AutoMirrored.Filled.TrendingUp", "androidx.compose.material.icons.Icons.Default.TrendingUp")

if "import androidx.compose.material.icons.filled.TrendingUp" not in content2:
    content2 = content2.replace("import androidx.compose.material.icons.filled.Info", "import androidx.compose.material.icons.filled.Info\nimport androidx.compose.material.icons.filled.TrendingUp")

with open("app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt", "w") as f:
    f.write(content2)

with open("app/src/main/java/com/example/ui/screens/DashboardScreen.kt", "r") as f:
    content3 = f.read()

# Fix the collectAsState error
content3 = content3.replace("viewModel.userProfile.collectAsState().value != null", "viewModel.userProfile.value != null")

with open("app/src/main/java/com/example/ui/screens/DashboardScreen.kt", "w") as f:
    f.write(content3)

print("Fixes applied")
