import re

with open("app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    "modifier = modifier.fillMaxSize().padding(androidx.compose.foundation.layout.WindowInsets.ime.asPaddingValues()),",
    "modifier = modifier.fillMaxSize().imePadding(),"
)

if "import androidx.compose.foundation.layout.imePadding" not in content:
    content = content.replace("import androidx.compose.foundation.layout.fillMaxSize", "import androidx.compose.foundation.layout.fillMaxSize\nimport androidx.compose.foundation.layout.imePadding")

with open("app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt", "w") as f:
    f.write(content)
