package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import com.example.ui.components.CustomFlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SyncStatus
import com.example.data.UserProfileEntity
import com.example.ui.VitalViewModel
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    viewModel: VitalViewModel,
    onOpenAuthDialog: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentProfile by viewModel.userProfile.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()

    var ageInput by remember(currentProfile) {
        mutableStateOf(currentProfile?.age?.toString() ?: "45")
    }

    var weightInput by remember(currentProfile) {
        mutableStateOf(currentProfile?.weightLbs?.toString() ?: "165")
    }

    val availableFitnessGoals = remember {
        listOf(
            "Bone Mineral Density & Osteogenesis",
            "Joint & Cartilage Longevity",
            "Posture & Spinal Health",
            "Metabolic Vitality & Glycemic Control",
            "Sarcopenia & Muscle Mass Preservation",
            "Balance, Stability & Fall Prevention"
        )
    }

    val availableRecoveryGoals = remember {
        listOf(
            "Optimal Sleep & Circadian Alignment",
            "CNS Support & Nervous System Recovery",
            "Myofascial Release & Mobility",
            "Nutrient Timing & Metabolic Support",
            "Hydration & Electrolyte Homeostasis"
        )
    }

    val selectedGoals = remember(currentProfile) {
        val initial = currentProfile?.fitnessGoals?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
            ?: listOf("Bone Mineral Density & Osteogenesis", "Joint & Cartilage Longevity", "Posture & Spinal Health")
        mutableStateOf(initial.toSet())
    }

    val selectedRecoveryGoals = remember(currentProfile) {
        val initial = currentProfile?.recoveryGoals?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
            ?: listOf("Optimal Sleep & Circadian Alignment", "Myofascial Release & Mobility")
        mutableStateOf(initial.toSet())
    }

    var strengthLevel by remember(currentProfile) {
        mutableStateOf(currentProfile?.strengthLevel ?: "Intermediate")
    }

    var equipment by remember(currentProfile) {
        mutableStateOf(currentProfile?.availableEquipment ?: "Dumbbells & Kettlebells")
    }

    var scheduleDays by remember(currentProfile) {
        mutableStateOf(currentProfile?.scheduleDaysPerWeek ?: 3)
    }

    var showSuccessMessage by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_setup_screen")
    ) {
        // Header Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Icon",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Account & Profile Setup",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Tailor your longevity programming based on age, goals & preferences.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Firebase Auth Account Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_account_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (currentUser != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (currentUser != null) {
                                    Text(
                                        text = currentUser?.email?.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Icon",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Column {
                            Text(
                                text = currentUser?.email ?: (if (currentUser?.isAnonymous == true) "Anonymous Guest Account" else "Not Signed In (Local Only)"),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            when (syncStatus) {
                                is SyncStatus.Syncing -> Text("Syncing to Firestore...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                is SyncStatus.Success -> Text("Firestore Cloud Backup Active", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                is SyncStatus.Error -> Text("Cloud Sync Error", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                                else -> Text(if (currentUser != null) "Cloud Synced" else "Offline Storage Mode", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onOpenAuthDialog,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manage_auth_account_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Icon",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentUser != null) "Manage Account / Sign Out" else "Sign In to Save Progress",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Age & Weight Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Personal Metrics",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Metrics influence bone recovery rates and individualized loading prescriptions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = ageInput,
                        onValueChange = { ageInput = it.filter { char -> char.isDigit() } },
                        label = { Text("Age") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_age_input")
                    )

                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it.filter { char -> char.isDigit() || char == '.' } },
                        label = { Text("Weight (lbs)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("profile_weight_input")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // DXA T-Score & Bone Density Guidance Card (UX Fix: Onboarding Guidance)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Text(
                        text = "Where to find your DXA T-Score",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "If you've had a bone density scan (DXA), check your report for the 'T-Score' of the lumbar spine or femoral neck. Normal is ≥ -1.0. Osteopenia is -1.0 to -2.5. If you don't have a scan, Vital Strength will estimate your baseline based on age and training history.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Longevity & Fitness Goals",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Select all primary health and longevity outcomes you wish to prioritize:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                CustomFlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalSpacing = 8.dp,
                    verticalSpacing = 8.dp
                ) {
                    availableFitnessGoals.forEach { goal ->
                        val isSelected = selectedGoals.value.contains(goal)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                val currentSet = selectedGoals.value.toMutableSet()
                                if (isSelected) {
                                    if (currentSet.size > 1) currentSet.remove(goal)
                                } else {
                                    currentSet.add(goal)
                                }
                                selectedGoals.value = currentSet
                            },
                            label = { Text(goal, fontSize = 13.sp) },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Icon",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.testTag("goal_chip_${goal.take(10)}")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recovery Goals Selection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Recovery & Lifestyle Optimization",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Personalize your recovery protocols to ensure optimal adaptation to axial loading:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                CustomFlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalSpacing = 8.dp,
                    verticalSpacing = 8.dp
                ) {
                    availableRecoveryGoals.forEach { goal ->
                        val isSelected = selectedRecoveryGoals.value.contains(goal)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                val currentSet = selectedRecoveryGoals.value.toMutableSet()
                                if (isSelected) {
                                    if (currentSet.size > 1) currentSet.remove(goal)
                                } else {
                                    currentSet.add(goal)
                                }
                                selectedRecoveryGoals.value = currentSet
                            },
                            label = { Text(goal, fontSize = 13.sp) },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Icon",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.testTag("recovery_goal_chip_${goal.take(10)}")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Strength Level & Schedule Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Training Level & Schedule",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Experience Level", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Beginner", "Intermediate", "Advanced").forEach { level ->
                        val isSelected = strengthLevel == level
                        OutlinedButton(
                            onClick = { strengthLevel = level },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(level, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Workouts Per Week: $scheduleDays Days", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Slider(
                    value = scheduleDays.toFloat(),
                    onValueChange = { scheduleDays = it.toInt() },
                    valueRange = 2f..4f,
                    steps = 2,
                    modifier = Modifier.testTag("schedule_days_slider")
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save & Sync Button
        Button(
            onClick = {
                val parsedAge = ageInput.toIntOrNull() ?: 45
                val parsedWeight = weightInput.toFloatOrNull() ?: 165f
                val updated = UserProfileEntity(
                    id = 1,
                    age = parsedAge,
                    weightLbs = parsedWeight,
                    strengthLevel = strengthLevel,
                    availableEquipment = equipment,
                    scheduleDaysPerWeek = scheduleDays,
                    fitnessGoals = selectedGoals.value.joinToString(", "),
                    focusAreas = selectedGoals.value.joinToString(", "),
                    recoveryGoals = selectedRecoveryGoals.value.joinToString(", "),
                    updatedAt = System.currentTimeMillis()
                )
                viewModel.saveUserProfile(updated)
                viewModel.regenerateRoutines()
                showSuccessMessage = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("save_profile_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CloudSync,
                contentDescription = "Icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Save Profile & Tailor Programming",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        if (showSuccessMessage) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Profile saved successfully! Longevity routines have been tailored to your age and goals.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("back_to_dashboard_button"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Return to Dashboard")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
