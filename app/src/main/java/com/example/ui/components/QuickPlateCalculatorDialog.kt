package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.PlateCount
import com.example.ui.screens.VisualBarbell
import kotlin.math.max

/**
 * Quick Plate Calculator Dialog accessible during active workout sessions.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickPlateCalculatorDialog(
    initialWeight: Float,
    exerciseName: String = "Barbell Exercise",
    onApplyWeight: ((Float) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var barWeight by remember { mutableDoubleStateOf(45.0) }
    var currentWeightText by remember { mutableStateOf(if (initialWeight > 0f) "${initialWeight.toInt()}" else "135") }
    val totalWeight = currentWeightText.toDoubleOrNull() ?: 135.0

    val availablePlates = remember {
        listOf(
            Triple(45.0, "45 lbs", Color(0xFFD32F2F)), // Red
            Triple(35.0, "35 lbs", Color(0xFFFBC02D)), // Yellow
            Triple(25.0, "25 lbs", Color(0xFF388E3C)), // Green
            Triple(10.0, "10 lbs", Color(0xFF1976D2)), // Blue
            Triple(5.0, "5 lbs", Color(0xFF7B1FA2)),   // Purple
            Triple(2.5, "2.5 lbs", Color(0xFF616161)), // Gray
            Triple(1.25, "1.25 lbs", Color(0xFF455A64)) // Micro-plate
        )
    }

    val netWeight = max(0.0, totalWeight - barWeight)
    val weightPerSide = netWeight / 2.0

    val plateBreakdown = remember(weightPerSide) {
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

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("quick_plate_calculator_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = "Barbell Plate Calc",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Plate Calculator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = exerciseName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Total Weight Adjuster Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TOTAL TARGET WEIGHT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = {
                                    val current = currentWeightText.toDoubleOrNull() ?: 135.0
                                    val next = max(barWeight, current - 5.0)
                                    currentWeightText = if (next % 1.0 == 0.0) "${next.toInt()}" else "$next"
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Minus 5 lbs", tint = MaterialTheme.colorScheme.primary)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "${currentWeightText.ifBlank { "0" }} lbs",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            IconButton(
                                onClick = {
                                    val current = currentWeightText.toDoubleOrNull() ?: 135.0
                                    val next = current + 5.0
                                    currentWeightText = if (next % 1.0 == 0.0) "${next.toInt()}" else "$next"
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Plus 5 lbs", tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Steppers
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(-25, -10, +10, +25).forEach { delta ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.clickable {
                                        val current = currentWeightText.toDoubleOrNull() ?: 135.0
                                        val next = max(barWeight, current + delta)
                                        currentWeightText = if (next % 1.0 == 0.0) "${next.toInt()}" else "$next"
                                    }
                                ) {
                                    Text(
                                        text = if (delta > 0) "+$delta lbs" else "$delta lbs",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Barbell Selection Chips
                Text(
                    text = "Select Barbell / Equipment Tare:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val bars = listOf(
                        45.0 to "45 lb Olympic Bar",
                        33.0 to "33 lb Bar",
                        25.0 to "25 lb EZ/Short",
                        15.0 to "15 lb Light Bar"
                    )
                    bars.forEach { (weight, label) ->
                        val isSelected = barWeight == weight
                        FilterChip(
                            selected = isSelected,
                            onClick = { barWeight = weight },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Visual Barbell Graphic
                VisualBarbell(
                    plateBreakdown = plateBreakdown,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Plates Breakdown Summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Load Needed Each Side",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${weightPerSide} lbs",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))

                        if (plateBreakdown.isEmpty()) {
                            Text(
                                text = if (totalWeight <= barWeight) "Bar weight only (No plates needed)" else "No standard plates fit this weight",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                plateBreakdown.forEach { p ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = p.color.copy(alpha = 0.18f),
                                        border = BorderStroke(1.dp, p.color.copy(alpha = 0.6f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(p.color)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${p.count} × ${p.plateWeight} lb",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (onApplyWeight != null) {
                Button(
                    onClick = {
                        onApplyWeight(totalWeight.toFloat())
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Apply to Set", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(onClick = onDismiss) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
