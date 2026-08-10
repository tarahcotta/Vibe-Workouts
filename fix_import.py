import re

with open('app/src/main/java/com/example/ui/screens/RecentActivitySummaryScreen.kt', 'r') as f:
    content = f.read()

if "import com.example.ui.components.IllustrativeEmptyState" not in content:
    content = content.replace("import com.example.ui.components.CustomFlowRow", "import com.example.ui.components.CustomFlowRow\nimport com.example.ui.components.IllustrativeEmptyState")

with open('app/src/main/java/com/example/ui/screens/RecentActivitySummaryScreen.kt', 'w') as f:
    f.write(content)

