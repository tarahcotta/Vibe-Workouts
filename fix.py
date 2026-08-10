import os

files = [
    "app/src/main/java/com/example/ui/screens/ExerciseLibraryScreen.kt",
    "app/src/main/java/com/example/ui/screens/RecentActivitySummaryScreen.kt",
    "app/src/main/java/com/example/ui/screens/AssessmentScreen.kt",
    "app/src/main/java/com/example/ui/screens/PlateCalculatorScreen.kt",
    "app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt"
]

for file in files:
    with open(file, "r") as f:
        content = f.read()
    
    # We want to replace horizontalSpacing = X.dp back to horizontalArrangement = Arrangement.spacedBy(X.dp) ONLY IF it's NOT inside CustomFlowRow.
    # Actually, let's just replace ALL of them back first.
    content = content.replace("horizontalSpacing = 6.dp", "horizontalArrangement = Arrangement.spacedBy(6.dp)")
    content = content.replace("horizontalSpacing = 8.dp", "horizontalArrangement = Arrangement.spacedBy(8.dp)")
    content = content.replace("verticalSpacing = 6.dp", "verticalArrangement = Arrangement.spacedBy(6.dp)")
    content = content.replace("verticalSpacing = 8.dp", "verticalArrangement = Arrangement.spacedBy(8.dp)")
    
    # Now, find CustomFlowRow( and replace horizontalArrangement with horizontalSpacing
    lines = content.split('\n')
    for i in range(len(lines)):
        if "CustomFlowRow" in lines[i]:
            # Look ahead for arrangement
            for j in range(i, min(i+5, len(lines))):
                if "horizontalArrangement = Arrangement.spacedBy(" in lines[j]:
                    lines[j] = lines[j].replace("horizontalArrangement = Arrangement.spacedBy(", "horizontalSpacing = ").replace(")", "")
                if "verticalArrangement = Arrangement.spacedBy(" in lines[j]:
                    lines[j] = lines[j].replace("verticalArrangement = Arrangement.spacedBy(", "verticalSpacing = ").replace(")", "")
    
    with open(file, "w") as f:
        f.write('\n'.join(lines))

print("Fixed")
