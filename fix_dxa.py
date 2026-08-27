import re

with open("app/src/main/java/com/example/ui/screens/DashboardScreen.kt", "r") as f:
    content = f.read()

target_dxa = """            // 4. LIFTMOR TRIAL CLINICAL DXA DENSITY SIMULATOR
            BoneDensityDxaSimulatorCard()"""

replacement_dxa = """            // 4. LIFTMOR TRIAL CLINICAL DXA DENSITY SIMULATOR
            if (sessions.isNotEmpty()) {
                BoneDensityDxaSimulatorCard()
            }"""

if target_dxa in content:
    content = content.replace(target_dxa, replacement_dxa)
else:
    print("DXA target not found again")

with open("app/src/main/java/com/example/ui/screens/DashboardScreen.kt", "w") as f:
    f.write(content)

print("DXA updated")
