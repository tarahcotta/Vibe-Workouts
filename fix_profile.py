import re

with open('app/src/main/java/com/example/ui/MainContainer.kt', 'r') as f:
    main_content = f.read()

main_content = main_content.replace(
    'NavDestination.PROFILE_SETUP -> "Firebase Profile Setup"',
    'NavDestination.PROFILE_SETUP -> "Account & Profile"'
)

main_content = main_content.replace(
    'contentDescription = "Firebase Auth & Cloud Sync",',
    'contentDescription = "Account & Profile",'
)

with open('app/src/main/java/com/example/ui/MainContainer.kt', 'w') as f:
    f.write(main_content)

with open('app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt', 'r') as f:
    profile_content = f.read()

profile_content = profile_content.replace(
    'text = "Firebase Auth & Profile Setup"',
    'text = "Account & Profile Setup"'
)
profile_content = profile_content.replace(
    'text = "Tailor your longevity programming based on age, goals & cloud sync."',
    'text = "Tailor your longevity programming based on age, goals & preferences."'
)
profile_content = profile_content.replace(
    'text = if (currentUser != null) "Manage Firebase Account / Sign Out" else "Sign In with Firebase / Google"',
    'text = if (currentUser != null) "Manage Account / Sign Out" else "Sign In to Save Progress"'
)

with open('app/src/main/java/com/example/ui/screens/ProfileSetupScreen.kt', 'w') as f:
    f.write(profile_content)

print("Done profile fixes")
