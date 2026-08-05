package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

data class BarbellOption(val name: String, val weightLbs: Double)

data class PlateCount(val plateWeight: Double, val count: Int, val color: Color)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlateCalculatorScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var targetWeightText by remember { mutableStateOf("135") }
    val targetWeight = targetWeightText.toDoubleOrNull() ?: 135.0

    val barbells = listOf(
        BarbellOption("Standard Olympic Bar", 45.0),
        BarbellOption("Women's Olympic Bar", 35.0),
        BarbellOption("Technique / Training Bar", 15.0),
        BarbellOption("EZ Curl / Special Bar", 25.0)
    )

    var selectedBarbell by remember { mutableStateOf(barbells[0]) }

    val availablePlates = listOf(
        Triple(45.0, "45 lbs", Color(0xFFD32F2F)), // Red
        Triple(35.0, "35 lbs", Color(0xFFFBC02D)), // Yellow
        Triple(25.0, "25 lbs", Color(0xFF388E3C)), // Green
        Triple(10.0, "10 lbs", Color(0xFF1976D2)), // Blue
        Triple(5.0, "5 lbs", Color(0xFF7B1FA2)),   // Purple
        Triple(2.5, "2.5 lbs", Color(0xFF616161)) // Gray
    )

    val netWeight = max(0.0, targetWeight - selectedBarbell.weightLbs)
    val weightPerSide = netWeight / 2.0

    val plateBreakdown = remember(weightPerSide) {
        val result = mutableListOf<PlateCount>()
        var remaining = weightPerSide
        for ((plateWeight, _, color) in availablePlates) {
            val count = (remaining / plateWeight).toInt()
            if (count > 0) {
                result.add(PlateCount(plateWeight, count, color))
                remaining -= count * plateWeight
                remaining = (remaining * 100).toInt() / 100.0 // fix floating point precision
            }
        }
        result
    }

    val unallocatedRemainder = remember(weightPerSide, plateBreakdown) {
        var accounted = 0.0
        for (pc in plateBreakdown) {
            accounted += pc.plateWeight * pc.count
        }
        weightPerSide - accounted
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("plate_calculator_header_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Plate Calculator",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Calculate exact plates needed per barbell side",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Target Weight Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Target Total Weight (lbs)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val current = targetWeightText.toDoubleOrNull() ?: 135.0
                            targetWeightText = max(0.0, current - 5.0).toInt().toString()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("decrease_weight_button")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease 5 lbs")
                    }

                    OutlinedTextField(
                        value = targetWeightText,
                        onValueChange = { targetWeightText = it },
                        modifier = Modifier
                            .weight(1f)
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

                    OutlinedButton(
                        onClick = {
                            val current = targetWeightText.toDoubleOrNull() ?: 135.0
                            targetWeightText = (current + 5.0).toInt().toString()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("increase_weight_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase 5 lbs")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Presets
                Text(
                    text = "Quick Presets",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                val presets = listOf(65.0, 95.0, 115.0, 135.0, 185.0, 225.0, 275.0, 315.0)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { preset ->
                        val isSelected = targetWeight == preset
                        FilterChip(
                            selected = isSelected,
                            onClick = { targetWeightText = preset.toInt().toString() },
                            label = { Text("${preset.toInt()} lbs") },
                            modifier = Modifier.testTag("preset_${preset.toInt()}")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Barbell Selection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Barbell Type",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                barbells.forEach { bar ->
                    val isSelected = selectedBarbell == bar
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedBarbell = bar }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = bar.name,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${bar.weightLbs.toInt()} lbs",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
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
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Plates Needed Per Side",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (targetWeight < selectedBarbell.weightLbs) {
                    Text(
                        text = "Target weight is less than the barbell weight (${selectedBarbell.weightLbs} lbs).",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else if (netWeight == 0.0) {
                    Text(
                        text = "Empty Barbell (${selectedBarbell.weightLbs} lbs). No plates needed!",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "Barbell (${selectedBarbell.weightLbs} lbs) + 2 × (${weightPerSide} lbs per side)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Visual Barbell Sleeve representation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Bar sleeve stub
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(40.dp)
                                    .background(Color.Gray, RoundedCornerShape(4.dp))
                            )

                            plateBreakdown.forEach { pc ->
                                repeat(pc.count) {
                                    val plateWidth = when {
                                        pc.plateWeight >= 45.0 -> 24.dp
                                        pc.plateWeight >= 35.0 -> 20.dp
                                        pc.plateWeight >= 25.0 -> 16.dp
                                        else -> 12.dp
                                    }
                                    val plateHeight = when {
                                        pc.plateWeight >= 45.0 -> 70.dp
                                        pc.plateWeight >= 35.0 -> 60.dp
                                        pc.plateWeight >= 25.0 -> 52.dp
                                        else -> 40.dp
                                    }

                                    Box(
                                        modifier = Modifier
                                            .width(plateWidth)
                                            .height(plateHeight)
                                            .background(pc.color, RoundedCornerShape(4.dp))
                                            .border(1.dp, Color.White, RoundedCornerShape(4.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (pc.plateWeight >= 25.0) {
                                            Text(
                                                text = pc.plateWeight.toInt().toString(),
                                                fontSize = 9.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                                    text = "${pc.plateWeight} lbs plate",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "× ${pc.count} per side",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (unallocatedRemainder > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Note: $unallocatedRemainder lbs remaining unallocated per side (smaller fractional plates may be required).",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
