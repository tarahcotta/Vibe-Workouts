package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class RpeGuidanceLevel(
    val rpe: Int,
    val label: String,
    val rirText: String,
    val clinicalDescription: String,
    val osteogenicZone: String,
    val isRecommended: Boolean,
    val badgeColor: Color
)

object RpeGuidanceRepository {
    val levels = listOf(
        RpeGuidanceLevel(
            rpe = 6,
            label = "Warmup / Technique",
            rirText = "4+ reps left in tank",
            clinicalDescription = "Light movement. Prepares joints and motor coordination, but below bone-remodeling stimulus threshold.",
            osteogenicZone = "Sub-Threshold",
            isRecommended = false,
            badgeColor = Color(0xFF64B5F6)
        ),
        RpeGuidanceLevel(
            rpe = 7,
            label = "Moderate Stimulus",
            rirText = "3 reps left in tank",
            clinicalDescription = "Solid working load. Safe for high-fatigue days or rehabilitation stages.",
            osteogenicZone = "Maintenance Zone",
            isRecommended = false,
            badgeColor = Color(0xFF81C784)
        ),
        RpeGuidanceLevel(
            rpe = 8,
            label = "Sweet Spot for Bone Density",
            rirText = "2 reps left in tank",
            clinicalDescription = "Clinical gold standard: Triggers mechanotransduction & osteoblast activity with pristine form and zero joint trauma.",
            osteogenicZone = "Optimal Bone Remodeling",
            isRecommended = true,
            badgeColor = Color(0xFF00E676)
        ),
        RpeGuidanceLevel(
            rpe = 9,
            label = "Very Heavy / Intense",
            rirText = "1 rep left in tank",
            clinicalDescription = "Near-maximal motor unit recruitment. Requires disciplined spinal bracing.",
            osteogenicZone = "High Mechanical Load",
            isRecommended = false,
            badgeColor = Color(0xFFFFB74D)
        ),
        RpeGuidanceLevel(
            rpe = 10,
            label = "Maximum Effort (Failure)",
            rirText = "0 reps left (Could not do another)",
            clinicalDescription = "Spinal breakdown risk increases. NOT recommended for longevity or osteogenic bone loading.",
            osteogenicZone = "Elevated Fatigue Risk",
            isRecommended = false,
            badgeColor = Color(0xFFE57373)
        )
    )
}

/**
 * Interactive RPE & Safety Effort Calibration Dialog
 * Mitigates unsafe assumptions around user understanding of RPE/RIR.
 */
@Composable
fun RpeEffortCalibrationDialog(
    currentRpe: Int,
    exerciseName: String,
    onSelectRpe: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedRpe by remember { mutableIntStateOf(currentRpe) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Calibrate Set Effort (RPE)",
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
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "RPE (Rating of Perceived Exertion) measures how close you were to muscular failure. RPE 8 (2 Reps in Reserve) is the proven sweet spot for bone remodeling.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                RpeGuidanceRepository.levels.forEach { level ->
                    val isChosen = selectedRpe == level.rpe
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRpe = level.rpe },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChosen) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        border = BorderStroke(
                            width = if (isChosen) 2.dp else 1.dp,
                            color = if (isChosen) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = level.badgeColor.copy(alpha = 0.2f),
                                        border = BorderStroke(1.dp, level.badgeColor)
                                    ) {
                                        Text(
                                            text = "RPE ${level.rpe}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = level.label,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                if (level.isRecommended) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            text = "RECOMMENDED",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "• ${level.rirText}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = level.clinicalDescription,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Safety Warning for RPE 9.5-10
                if (selectedRpe >= 10) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Safety Warning",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Clinical Warning: Training to absolute failure (RPE 10) on axial spinal lifts increases injury risk without offering additional bone density gains over RPE 8.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSelectRpe(selectedRpe)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Confirm RPE $selectedRpe", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
