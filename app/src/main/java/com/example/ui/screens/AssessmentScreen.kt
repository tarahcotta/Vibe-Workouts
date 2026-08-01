package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import com.example.data.UserProfileEntity
import com.example.ui.theme.ThemeMode

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AssessmentScreen(
    currentProfile: UserProfileEntity?,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    onSaveProfile: (UserProfileEntity) -> Unit,
    onNavigateToRoutines: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var strengthLevel by remember(currentProfile) {
        mutableStateOf(currentProfile?.strengthLevel ?: "Intermediate")
    }
    var equipment by remember(currentProfile) {
        mutableStateOf(currentProfile?.availableEquipment ?: "Dumbbells & Kettlebells")
    }
    var scheduleDays by remember(currentProfile) {
        mutableStateOf(currentProfile?.scheduleDaysPerWeek ?: 3)
    }

    val selectedJoints = remember(currentProfile) {
        val initial = currentProfile?.jointHistory?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
            ?: listOf("Knees", "Lower Back")
        mutableStateOf(initial.toSet())
    }

    val selectedFocus = remember(currentProfile) {
        val initial = currentProfile?.focusAreas?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
            ?: listOf("Bone Density", "Posture", "Single-Leg Balance", "Grip Strength")
        mutableStateOf(initial.toSet())
    }

    var rpeTarget by remember(currentProfile) {
        mutableStateOf(currentProfile?.targetRpeRange ?: "7-8")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Top Assessment Banner
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
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = "Assessment Icon",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Longevity & Health Assessment",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Customize your program for maximum bone density, joint safety & functional strength.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App Appearance & Theme Selection Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("app_theme_selection_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                AssessmentSectionHeader(
                    title = "App Appearance & Theme",
                    subtitle = "Switch between Light, Dark, or System mode while maintaining Geometric Balance contrast"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = themeMode == ThemeMode.LIGHT,
                        onClick = { onThemeModeChange(ThemeMode.LIGHT) },
                        label = { Text("Light") },
                        leadingIcon = { Icon(Icons.Default.LightMode, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.weight(1f).testTag("theme_chip_light")
                    )

                    FilterChip(
                        selected = themeMode == ThemeMode.DARK,
                        onClick = { onThemeModeChange(ThemeMode.DARK) },
                        label = { Text("Dark") },
                        leadingIcon = { Icon(Icons.Default.DarkMode, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.weight(1f).testTag("theme_chip_dark")
                    )

                    FilterChip(
                        selected = themeMode == ThemeMode.SYSTEM,
                        onClick = { onThemeModeChange(ThemeMode.SYSTEM) },
                        label = { Text("System") },
                        leadingIcon = { Icon(Icons.Default.SettingsBrightness, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.weight(1f).testTag("theme_chip_system")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 1. Current Strength Level
        AssessmentSectionHeader(
            title = "1. Current Strength Experience",
            subtitle = "Tailors starting loads and movement complexity"
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Beginner", "Intermediate", "Advanced").forEach { level ->
                val isSelected = strengthLevel == level
                OutlinedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { strengthLevel = level }
                        .testTag("strength_level_$level"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = level,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Available Equipment
        AssessmentSectionHeader(
            title = "2. Available Training Equipment",
            subtitle = "Select what equipment you have access to"
        )
        val equipmentOptions = listOf(
            "Bodyweight & Bands",
            "Dumbbells & Kettlebells",
            "Full Barbell Gym"
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            equipmentOptions.forEach { eq ->
                val isSelected = equipment == eq
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { equipment = eq }
                        .testTag("equipment_$eq"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = eq,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Weekly Schedule
        AssessmentSectionHeader(
            title = "3. Weekly Training Schedule",
            subtitle = "Days per week dedicated to strength & bone density"
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(2, 3, 4).forEach { daysCount ->
                val isSelected = scheduleDays == daysCount
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { scheduleDays = daysCount }
                        .testTag("schedule_$daysCount"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$daysCount Days / Wk",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = when(daysCount) {
                                2 -> "Full Body Split"
                                3 -> "Longevity Split"
                                else -> "Lower/Upper Split"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Joint History & Safety Considerations
        AssessmentSectionHeader(
            title = "4. Joint Health & History",
            subtitle = "Exercises are auto-scaled to protect these joint zones"
        )
        val jointOptions = listOf("Knees", "Lower Back", "Shoulders", "Wrists/Grip", "None")
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            jointOptions.forEach { joint ->
                val isSelected = if (joint == "None") {
                    selectedJoints.value.isEmpty() || selectedJoints.value.contains("None")
                } else {
                    selectedJoints.value.contains(joint)
                }

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val current = selectedJoints.value.toMutableSet()
                        if (joint == "None") {
                            current.clear()
                            current.add("None")
                        } else {
                            current.remove("None")
                            if (current.contains(joint)) {
                                current.remove(joint)
                            } else {
                                current.add(joint)
                            }
                        }
                        selectedJoints.value = current
                    },
                    label = { Text(joint) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    modifier = Modifier.testTag("joint_chip_$joint")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5. Specific Longevity Focus Areas
        AssessmentSectionHeader(
            title = "5. Priority Longevity Focus Areas",
            subtitle = "Select target physical adaptations"
        )
        val focusOptions = listOf(
            "Bone Density",
            "Posture",
            "Single-Leg Balance",
            "Grip Strength",
            "Core Stability",
            "Joint Mobility"
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            focusOptions.forEach { focus ->
                val isSelected = selectedFocus.value.contains(focus)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val current = selectedFocus.value.toMutableSet()
                        if (current.contains(focus)) {
                            current.remove(focus)
                        } else {
                            current.add(focus)
                        }
                        selectedFocus.value = current
                    },
                    label = { Text(focus) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("focus_chip_$focus")
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // CTA Button to Save & Generate
        Button(
            onClick = {
                val updatedProfile = UserProfileEntity(
                    id = 1,
                    strengthLevel = strengthLevel,
                    availableEquipment = equipment,
                    scheduleDaysPerWeek = scheduleDays,
                    jointHistory = if (selectedJoints.value.isEmpty()) "None" else selectedJoints.value.joinToString(", "),
                    focusAreas = if (selectedFocus.value.isEmpty()) "Bone Density" else selectedFocus.value.joinToString(", "),
                    targetRpeRange = rpeTarget,
                    updatedAt = System.currentTimeMillis()
                )
                onSaveProfile(updatedProfile)
                onNavigateToRoutines()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("generate_program_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Generate Custom Longevity Plan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AssessmentSectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
