with open("app/src/main/java/com/example/ui/components/AuthDialog.kt", "r") as f:
    content = f.read()

imports = """import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Save
"""
content = content.replace("import androidx.compose.foundation.background", imports + "import androidx.compose.foundation.background")

with open("app/src/main/java/com/example/ui/components/AuthDialog.kt", "w") as f:
    f.write(content)

print("Imports added")
