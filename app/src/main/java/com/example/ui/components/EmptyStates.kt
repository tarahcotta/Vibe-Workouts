package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IllustrativeEmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionButton: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Empty State Illustration",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(34.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (actionButton != null) {
            Spacer(modifier = Modifier.height(20.dp))
            actionButton()
        }
    }
}

/**
 * High-craft Canvas-rendered illustration for the First Workout Onboarding state.
 * Features stylized Olympic barbell, posture alignment plumb line, bone density pulse waves,
 * and glowing focal halos.
 */
@Composable
fun WorkoutCanvasIllustration(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f + 10f

            // 1. Glowing Radial Aura Background
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.22f * pulseAnim),
                        secondaryColor.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = (w.coerceAtMost(h) * 0.75f) * pulseAnim
                ),
                center = Offset(cx, cy),
                radius = w * 0.45f
            )

            // 2. Concentric Orbit Rings (Osteogenic stimulus wave rings)
            drawCircle(
                color = primaryColor.copy(alpha = 0.18f),
                center = Offset(cx, cy),
                radius = 65.dp.toPx(),
                style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f))
            )
            drawCircle(
                color = secondaryColor.copy(alpha = 0.25f),
                center = Offset(cx, cy),
                radius = 45.dp.toPx(),
                style = Stroke(width = 2.dp.toPx())
            )

            // 3. Vertical Spinal Alignment Reference Plumb Line
            drawLine(
                color = Color(0xFF00E676).copy(alpha = 0.6f),
                start = Offset(cx, 15f),
                end = Offset(cx, h - 15f),
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
            )

            // 4. Stylized Barbell Rendering
            val barY = cy
            val barHalfWidth = 110.dp.toPx()
            val barThickness = 6.dp.toPx()

            // Steel Bar
            drawRoundRect(
                color = Color(0xFF90A4AE),
                topLeft = Offset(cx - barHalfWidth, barY - barThickness / 2),
                size = Size(barHalfWidth * 2, barThickness),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )

            // Knurling Grips (Textured zones)
            val knurlWidth = 30.dp.toPx()
            drawRoundRect(
                color = primaryColor.copy(alpha = 0.7f),
                topLeft = Offset(cx - 50.dp.toPx() - knurlWidth, barY - barThickness / 2),
                size = Size(knurlWidth, barThickness),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
            drawRoundRect(
                color = primaryColor.copy(alpha = 0.7f),
                topLeft = Offset(cx + 50.dp.toPx(), barY - barThickness / 2),
                size = Size(knurlWidth, barThickness),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )

            // Outer Olympic Weight Plates (Left)
            // Plate 1 (45 lbs large)
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(cx - barHalfWidth + 12.dp.toPx(), barY - 38.dp.toPx()),
                size = Size(10.dp.toPx(), 76.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            // Plate 2 (25 lbs medium)
            drawRoundRect(
                color = secondaryColor,
                topLeft = Offset(cx - barHalfWidth + 24.dp.toPx(), barY - 30.dp.toPx()),
                size = Size(8.dp.toPx(), 60.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )
            // Collar Lock
            drawRoundRect(
                color = Color(0xFF455A64),
                topLeft = Offset(cx - barHalfWidth + 34.dp.toPx(), barY - 10.dp.toPx()),
                size = Size(6.dp.toPx(), 20.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )

            // Outer Olympic Weight Plates (Right)
            // Plate 1 (45 lbs large)
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(cx + barHalfWidth - 22.dp.toPx(), barY - 38.dp.toPx()),
                size = Size(10.dp.toPx(), 76.dp.toPx()),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            // Plate 2 (25 lbs medium)
            drawRoundRect(
                color = secondaryColor,
                topLeft = Offset(cx + barHalfWidth - 32.dp.toPx(), barY - 30.dp.toPx()),
                size = Size(8.dp.toPx(), 60.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
            )
            // Collar Lock
            drawRoundRect(
                color = Color(0xFF455A64),
                topLeft = Offset(cx + barHalfWidth - 40.dp.toPx(), barY - 10.dp.toPx()),
                size = Size(6.dp.toPx(), 20.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )

            // 5. Center Biomechanical Node / Bone Strength Symbol
            drawCircle(
                color = primaryColor,
                center = Offset(cx, cy),
                radius = 8.dp.toPx()
            )
            drawCircle(
                color = Color.White,
                center = Offset(cx, cy),
                radius = 3.5.dp.toPx()
            )

            // 6. Upward Progression Spark Wave
            val sparkPath = Path().apply {
                moveTo(cx - 70.dp.toPx(), cy + 40.dp.toPx())
                cubicTo(
                    cx - 30.dp.toPx(), cy + 42.dp.toPx(),
                    cx - 10.dp.toPx(), cy + 20.dp.toPx(),
                    cx + 30.dp.toPx(), cy - 25.dp.toPx()
                )
                lineTo(cx + 65.dp.toPx(), cy - 45.dp.toPx())
            }
            drawPath(
                path = sparkPath,
                color = Color(0xFF00E676).copy(alpha = 0.7f),
                style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f))
            )
        }
    }
}

/**
 * Full visual card for new users guiding them through initiating their first workout log.
 */
@Composable
fun FirstWorkoutOnboardingCard(
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                .padding(20.dp)
        ) {
            // Header Pill & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "START YOUR JOURNEY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Text(
                    text = "3-Step Protocol",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Log Your First Resistance Session",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Begin osteogenic bone remodeling and postural strength with your prescribed foundation routine.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas Vector Art
            WorkoutCanvasIllustration(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(12.dp))

            // 3-Step Walkthrough Chips
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OnboardingStepRow(
                    stepNumber = "1",
                    icon = Icons.Default.FitnessCenter,
                    title = "Choose Prescribed Routine",
                    description = "Axial loading exercises carefully calibrated for spinal & hip density."
                )
                OnboardingStepRow(
                    stepNumber = "2",
                    icon = Icons.Default.Speed,
                    title = "Target RPE 7–8 (2–3 RIR)",
                    description = "Stimulate bone mechanotransduction without excessive neuromuscular fatigue."
                )
                OnboardingStepRow(
                    stepNumber = "3",
                    icon = Icons.Default.CheckCircle,
                    title = "Complete & Calculate Stimulus",
                    description = "Watch your Bone Density Score, lifetime volume, and PRs track automatically."
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Start CTA
            Button(
                onClick = onStartWorkout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Start First Workout Session",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun OnboardingStepRow(
    stepNumber: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stepNumber,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HistoryCanvasIllustration(
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f

            // Soft Background Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.15f),
                        secondaryColor.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = w * 0.4f
                ),
                center = Offset(cx, cy),
                radius = w * 0.4f
            )

            val barWidth = 24.dp.toPx()
            val spacing = 16.dp.toPx()
            val totalBars = 5
            val totalWidth = (totalBars * barWidth) + ((totalBars - 1) * spacing)
            val startX = (w - totalWidth) / 2f

            val heights = listOf(0.4f, 0.7f, 0.5f, 0.85f, 0.6f)
            for (i in 0 until totalBars) {
                val x = startX + i * (barWidth + spacing)
                val barH = h * 0.55f * heights[i]
                val topY = cy + 20.dp.toPx() - barH

                // Background track
                drawRoundRect(
                    color = primaryColor.copy(alpha = 0.1f),
                    topLeft = Offset(x, cy - 30.dp.toPx()),
                    size = Size(barWidth, 60.dp.toPx()),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )

                // Filled activity bar
                drawRoundRect(
                    color = if (i % 2 == 0) primaryColor else secondaryColor,
                    topLeft = Offset(x, topY),
                    size = Size(barWidth, cy + 20.dp.toPx() - topY),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
            }

            // Connecting trend curve
            val path = Path().apply {
                moveTo(startX + barWidth / 2f, cy - 10.dp.toPx())
                cubicTo(
                    startX + barWidth + spacing + barWidth / 2f, cy - 35.dp.toPx(),
                    startX + 2 * (barWidth + spacing) + barWidth / 2f, cy - 5.dp.toPx(),
                    startX + 3 * (barWidth + spacing) + barWidth / 2f, cy - 45.dp.toPx()
                )
                lineTo(startX + 4 * (barWidth + spacing) + barWidth / 2f, cy - 25.dp.toPx())
            }
            drawPath(
                path = path,
                color = Color(0xFF00E676),
                style = Stroke(width = 2.5.dp.toPx())
            )
        }
    }
}

@Composable
fun WorkoutHistoryEmptyCard(
    onStartWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "WORKOUT ARCHIVE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "No Workout History Yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Your completed sets, volume PRs, and consistency logs will appear here automatically after your first session.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            HistoryCanvasIllustration(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onStartWorkout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Start First Session",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

