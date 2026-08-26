package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CustomFlowRow
import kotlin.math.max

enum class EquipmentCategory(val label: String) {
    OLYMPIC_BARBELL("Olympic 2\""),
    STANDARD_BARBELL("Home 1\" Bar"),
    DUMBBELLS("Dumbbell Pair"),
    MACHINE_STACK("Pin Machine")
}

data class EquipmentOption(
    val name: String,
    val tareWeightLbs: Double,
    val category: EquipmentCategory,
    val description: String
)

data class PlateCount(
    val plateWeight: Double,
    val count: Int,
    val color: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlateCalculatorScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var selectedCategory by remember { mutableStateOf(EquipmentCategory.OLYMPIC_BARBELL) }
    var targetWeightText by remember { mutableStateOf("135") }
    val targetWeight = targetWeightText.toDoubleOrNull() ?: 135.0

    val equipmentList = remember {
        listOf(
            // Olympic
            EquipmentOption("Standard Olympic Bar (45 lbs / 20.4 kg)", 45.0, EquipmentCategory.OLYMPIC_BARBELL, "Full-size 7ft Olympic sleeve bar"),
            EquipmentOption("Women's Olympic Bar (33 lbs / 15 kg)", 33.0, EquipmentCategory.OLYMPIC_BARBELL, "25mm grip diameter competition bar"),
            EquipmentOption("Aluminum Training Bar (25 lbs)", 25.0, EquipmentCategory.OLYMPIC_BARBELL, "Lightweight technique progression bar"),
            EquipmentOption("Rehab / Technique Bar (15 lbs)", 15.0, EquipmentCategory.OLYMPIC_BARBELL, "Ultra-light bone loading entry bar"),
            EquipmentOption("EZ Curl Bar (25 lbs)", 25.0, EquipmentCategory.OLYMPIC_BARBELL, "Cambered joint-friendly arm bar"),

            // Standard 1-inch Home
            EquipmentOption("Standard 1-Inch Bar (15 lbs)", 15.0, EquipmentCategory.STANDARD_BARBELL, "Common home gym threaded or collar bar"),
            EquipmentOption("Short 1-Inch Home Bar (10 lbs)", 10.0, EquipmentCategory.STANDARD_BARBELL, "5ft standard home barbell"),

            // Dumbbells
            EquipmentOption("Dumbbell Pair (Load per Hand / 0 lb Tare)", 0.0, EquipmentCategory.DUMBBELLS, "Calculates single or paired dumbbell load"),
            EquipmentOption("Adjustable DB Handles (5 lbs each)", 10.0, EquipmentCategory.DUMBBELLS, "Threaded metal dumbbell handles (pair)"),

            // Machine / Stack
            EquipmentOption("Selectorized Pin Stack (0 lb Tare)", 0.0, EquipmentCategory.MACHINE_STACK, "Cable & leg press weight stack increments")
        )
    }

    val filteredEquipment = remember(selectedCategory) {
        equipmentList.filter { it.category == selectedCategory }
    }

    var selectedEquipment by remember { mutableStateOf(equipmentList[0]) }

    // Synchronize selected equipment when category tab changes
    LaunchedEffect(selectedCategory) {
        if (selectedEquipment.category != selectedCategory) {
            selectedEquipment = filteredEquipment.firstOrNull() ?: equipmentList[0]
            if (selectedCategory == EquipmentCategory.DUMBBELLS && targetWeightText == "135") {
                targetWeightText = "35" // Friendly default for dumbbells
            } else if (selectedCategory == EquipmentCategory.OLYMPIC_BARBELL && targetWeightText == "35") {
                targetWeightText = "135"
            }
        }
    }

    // Available Plates based on Equipment Category
    val availablePlates = remember(selectedCategory) {
        when (selectedCategory) {
            EquipmentCategory.OLYMPIC_BARBELL -> listOf(
                Triple(45.0, "45 lbs", Color(0xFFD32F2F)), // Red
                Triple(35.0, "35 lbs", Color(0xFFFBC02D)), // Yellow
                Triple(25.0, "25 lbs", Color(0xFF388E3C)), // Green
                Triple(10.0, "10 lbs", Color(0xFF1976D2)), // Blue
                Triple(5.0, "5 lbs", Color(0xFF7B1FA2)),   // Purple
                Triple(2.5, "2.5 lbs", Color(0xFF616161)), // Gray
                Triple(1.25, "1.25 lbs", Color(0xFF455A64)) // Dark Gray Micro-plate
            )
            EquipmentCategory.STANDARD_BARBELL -> listOf(
                Triple(25.0, "25 lbs", Color(0xFF388E3C)),
                Triple(10.0, "10 lbs", Color(0xFF1976D2)),
                Triple(5.0, "5 lbs", Color(0xFF7B1FA2)),
                Triple(2.5, "2.5 lbs", Color(0xFF616161)),
                Triple(1.25, "1.25 lbs", Color(0xFF455A64))
            )
            EquipmentCategory.DUMBBELLS -> listOf(
                Triple(10.0, "10 lbs", Color(0xFF1976D2)),
                Triple(5.0, "5 lbs", Color(0xFF7B1FA2)),
                Triple(2.5, "2.5 lbs", Color(0xFF616161)),
                Triple(1.25, "1.25 lbs", Color(0xFF455A64))
            )
            EquipmentCategory.MACHINE_STACK -> listOf(
                Triple(20.0, "20 lbs Stack Plate", Color(0xFF1976D2)),
                Triple(10.0, "10 lbs Stack Plate", Color(0xFF388E3C)),
                Triple(5.0, "5 lbs Add-on Weight", Color(0xFF7B1FA2)),
                Triple(2.5, "2.5 lbs Micro-pin", Color(0xFFFBC02D))
            )
        }
    }

    val netWeight = max(0.0, targetWeight - selectedEquipment.tareWeightLbs)
    val weightPerSide = if (selectedCategory == EquipmentCategory.MACHINE_STACK) netWeight else netWeight / 2.0

    val plateBreakdown = remember(weightPerSide, availablePlates) {
        val result = mutableListOf<PlateCount>()
        var remaining = weightPerSide
        for ((plateWeight, _, color) in availablePlates) {
            val count = (remaining / plateWeight).toInt()
            if (count > 0) {
                result.add(PlateCount(plateWeight, count, color))
                remaining -= count * plateWeight
                remaining = (remaining * 100).toInt() / 100.0
            }
        }
        result
    }

    val unallocatedRemainder = remember(weightPerSide, plateBreakdown) {
        var accounted = 0.0
        for (pc in plateBreakdown) {
            accounted += pc.plateWeight * pc.count
        }
        val diff = weightPerSide - accounted
        (diff * 100).toInt() / 100.0
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Card with Back Navigation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("plate_calculator_header_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Navigation",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Column {
                        Text(
                            text = "Equipment & Plate Calculator",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Load calculation for Olympic, home 1\" bars, dumbbells & stacks",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Equipment Category Tab Selector
        TabRow(
            selectedTabIndex = selectedCategory.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
        ) {
            EquipmentCategory.values().forEach { cat ->
                Tab(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    text = {
                        Text(
                            text = cat.label,
                            fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Target Weight Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory == EquipmentCategory.DUMBBELLS) "Target Weight Per Dumbbell (lbs)" else "Target Total Weight (lbs)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${selectedEquipment.tareWeightLbs.toInt()} lb Tare",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            val current = targetWeightText.toDoubleOrNull() ?: 135.0
                            val step = if (selectedCategory == EquipmentCategory.DUMBBELLS) 2.5 else 5.0
                            targetWeightText = max(0.0, current - step).let {
                                if (it % 1.0 == 0.0) "${it.toInt()}" else "$it"
                            }
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .width(64.dp)
                            .testTag("decrease_weight_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease Weight")
                    }

                    OutlinedTextField(
                        value = targetWeightText,
                        onValueChange = { input ->
                            val sanitized = input.filter { it.isDigit() || it == '.' }
                            if (sanitized.count { it == '.' } <= 1) {
                                targetWeightText = sanitized
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("target_weight_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    FilledTonalButton(
                        onClick = {
                            val current = targetWeightText.toDoubleOrNull() ?: 135.0
                            val step = if (selectedCategory == EquipmentCategory.DUMBBELLS) 2.5 else 5.0
                            val next = current + step
                            targetWeightText = if (next % 1.0 == 0.0) "${next.toInt()}" else "$next"
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .width(64.dp)
                            .testTag("increase_weight_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase Weight")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Equipment Options Picker within Selected Category
                Text(
                    text = "Selected Implement / Bar",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                CustomFlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalSpacing = 6.dp,
                    verticalSpacing = 6.dp
                ) {
                    filteredEquipment.forEach { opt ->
                        val isSelected = selectedEquipment == opt
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedEquipment = opt },
                            label = {
                                Text(
                                    text = opt.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.defaultMinSize(minHeight = 48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Weight Presets based on Category
                Text(
                    text = "Quick Presets",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                val presets = when (selectedCategory) {
                    EquipmentCategory.DUMBBELLS -> listOf(15.0, 20.0, 25.0, 30.0, 35.0, 45.0, 50.0)
                    EquipmentCategory.MACHINE_STACK -> listOf(30.0, 50.0, 70.0, 90.0, 110.0, 130.0, 150.0)
                    EquipmentCategory.STANDARD_BARBELL -> listOf(45.0, 65.0, 85.0, 95.0, 115.0, 135.0)
                    EquipmentCategory.OLYMPIC_BARBELL -> listOf(65.0, 95.0, 115.0, 135.0, 155.0, 185.0, 225.0)
                }

                CustomFlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalSpacing = 6.dp,
                    verticalSpacing = 6.dp
                ) {
                    presets.forEach { preset ->
                        val isSelected = targetWeight == preset
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                targetWeightText = if (preset % 1.0 == 0.0) "${preset.toInt()}" else "$preset"
                            },
                            label = { Text("${if (preset % 1.0 == 0.0) preset.toInt() else preset} lbs") },
                            modifier = Modifier
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("preset_${preset.toInt()}")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Calculation Results Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = when (selectedCategory) {
                        EquipmentCategory.DUMBBELLS -> "Plates Per Dumbbell Handle"
                        EquipmentCategory.MACHINE_STACK -> "Pin Stack & Add-on Weight"
                        else -> "Plates Needed Per Side"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))

                if (targetWeight < selectedEquipment.tareWeightLbs) {
                    Text(
                        text = "Target load is less than empty implement weight (${selectedEquipment.tareWeightLbs} lbs).",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else if (netWeight == 0.0) {
                    Text(
                        text = "Empty Implement (${selectedEquipment.tareWeightLbs} lbs). No plates needed!",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    val summarySubtext = when (selectedCategory) {
                        EquipmentCategory.DUMBBELLS -> "Handle (${selectedEquipment.tareWeightLbs} lbs) + ${weightPerSide} lbs plates per hand"
                        EquipmentCategory.MACHINE_STACK -> "Total Stack Resistance: $targetWeight lbs"
                        else -> "Barbell (${selectedEquipment.tareWeightLbs} lbs) + 2 × (${weightPerSide} lbs per side)"
                    }
                    Text(
                        text = summarySubtext,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val sleeveSemanticDesc = remember(selectedEquipment, plateBreakdown, weightPerSide) {
                        "${selectedEquipment.name} with " + if (plateBreakdown.isEmpty()) "no plates"
                        else plateBreakdown.joinToString(", ") { "${it.count} plates of ${if (it.plateWeight % 1.0 == 0.0) it.plateWeight.toInt() else it.plateWeight} lbs" } + " per side"
                    }

                    // Visual Sleeve / Implement representation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 14.dp)
                            .semantics {
                                contentDescription = sleeveSemanticDesc
                            },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            // Bar sleeve or handle stub
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(44.dp)
                                    .background(Color.Gray, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${selectedEquipment.tareWeightLbs.toInt()}",
                                    fontSize = 8.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            plateBreakdown.forEach { pc ->
                                repeat(pc.count) {
                                    val plateWidth = when {
                                        pc.plateWeight >= 45.0 -> 24.dp
                                        pc.plateWeight >= 35.0 -> 20.dp
                                        pc.plateWeight >= 25.0 -> 16.dp
                                        pc.plateWeight >= 10.0 -> 14.dp
                                        else -> 10.dp
                                    }
                                    val plateHeight = when {
                                        pc.plateWeight >= 45.0 -> 72.dp
                                        pc.plateWeight >= 35.0 -> 62.dp
                                        pc.plateWeight >= 25.0 -> 54.dp
                                        pc.plateWeight >= 10.0 -> 46.dp
                                        else -> 36.dp
                                    }

                                    Box(
                                        modifier = Modifier
                                            .width(plateWidth)
                                            .height(plateHeight)
                                            .background(pc.color, RoundedCornerShape(4.dp))
                                            .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(4.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (pc.plateWeight >= 5.0) {
                                            Text(
                                                text = if (pc.plateWeight % 1.0 == 0.0) "${pc.plateWeight.toInt()}" else "${pc.plateWeight}",
                                                fontSize = 8.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Explicit text list of plates needed
                    plateBreakdown.forEach { pc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(pc.color, CircleShape)
                                )
                                Text(
                                    text = "${if (pc.plateWeight % 1.0 == 0.0) pc.plateWeight.toInt() else pc.plateWeight} lbs plate",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "× ${pc.count} ${if (selectedCategory == EquipmentCategory.MACHINE_STACK) "total" else "per side"}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (unallocatedRemainder > 0.0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Note: $unallocatedRemainder lbs remainder unallocated per side. Micro-plates or 1.25 lb fractional collars recommended.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
