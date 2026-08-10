package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.example.ui.components.CustomFlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExerciseLibraryItem
import com.example.data.ExerciseLibraryRepository
import com.example.data.HealthFocusCategory
import com.example.data.WorkoutRoutineEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExerciseLibraryScreen(
    onSelectExerciseForWorkout: ((ExerciseLibraryItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(HealthFocusCategory.ALL) }
    var selectedEquipment by remember { mutableStateOf("All") }
    var selectedExerciseForDetail by remember { mutableStateOf<ExerciseLibraryItem?>(null) }
    var bookmarkedIds by remember { mutableStateOf(setOf<String>()) }

    val equipmentOptions = listOf("All", "Barbell", "Dumbbell", "Kettlebell", "Bodyweight", "Cable")

    val filteredExercises = remember(searchQuery, selectedCategory, selectedEquipment) {
        ExerciseLibraryRepository.searchExercises(
            query = searchQuery,
            category = selectedCategory,
            equipmentFilter = selectedEquipment
        )
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                        contentDescription = "Longevity Exercise Library",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Strength & Longevity Library",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Bone density, joint preservation & proper form guide",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search exercise, muscle, bone target, form tip...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("exercise_search_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(HealthFocusCategory.values()) { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    label = {
                        Text(
                            text = category.displayName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("category_chip_${category.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Equipment Filter Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Equipment:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(equipmentOptions) { equip ->
                    val isSelected = selectedEquipment == equip
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedEquipment = equip },
                        label = {
                            Text(
                                text = equip,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Result Count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredExercises.size} Exercises Found",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (searchQuery.isNotEmpty() || selectedCategory != HealthFocusCategory.ALL || selectedEquipment != "All") {
                TextButton(
                    onClick = {
                        searchQuery = ""
                        selectedCategory = HealthFocusCategory.ALL
                        selectedEquipment = "All"
                    }
                ) {
                    Text("Reset Filters", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Exercise Cards List
        if (filteredExercises.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No matching exercises found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try broadening your search query or selecting 'All Focuses'",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = {
                            searchQuery = ""
                            selectedCategory = HealthFocusCategory.ALL
                            selectedEquipment = "All"
                        }
                    ) {
                        Text("Clear All Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredExercises, key = { it.id }) { exercise ->
                    val isBookmarked = bookmarkedIds.contains(exercise.id)

                    ExerciseCardItem(
                        exercise = exercise,
                        isBookmarked = isBookmarked,
                        onToggleBookmark = {
                            bookmarkedIds = if (isBookmarked) {
                                bookmarkedIds - exercise.id
                            } else {
                                bookmarkedIds + exercise.id
                            }
                        },
                        onClick = {
                            selectedExerciseForDetail = exercise
                        }
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet for Exercise Form Instructions & Science Tips
    if (selectedExerciseForDetail != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedExerciseForDetail = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            val item = selectedExerciseForDetail!!
            ExerciseDetailSheetContent(
                exercise = item,
                onClose = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        selectedExerciseForDetail = null
                    }
                },
                onSelectForWorkout = if (onSelectExerciseForWorkout != null) {
                    {
                        onSelectExerciseForWorkout(item)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            selectedExerciseForDetail = null
                        }
                    }
                } else null
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseCardItem(
    exercise: ExerciseLibraryItem,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("exercise_card_${exercise.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row: Title & Bookmark button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark Exercise",
                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Badges FlowRow (Category, Muscle Group, Equipment)
            CustomFlowRow(
                horizontalSpacing = 6.dp,
                verticalSpacing = 6.dp
            ) {
                // Health Category Badge
                CategoryPill(
                    text = exercise.healthFocusCategory.displayName,
                    containerColor = when (exercise.healthFocusCategory) {
                        HealthFocusCategory.BONE_DENSITY -> MaterialTheme.colorScheme.primaryContainer
                        HealthFocusCategory.JOINT_HEALTH -> MaterialTheme.colorScheme.secondaryContainer
                        HealthFocusCategory.SPINE_POSTURE -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = when (exercise.healthFocusCategory) {
                        HealthFocusCategory.BONE_DENSITY -> MaterialTheme.colorScheme.onPrimaryContainer
                        HealthFocusCategory.JOINT_HEALTH -> MaterialTheme.colorScheme.onSecondaryContainer
                        HealthFocusCategory.SPINE_POSTURE -> MaterialTheme.colorScheme.onTertiaryContainer
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                // Equipment Badge
                CategoryPill(
                    text = exercise.equipment,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )

                // Difficulty Badge
                CategoryPill(
                    text = exercise.difficulty,
                    containerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Muscle Group Subtitle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = exercise.muscleGroup,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Target Bones & Joints
            Text(
                text = "🦴 Targets: ${exercise.targetBonesAndJoints}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Short Summary
            Text(
                text = exercise.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "View Form Instructions & Tips →",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CategoryPill(
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseDetailSheetContent(
    exercise: ExerciseLibraryItem,
    onClose: () -> Unit,
    onSelectForWorkout: (() -> Unit)? = null
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            // Header Title & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = exercise.muscleGroup,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close detail view"
                    )
                }
            }
        }

        item {
            // Category & Equipment Tags
            CustomFlowRow(
                horizontalSpacing = 8.dp,
                verticalSpacing = 8.dp
            ) {
                CategoryPill(
                    text = "Focus: ${exercise.healthFocusCategory.displayName}",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                CategoryPill(
                    text = "Equipment: ${exercise.equipment}",
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
                CategoryPill(
                    text = "Difficulty: ${exercise.difficulty}",
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            // Targeted Bones & Joints Box
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "🦴",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Targeted Bones & Joints",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = exercise.targetBonesAndJoints,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            // Longevity Science Note Highlight Box
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Longevity & Osteo Science Note",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = exercise.longevityScienceNote,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        item {
            // Step-by-Step Form Instructions
            Text(
                text = "Step-by-Step Execution Form",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                exercise.stepByStepForm.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f).padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        item {
            // Pro Form Tips for Longevity
            Text(
                text = "Pro Form Tips for Maximum Safety",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                exercise.proFormTips.forEach { tip ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tip,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        item {
            // Common Mistakes to Avoid
            Text(
                text = "Common Mistakes to Avoid",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                exercise.commonMistakes.forEach { mistake ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = mistake,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        if (onSelectForWorkout != null) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.Button(
                    onClick = onSelectForWorkout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("select_exercise_for_workout_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Include in Next Workout Session")
                }
            }
        }
    }
}
