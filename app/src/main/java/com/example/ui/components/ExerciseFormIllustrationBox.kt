package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MotionPhotosAuto
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class FormPhase(
    val title: String,
    val description: String,
    val safetyCue: String
)

data class ExerciseTechniqueGuide(
    val exerciseName: String,
    val liftCategory: String, // e.g. "Squat / Knee Dominant", "Hinge / Posterior Chain", "Overhead Press"
    val phases: List<FormPhase>,
    val jointSafetyCheckpoints: List<String>,
    val commonMistakesToAvoid: List<String>
)

object ExerciseTechniqueRepository {

    fun getGuideForExercise(exerciseName: String): ExerciseTechniqueGuide {
        val lower = exerciseName.lowercase()
        return when {
            lower.contains("squat") || lower.contains("leg press") -> ExerciseTechniqueGuide(
                exerciseName = exerciseName,
                liftCategory = "Squat • Knee & Hip Biomechanics",
                phases = listOf(
                    FormPhase("1. Setup & Stance", "Feet shoulder-width apart, 15-30° toe flare. Inhale into 360° belly brace.", "Keep heels firmly glued to floor. Distribute weight across tripod foot."),
                    FormPhase("2. Eccentric Descent", "Unlock hips and knees simultaneously. Push knees out in line with toes.", "Maintain neutral spine; do not let lower back round into a butt-wink."),
                    FormPhase("3. Bottom Depth", "Hip crease drops parallel to or slightly below knee joint.", "Maintain bar directly over mid-foot balance point."),
                    FormPhase("4. Drive & Lockout", "Drive ground away through mid-foot, extending hips and knees in tandem.", "Exhale through braced abdomen at full lockout.")
                ),
                jointSafetyCheckpoints = listOf(
                    "Vertical Bar Path: Keeps weight over center of mass, protecting lower back lumbar discs.",
                    "Active Knee Tracking: Prevents knee valgus (inward collapse) to safeguard ACL & patellar tendon.",
                    "Rigid Core Cylinder: Neutral spine alignment transmits force safely into femur and hip bone matrix."
                ),
                commonMistakesToAvoid = listOf(
                    "Rising hips too fast (Good-morning squat pattern)",
                    "Heels lifting off ground due to ankle stiffness",
                    "Caving knees inward during ascending drive phase"
                )
            )

            lower.contains("deadlift") || lower.contains("rdl") || lower.contains("hinge") -> ExerciseTechniqueGuide(
                exerciseName = exerciseName,
                liftCategory = "Hinge • Posterior Chain Mechanics",
                phases = listOf(
                    FormPhase("1. Wedge & Tension", "Shin 1 inch from bar. Hinge hips back, engage lats into back pockets.", "Pull slack out of barbell before driving off floor."),
                    FormPhase("2. Initial Pull", "Push floor away with legs while holding torso angle static.", "Keep barbell path tight against shins and thighs."),
                    FormPhase("3. Hip Drive", "Once bar clears knees, snap hips forward into full extension.", "Squeeze glutes hard without hyperextending lumbar spine."),
                    FormPhase("4. Controlled Eccentric", "Hinge at hips first until bar passes knees, then bend knees.", "Never let bar drift forward away from gravity center.")
                ),
                jointSafetyCheckpoints = listOf(
                    "Anti-Flexion Back Brace: Maintains natural lumbar arch under high shear forces.",
                    "Lat Activation: Secures scapula to eliminate upper back rounding and cervical strain.",
                    "Posterior Loading: Maximizes glute & hamstring bone-density osteogenic stimulus."
                ),
                commonMistakesToAvoid = listOf(
                    "Rounding lumbar spine when pulling off floor",
                    "Bouncing bar off ground or jerking slack out of bar",
                    "Hyperextending lower back at top lockout"
                )
            )

            lower.contains("press") || lower.contains("bench") || lower.contains("dip") -> ExerciseTechniqueGuide(
                exerciseName = exerciseName,
                liftCategory = "Pressing • Upper Body Scapular Control",
                phases = listOf(
                    FormPhase("1. Scapular Retraction", "Pinch shoulder blades together and depress down into bench/seat.", "Creates stable platform for shoulder socket safety."),
                    FormPhase("2. Controlled Lowering", "Lower bar/dumbbell under control toward mid-sternum.", "Forearms stay vertically stacked underneath wrists and elbows."),
                    FormPhase("3. Turnaround", "Touch lightly without bouncing off ribs.", "Keep thoracic chest arch high and feet planted."),
                    FormPhase("4. Pressing Arc", "Drive up and slightly backward over shoulder joint center.", "Lock out elbows with controlled shoulder protraction.")
                ),
                jointSafetyCheckpoints = listOf(
                    "Stacked Forearms: Eliminates rotational torque on wrist and elbow joint ligaments.",
                    "Depressed Scapulae: Creates subacromial clearance preventing shoulder impingement.",
                    "Glute Drive: Stabilizes lower lumbar arch during heavy pressing loads."
                ),
                commonMistakesToAvoid = listOf(
                    "Flaring elbows out to 90 degrees (keep at 45-60 degree angle)",
                    "Lifting hips off bench during heavy reps",
                    "Collapsing chest at bottom of movement"
                )
            )

            else -> ExerciseTechniqueGuide(
                exerciseName = exerciseName,
                liftCategory = "Compound Lift • Structural Alignment",
                phases = listOf(
                    FormPhase("1. Solid Base Setup", "Plant feet firmly, establish stable center of balance.", "Brace abdominal wall prior to weight motion."),
                    FormPhase("2. Controlled Movement", "Move through full pain-free range of motion with tempo.", "Control weight speed on both lowering and lifting phases."),
                    FormPhase("3. Lockout Position", "Complete movement with stable joint alignment.", "Breathe regularly and maintain postural alignment.")
                ),
                jointSafetyCheckpoints = listOf(
                    "Neutral Spine Maintenance: Protects spine under loaded tension.",
                    "Controlled Tempo: Minimizes sudden momentum shock on tendons and joints.",
                    "Full Range of Motion: Stimulates bone remodeling along line of force."
                ),
                commonMistakesToAvoid = listOf(
                    "Rushing reps using erratic momentum",
                    "Holding breath excessively without proper bracing",
                    "Sacrificing range of motion for excessive weight"
                )
            )
        }
    }
}

@Composable
fun ExerciseFormIllustrationBox(
    exerciseName: String,
    modifier: Modifier = Modifier
) {
    val guide = remember(exerciseName) { ExerciseTechniqueRepository.getGuideForExercise(exerciseName) }
    var selectedPhaseIndex by remember { mutableIntStateOf(0) }
    var isAnimating by remember { mutableStateOf(true) }
    var isSlowMotion by remember { mutableStateOf(false) }

    // Animation progresses bar motion back and forth
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(isAnimating, isSlowMotion) {
        if (isAnimating) {
            animProgress.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = if (isSlowMotion) 3200 else 1800,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            animProgress.stop()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("exercise_form_illustration_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Exercise Title & Category Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exerciseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = guide.liftCategory,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Form Mastery",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Biomechanical Canvas Demonstration Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val secondaryColor = MaterialTheme.colorScheme.secondary
                val tertiaryColor = MaterialTheme.colorScheme.tertiary
                val surfaceColor = MaterialTheme.colorScheme.surface

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Grid lines (Form alignment grid background)
                    val gridStep = 40f
                    var x = 0f
                    while (x < w) {
                        drawLine(
                            color = primaryColor.copy(alpha = 0.06f),
                            start = Offset(x, 0f),
                            end = Offset(x, h),
                            strokeWidth = 1f
                        )
                        x += gridStep
                    }
                    var y = 0f
                    while (y < h) {
                        drawLine(
                            color = primaryColor.copy(alpha = 0.06f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                        y += gridStep
                    }

                    // Green Ideal Vertical Bar Path Reference Line
                    val midX = w * 0.48f
                    drawLine(
                        color = secondaryColor.copy(alpha = 0.6f),
                        start = Offset(midX, 25f),
                        end = Offset(midX, h - 30f),
                        strokeWidth = 2.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    )

                    // Platform Ground line
                    drawLine(
                        color = primaryColor.copy(alpha = 0.4f),
                        start = Offset(30f, h - 30f),
                        end = Offset(w - 30f, h - 30f),
                        strokeWidth = 4f
                    )

                    // Animated Avatar Biomechanics based on exercise category
                    val progress = animProgress.value
                    val lower = exerciseName.lowercase()

                    if (lower.contains("squat") || lower.contains("leg press")) {
                        // Animated Squat Biomechanics
                        val hipY = (h - 90f) - (progress * 55f)
                        val kneeX = midX - 35f - (progress * 15f)
                        val kneeY = h - 55f
                        val shoulderX = midX - 10f
                        val shoulderY = hipY - 50f
                        val headY = shoulderY - 20f
                        val barY = shoulderY

                        // Spine Path
                        val spinePath = Path().apply {
                            moveTo(midX, h - 30f) // Foot
                            lineTo(kneeX, kneeY)   // Knee
                            lineTo(midX - 25f, hipY) // Hip
                            lineTo(shoulderX, shoulderY) // Shoulder
                        }

                        drawPath(
                            path = spinePath,
                            color = primaryColor,
                            style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )

                        // Head
                        drawCircle(color = primaryColor, radius = 12f, center = Offset(shoulderX, headY))

                        // Barbell Plates & Trajectory Line
                        drawCircle(color = tertiaryColor, radius = 22f, center = Offset(midX, barY))
                        drawCircle(color = surfaceColor, radius = 10f, center = Offset(midX, barY))
                        drawLine(
                            color = tertiaryColor,
                            start = Offset(midX - 40f, barY),
                            end = Offset(midX + 40f, barY),
                            strokeWidth = 6f
                        )
                    } else if (lower.contains("deadlift") || lower.contains("rdl") || lower.contains("hinge")) {
                        // Animated Hinge / Deadlift Biomechanics
                        val barY = (h - 55f) - (progress * 80f)
                        val hipX = midX - 45f + (progress * 25f)
                        val hipY = (h - 85f) - (progress * 20f)
                        val shoulderX = midX + 10f - (progress * 20f)
                        val shoulderY = barY - 25f

                        val hingePath = Path().apply {
                            moveTo(midX, h - 30f) // Foot
                            lineTo(midX - 15f, h - 60f) // Knee
                            lineTo(hipX, hipY) // Hip
                            lineTo(shoulderX, shoulderY) // Shoulder
                        }

                        drawPath(
                            path = hingePath,
                            color = primaryColor,
                            style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )

                        drawCircle(color = primaryColor, radius = 12f, center = Offset(shoulderX, shoulderY - 18f))

                        // Barbell
                        drawCircle(color = tertiaryColor, radius = 24f, center = Offset(midX, barY))
                        drawCircle(color = surfaceColor, radius = 10f, center = Offset(midX, barY))
                    } else {
                        // Generic Pressing / Upper Body Biomechanics
                        val barY = (h - 70f) - (progress * 90f)
                        val bodyPath = Path().apply {
                            moveTo(midX - 30f, h - 30f)
                            lineTo(midX - 10f, h - 80f)
                            lineTo(midX, h - 130f)
                        }
                        drawPath(
                            path = bodyPath,
                            color = primaryColor,
                            style = Stroke(width = 8f, cap = StrokeCap.Round)
                        )
                        drawCircle(color = primaryColor, radius = 12f, center = Offset(midX, h - 150f))

                        // Barbell over shoulder line
                        drawCircle(color = tertiaryColor, radius = 20f, center = Offset(midX, barY))
                        drawCircle(color = surfaceColor, radius = 8f, center = Offset(midX, barY))
                    }
                }

                // Overlay Controls: Play/Pause, Slow-Mo & Phase Indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.clickable { isAnimating = !isAnimating }
                        ) {
                            Box(modifier = Modifier.padding(6.dp)) {
                                Icon(
                                    imageVector = if (isAnimating) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSlowMotion) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { isSlowMotion = !isSlowMotion }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SlowMotionVideo,
                                    contentDescription = "Icon",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isSlowMotion) "0.5x Slow-Mo" else "1.0x Speed",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MotionPhotosAuto,
                            contentDescription = "Icon",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ideal Bar Path Tracked",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Phase Selector Chips
            Text(
                text = "Biomechanical Movement Phases",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                guide.phases.forEachIndexed { idx, phase ->
                    val isSelected = selectedPhaseIndex == idx
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPhaseIndex = idx },
                        label = {
                            Text(
                                text = "Phase ${idx + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Selected Phase Detail Box
            val currentPhase = guide.phases.getOrNull(selectedPhaseIndex) ?: guide.phases.first()
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = currentPhase.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentPhase.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Icon",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Safety Cue: ${currentPhase.safetyCue}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Joint Safety Checkpoints
            Text(
                text = "Joint Safety & Osteogenic Remodeling Cues",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            guide.jointSafetyCheckpoints.forEach { checkpoint ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = checkpoint,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
