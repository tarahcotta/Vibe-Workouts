import re

with open("app/src/main/java/com/example/ui/screens/DashboardScreen.kt", "r") as f:
    content = f.read()

# 1. Hide DXA simulator and AI Recommendations behind `sessions.isNotEmpty()` if they are in Tab 0
# Let's find AIRecommendationsCard
target_ai = """            // AI Recommendations & Science Insights
            AIRecommendationsCard(
                sessions = sessions,
                routines = routines
            )"""

replacement_ai = """            // AI Recommendations & Science Insights
            if (sessions.isNotEmpty()) {
                AIRecommendationsCard(
                    sessions = sessions,
                    routines = routines
                )
            }"""

if target_ai in content:
    content = content.replace(target_ai, replacement_ai)
else:
    print("AI target not found")

target_dxa = """            // DXA Simulator
            BoneDensityDxaSimulatorCard(sessions = sessions)"""

replacement_dxa = """            // DXA Simulator
            if (sessions.isNotEmpty()) {
                BoneDensityDxaSimulatorCard(sessions = sessions)
            }"""

if target_dxa in content:
    content = content.replace(target_dxa, replacement_dxa)
else:
    print("DXA target not found")


with open("app/src/main/java/com/example/ui/screens/DashboardScreen.kt", "w") as f:
    f.write(content)

print("Dashboard updated with Progressive Disclosure")
