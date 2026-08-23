package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BoneDensityGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Animated Checkmark / Completion Button for individual workout sets.
 * Features a spring pop animation, ripple burst expansion, and celebratory micro-particles.
 */
@Composable
fun AnimatedSetCompletionButton(
    isCompleted: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    testTag: String = "animated_set_completion_btn"
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    
    // Scale animation on tap
    val scaleAnim = remember { Animatable(1f) }
    // Ring burst animation
    val burstRadiusAnim = remember { Animatable(0f) }
    val burstAlphaAnim = remember { Animatable(0f) }
    
    // Particle burst state
    var showSparkles by remember { mutableStateOf(false) }

    val activeColor = Color(0xFF00C853) // Vibrant emerald green
    val inactiveColor = MaterialTheme.colorScheme.surfaceVariant
    val iconColor = if (isCompleted) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        // Celebratory Ring Burst Canvas
        if (burstAlphaAnim.value > 0f) {
            Canvas(modifier = Modifier.size(size * 1.8f)) {
                val radius = (this.size.minDimension / 2) * burstRadiusAnim.value
                drawCircle(
                    color = activeColor.copy(alpha = burstAlphaAnim.value),
                    radius = radius,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }

        // Celebratory Sparkle Particles
        if (showSparkles) {
            SparkleParticleBurst(
                modifier = Modifier.size(size * 2f),
                color = activeColor
            )
        }

        // Main Checkmark Button
        Surface(
            modifier = Modifier
                .size(size)
                .scale(scaleAnim.value)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    coroutineScope.launch {
                        if (!isCompleted) {
                            // Trigger spring pop and burst
                            showSparkles = true
                            launch {
                                scaleAnim.animateTo(
                                    targetValue = 1.28f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                                scaleAnim.animateTo(
                                    targetValue = 1f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            }
                            launch {
                                burstRadiusAnim.snapTo(0.2f)
                                burstAlphaAnim.snapTo(0.9f)
                                burstRadiusAnim.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
                                burstAlphaAnim.animateTo(0f, tween(300, easing = LinearEasing))
                                showSparkles = false
                            }
                        } else {
                            scaleAnim.animateTo(0.88f, tween(80))
                            scaleAnim.animateTo(1f, spring(Spring.DampingRatioMediumBouncy))
                        }
                    }
                    onToggle()
                }
                .testTag(testTag)
                .semantics {
                    contentDescription = if (isCompleted) "Completed Set. Tap to unmark." else "Mark Set as Completed."
                },
            shape = RoundedCornerShape(12.dp),
            color = if (isCompleted) activeColor else inactiveColor,
            shadowElevation = if (isCompleted) 4.dp else 0.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier
                        .size(size * 0.5f)
                        .scale(if (isCompleted) 1f else 0.8f)
                )
            }
        }
    }
}

/**
 * Micro-particle sparkle burst that shoots out radially when completing a set.
 */
@Composable
private fun SparkleParticleBurst(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00E676),
    particleCount: Int = 8
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(450, easing = FastOutSlowInEasing))
    }

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val maxDist = size.minDimension * 0.45f

        for (i in 0 until particleCount) {
            val angle = (i * (360f / particleCount)) * (Math.PI / 180f)
            val currentDist = maxDist * progress.value
            val px = center.x + (cos(angle) * currentDist).toFloat()
            val py = center.y + (sin(angle) * currentDist).toFloat()
            val particleAlpha = (1f - progress.value).coerceIn(0f, 1f)
            val particleRadius = (4.dp.toPx() * (1f - progress.value * 0.5f)).coerceAtLeast(1f)

            drawCircle(
                color = color.copy(alpha = particleAlpha),
                radius = particleRadius,
                center = Offset(px, py)
            )
        }
    }
}

/**
 * Confetti particle data structure for full-screen PR celebratory animations.
 */
private data class ConfettiParticle(
    val id: Int,
    val xOffsetNorm: Float, // 0..1 across screen width
    val speedY: Float,
    val sizePx: Float,
    val color: Color,
    val initialRotation: Float,
    val rotationSpeed: Float,
    val isCircle: Boolean
)

/**
 * High-craft Full-Screen / Modal Personal Record (PR) Celebration Dialog.
 * Displays interactive confetti physics, golden rotating halo radiance, spring-bounced trophy icon,
 * and high-contrast statistical load comparison.
 */
@Composable
fun PersonalRecordCelebrationDialog(
    exerciseName: String,
    newWeightLbs: Float,
    previousMaxLbs: Float,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    // Auto dismiss after 4.5 seconds of celebratory glory
    LaunchedEffect(Unit) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        delay(4500)
        onDismiss()
    }

    // Infinite rotating halo and pulsing shimmer for gold trophy
    val infiniteTransition = rememberInfiniteTransition(label = "trophy_glow")
    val glowRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glow_rotation"
    )
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    // Spring scale pop for trophy
    val trophyScale = remember { Animatable(0.3f) }
    LaunchedEffect(Unit) {
        trophyScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // Generate confetti particles
    val particles = remember {
        val colors = listOf(
            BoneDensityGold,
            Color(0xFFFFD54F),
            Color(0xFF00E676),
            Color(0xFF00B0FF),
            Color(0xFFFF4081),
            Color(0xFFB388FF),
            Color(0xFFFFAB00)
        )
        List(40) { id ->
            ConfettiParticle(
                id = id,
                xOffsetNorm = Random.nextFloat(),
                speedY = Random.nextFloat() * 400f + 300f,
                sizePx = Random.nextFloat() * 12f + 8f,
                color = colors[id % colors.size],
                initialRotation = Random.nextFloat() * 360f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    val confettiAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        confettiAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(3500, easing = LinearEasing)
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            // Confetti Falling Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height
                val t = confettiAnim.value

                particles.forEach { p ->
                    val currentY = (p.speedY * t * (canvasH / 600f)) % (canvasH + 50f) - 50f
                    val currentX = p.xOffsetNorm * canvasW + sin((t * 6f) + p.id) * 30f
                    val currentRot = p.initialRotation + p.rotationSpeed * t
                    val alpha = (1f - (currentY / (canvasH + 40f))).coerceIn(0.2f, 1f)

                    rotate(degrees = currentRot, pivot = Offset(currentX, currentY)) {
                        if (p.isCircle) {
                            drawCircle(
                                color = p.color.copy(alpha = alpha),
                                radius = p.sizePx / 2,
                                center = Offset(currentX, currentY)
                            )
                        } else {
                            drawRect(
                                color = p.color.copy(alpha = alpha),
                                topLeft = Offset(currentX - p.sizePx / 2, currentY - p.sizePx / 2),
                                size = Size(p.sizePx, p.sizePx * 1.6f)
                            )
                        }
                    }
                }
            }

            // Central Celebratory Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp)
                    .scale(trophyScale.value)
                    .testTag("pr_celebration_dialog")
                    .semantics {
                        heading()
                        contentDescription = "New Personal Record Achieved for $exerciseName. Loaded ${newWeightLbs.toInt()} pounds."
                    },
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 24.dp,
                border = BorderStroke(2.dp, BoneDensityGold)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    BoneDensityGold.copy(alpha = 0.2f),
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close celebration",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Rotating Golden Halo + Trophy Icon
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(110.dp)
                    ) {
                        // Halo Radiance Glow
                        Canvas(
                            modifier = Modifier
                                .size(110.dp)
                                .scale(glowScale)
                                .rotate(glowRotation)
                        ) {
                            drawCircle(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        BoneDensityGold.copy(alpha = 0.8f),
                                        Color(0xFFFFD54F).copy(alpha = 0.3f),
                                        Color.Transparent,
                                        BoneDensityGold.copy(alpha = 0.8f)
                                    )
                                ),
                                style = Stroke(width = 6.dp.toPx())
                            )
                        }

                        // Gold Circle Trophy Base
                        Surface(
                            shape = CircleShape,
                            color = BoneDensityGold,
                            modifier = Modifier.size(76.dp),
                            shadowElevation = 10.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Trophy",
                                    tint = Color.Black,
                                    modifier = Modifier.size(42.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title & Sparkles
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = BoneDensityGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "NEW PERSONAL RECORD!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = BoneDensityGold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = BoneDensityGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = exerciseName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Progressive Weight Progression Comparison Box
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "PREVIOUS PR",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (previousMaxLbs > 0f) "${previousMaxLbs.toInt()} lbs" else "Baseline",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = "Overload Progression",
                                tint = BoneDensityGold,
                                modifier = Modifier.size(28.dp)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "NEW BENCHMARK",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BoneDensityGold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${newWeightLbs.toInt()} LBS",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BoneDensityGold
                                )
                            }
                        }
                    }

                    val delta = newWeightLbs - previousMaxLbs
                    if (previousMaxLbs > 0f && delta > 0f) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF00C853).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "+${delta.toInt()} LBS MECHANICAL OVERLOAD GAIN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF00C853),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Bone remodeling and mechanotransduction stimulated! Exceptional strength progression.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Celebration Action Button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("celebration_continue_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BoneDensityGold,
                            contentColor = Color.Black
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Keep Crushing It!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
