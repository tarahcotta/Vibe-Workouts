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
        simulatedTScore >= -1.0f -> Color(0xFF00C853)
        simulatedTScore >= -2.5f -> Color(0xFFF57C00)
        else -> Color(0xFFD32F2F)
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==========================================
            // 1. HEADER & SCIENCE CONTEXT
            // ==========================================
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
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = "Clinical Science Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "DXA Bone Density Simulator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "LIFTMOR Clinical Trial Remodeling Model",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(
                    onClick = { showClinicalDetails = !showClinicalDetails },
                    modifier = Modifier
                        .size(36.dp)
                        .semantics {
                            contentDescription = if (showClinicalDetails) "Hide clinical trial details" else "Show clinical trial details"
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expandable Clinical Details Box
            AnimatedVisibility(visible = showClinicalDetails) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Clinical Foundation:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Based on the landmark LIFTMOR trial (Watson et al., JBMR 2018), high-intensity resistance training at RPE 8+ reversed bone mineral density decline in postmenopausal women with osteopenia/osteoporosis, improving femoral neck & lumbar spine T-scores.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ==========================================
            // 2. HERO METRIC DISPLAY (Projected T-Score & Net Change)
            // ==========================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .semantics(mergeDescendants = true) {
                            contentDescription = "Projected T-Score: ${String.format("%.2f", simulatedTScore)}, baseline ${String.format("%.2f", initialTScore)}. Status: $statusLabel. Estimated Bone Mineral Density Net Change: ${if (interventionGainPercent >= 0) "+" else ""}${String.format("%.1f", interventionGainPercent)}%."
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "PROJECTED T-SCORE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = String.format("%.2f", simulatedTScore),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = statusColor
                            )
                            Text(
                                text = "Base: ${String.format("%.2f", initialTScore)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = statusColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = statusLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Net Change Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (interventionGainPercent >= 0) Color(0xFF00C853).copy(alpha = 0.15f) else Color(0xFFD32F2F).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, if (interventionGainPercent >= 0) Color(0xFF00C853).copy(alpha = 0.3f) else Color(0xFFD32F2F).copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (interventionGainPercent >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = if (interventionGainPercent >= 0) Color(0xFF00C853) else Color(0xFFD32F2F),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "${if (interventionGainPercent >= 0) "+" else ""}${String.format("%.1f", interventionGainPercent)}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (interventionGainPercent >= 0) Color(0xFF00C853) else Color(0xFFD32F2F)
                                )
                            }
                            Text(
                                text = "Est. BMD Net Change",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 3. INTERACTIVE SIMULATION PARAMETERS
            // ==========================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Simulation Parameters",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Timeline Slider Section
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Training Duration",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "$weeks Weeks (${String.format("%.1f", weeks / 4.33f)} Mos)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
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
                    }

                    // Frequency & Comparison Selectors
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Weekly Protocol Frequency",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(1, 2, 3).forEach { freq ->
                                val isSel = !isSedentaryComparison && trainingFrequencyPerWeek == freq
                                FilterChip(
                                    selected = isSel,
                                    onClick = {
                                        isSedentaryComparison = false
                                        trainingFrequencyPerWeek = freq
                                    },
                                    label = { Text("${freq}x / wk", fontWeight = FontWeight.Bold) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .semantics {
                                            stateDescription = if (isSel) "$freq sessions per week selected" else "Select $freq sessions per week"
                                        }
                                )
                            }

                            FilterChip(
                                selected = isSedentaryComparison,
                                onClick = { isSedentaryComparison = !isSedentaryComparison },
                                label = { Text("Sedentary") },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(36.dp)
                                    .semantics {
                                        stateDescription = if (isSedentaryComparison) "Sedentary comparison mode active" else "Switch to sedentary comparison mode"
                                    }
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 4. CLINICAL TAKEAWAY SUMMARY
            // ==========================================
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isSedentaryComparison) {
                            "Without axial loading stimulus, age-related bone resorption outpaces formation by ~1-2% annually."
                        } else {
                            "Consistent ${trainingFrequencyPerWeek}x/week training signals bone osteoblasts to mineralize bone matrix, offsetting age-related loss."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

