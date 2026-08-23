package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Interactive Clinical DXA & T-Score Bone Density Simulator
 * Built on LIFTMOR & OPTIMA-Ex clinical resistance training research data.
 */
@Composable
fun BoneDensityDxaSimulatorCard(
    modifier: Modifier = Modifier,
    initialTScore: Float = -1.8f
) {
    var weeksOfTraining by remember { mutableFloatStateOf(24f) }
    var trainingFrequencyPerWeek by remember { mutableIntStateOf(2) }
    var isSedentaryComparison by remember { mutableStateOf(false) }
    var showClinicalDetails by remember { mutableStateOf(false) }

    // Dynamic DXA calculation models
    val weeks = weeksOfTraining.toInt()
    
    // Baseline model: Postmenopausal natural bone loss is ~ -1.2% per year (~ -0.023% per week)
    // LIFTMOR trial: High-intensity axial resistance training produces ~ +2.9% Lumbar & +2.4% Femoral Neck BMD over 8 months (32 weeks)
    val weeklyResistanceGainPercent = when (trainingFrequencyPerWeek) {
        1 -> 0.055f // Minimal osteogenic adaptation
        2 -> 0.092f // Clinical sweet spot
        else -> 0.115f // Accelerated bone remodeling
    }

    val naturalDeclinePercent = (weeks * 0.025f)
    val interventionGainPercent = if (isSedentaryComparison) {
        -naturalDeclinePercent
    } else {
        (weeks * weeklyResistanceGainPercent)
    }

    // T-Score delta: 1 SD in DXA T-Score is approx 10-12% BMD change
    val tScoreDelta = (interventionGainPercent / 10.5f)
    val simulatedTScore = initialTScore + tScoreDelta

    val statusLabel = when {
        simulatedTScore >= -1.0f -> "Normal Bone Density"
        simulatedTScore >= -2.5f -> "Osteopenia (Low Mass)"
        else -> "Osteoporosis"
    }

    val statusColor = when {
        simulatedTScore >= -1.0f -> Color(0xFF00E676)
        simulatedTScore >= -2.5f -> Color(0xFFFFB74D)
        else -> Color(0xFFE57373)
    }

    val dxaAccessibleSummary = remember(simulatedTScore, initialTScore, interventionGainPercent, weeks, trainingFrequencyPerWeek, isSedentaryComparison) {
        if (isSedentaryComparison) {
            "Simulated sedentary bone mineral density trend over $weeks weeks. Projected T-score is ${String.format("%.2f", simulatedTScore)} from baseline ${String.format("%.2f", initialTScore)}, representing a net decline of ${String.format("%.1f", -interventionGainPercent)} percent due to natural age-related resorption."
        } else {
            "Simulated LIFTMOR high-intensity resistance training bone density projection over $weeks weeks with $trainingFrequencyPerWeek sessions per week. Projected T-score improves to ${String.format("%.2f", simulatedTScore)} from baseline ${String.format("%.2f", initialTScore)} ($statusLabel), with an estimated bone mineral density net increase of ${String.format("%.1f", interventionGainPercent)} percent."
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                heading()
                contentDescription = dxaAccessibleSummary
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header with Science Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = "Clinical Science Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DXA Bone Density Simulator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "LIFTMOR Clinical Remodeling Model",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(
                    onClick = { showClinicalDetails = !showClinicalDetails },
                    modifier = Modifier
                        .size(32.dp)
                        .semantics {
                            contentDescription = if (showClinicalDetails) "Hide clinical trial details" else "Show clinical trial details"
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Prominent Non-Diagnostic Research Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RESEARCH SIMULATION — NOT A MEDICAL DIAGNOSIS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            AnimatedVisibility(visible = showClinicalDetails) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Clinical Foundation:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Based on the landmark LIFTMOR trial (Watson et al., JBMR 2018), high-intensity resistance training at RPE 8+ reversed bone mineral density decline in postmenopausal women with osteopenia/osteoporosis, improving femoral neck & lumbar spine T-scores while reducing kyphosis.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Metric Comparison Display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(14.dp)
                    .semantics(mergeDescendants = true) {
                        contentDescription = "Projected T-Score: ${String.format("%.2f", simulatedTScore)}, baseline ${String.format("%.2f", initialTScore)}. Status: $statusLabel. Estimated Bone Mineral Density Net Change: ${if (interventionGainPercent >= 0) "+" else ""}${String.format("%.1f", interventionGainPercent)}%."
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Projected T-Score",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format("%.2f", simulatedTScore),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = statusColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(Base: ${String.format("%.2f", initialTScore)})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (interventionGainPercent >= 0) Color(0xFF00E676).copy(alpha = 0.15f) else Color(0xFFE57373).copy(alpha = 0.15f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (interventionGainPercent >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = if (interventionGainPercent >= 0) Color(0xFF00E676) else Color(0xFFE57373),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${if (interventionGainPercent >= 0) "+" else ""}${String.format("%.1f", interventionGainPercent)}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (interventionGainPercent >= 0) Color(0xFF00E676) else Color(0xFFE57373)
                            )
                        }
                        Text(
                            text = "Estimated BMD Net Change",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Timeline Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Training Duration:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$weeks Weeks (${String.format("%.1f", weeks / 4.33f)} Months)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Slider(
                value = weeksOfTraining,
                onValueChange = { weeksOfTraining = it },
                valueRange = 4f..104f,
                steps = 24,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.semantics {
                    contentDescription = "Simulated training duration slider"
                    stateDescription = "$weeks weeks"
                }
            )

            // Frequency & Comparison Selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Frequency:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(1, 2, 3).forEach { freq ->
                        val isSel = !isSedentaryComparison && trainingFrequencyPerWeek == freq
                        FilterChip(
                            selected = isSel,
                            onClick = {
                                isSedentaryComparison = false
                                trainingFrequencyPerWeek = freq
                            },
                            label = { Text("${freq}x/wk", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier
                                .height(28.dp)
                                .semantics {
                                    stateDescription = if (isSel) "$freq sessions per week selected" else "Select $freq sessions per week"
                                }
                        )
                    }

                    FilterChip(
                        selected = isSedentaryComparison,
                        onClick = { isSedentaryComparison = !isSedentaryComparison },
                        label = { Text("Sedentary", fontSize = 11.sp) },
                        modifier = Modifier
                            .height(28.dp)
                            .semantics {
                                stateDescription = if (isSedentaryComparison) "Sedentary comparison mode active" else "Switch to sedentary comparison mode"
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Clinical Takeaway Callout
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSedentaryComparison) {
                            "Without axial loading stimulus, age-related bone resorption outpaces formation by ~1-2% annually."
                        } else {
                            "Consistent ${trainingFrequencyPerWeek}x/week training signals bone osteoblasts to mineralize bone matrix, offsetting age-related loss."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
