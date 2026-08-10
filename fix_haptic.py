import os

filepath = 'app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

# add imports
imports = """import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
"""
content = content.replace('import androidx.compose.ui.platform.LocalContext', imports + 'import androidx.compose.ui.platform.LocalContext')

# add haptic variable
haptic_var = "    val haptic = LocalHapticFeedback.current\n"
content = content.replace('    val context = LocalContext.current\n', haptic_var + '    val context = LocalContext.current\n')

# call haptic on complete
content = content.replace('setInput.isCompleted = !setInput.isCompleted', """setInput.isCompleted = !setInput.isCompleted
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)""")

with open(filepath, 'w') as f:
    f.write(content)

print("Added haptic feedback")
