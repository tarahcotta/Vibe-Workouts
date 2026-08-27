import re

with open("app/src/main/java/com/example/ui/screens/DashboardScreen.kt", "r") as f:
    content = f.read()

target_auth_sync = """            AuthSyncCard(
                viewModel = viewModel,
                onOpenAuthDialog = onOpenAuthDialog
            )"""

replacement_auth_sync = """            var showConflictDialog by remember { mutableStateOf(false) }
            if (showConflictDialog) {
                com.example.ui.components.SyncConflictDialog(
                    onKeepLocal = { showConflictDialog = false },
                    onKeepCloud = { showConflictDialog = false },
                    onDismiss = { showConflictDialog = false }
                )
            }
            Box(modifier = Modifier.clickable { if (viewModel.userProfile.collectAsState().value != null) showConflictDialog = true }) {
                AuthSyncCard(
                    viewModel = viewModel,
                    onOpenAuthDialog = onOpenAuthDialog
                )
            }"""

if target_auth_sync in content:
    content = content.replace(target_auth_sync, replacement_auth_sync)
else:
    print("AuthSyncCard usage not found in DashboardScreen")

with open("app/src/main/java/com/example/ui/screens/DashboardScreen.kt", "w") as f:
    f.write(content)
print("Conflict dialog trigger added")
