package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ExerciseAlternativeOption(
    val exerciseName: String,
    val equipment: String,
    val spineSafetyRating: String, // "Spine-Sparing", "Low Shear", "Supported", "Standard Axial"
    val clinicalBenefit: String,
    val isSpineSparing: Boolean = false
)

object ExerciseSubstitutionRepository {
    private val substitutionMap = mapOf(
        "Barbell Back Squat" to listOf(
            ExerciseAlternativeOption(
                exerciseName = "Goblet Squat (Dumbbell/Kettlebell)",
                equipment = "Dumbbell",
                spineSafetyRating = "Spine-Sparing",
                clinicalBenefit = "Keeps torso upright, eliminating lumbar forward shear while providing femur bone loading.",
                isSpineSparing = true
            ),
            ExerciseAlternativeOption(
                exerciseName = "Seated Leg Press / Machine Squat",
                equipment = "Machine",
                spineSafetyRating = "Lumbar Supported",
                clinicalBenefit = "Back rest supports spine fully, ideal for acute lumbar sensitivity.",
                isSpineSparing = true
            ),
            ExerciseAlternativeOption(
                exerciseName = "Dumbbell Box Squat",
                equipment = "Dumbbells",
                spineSafetyRating = "Controlled Depth",
                clinicalBenefit = "Limits range of motion to pain-free depth and eliminates bouncy joint rebound.",
                isSpineSparing = true
            )
        ),
        "Romanian Deadlift (RDL)" to listOf(
            ExerciseAlternativeOption(
                exerciseName = "Dumbbell Romanian Deadlift",
                equipment = "Dumbbells",
                spineSafetyRating = "Low Lumbar Shear",
                clinicalBenefit = "Weights travel along the sides of legs, allowing neutral spine centering.",
                isSpineSparing = true
            ),
            ExerciseAlternativeOption(
                exerciseName = "Trap Bar Deadlift (High Handles)",
                equipment = "Trap Bar",
                spineSafetyRating = "Spine-Sparing Gold Standard",
                clinicalBenefit = "Centers load directly through the body's center of gravity with reduced lower back moment arm.",
                isSpineSparing = true
            ),
            ExerciseAlternativeOption(
                exerciseName = "Barbell / Dumbbell Glute Bridge",
                equipment = "Floor / Bench",
                spineSafetyRating = "Zero Axial Load",
                clinicalBenefit = "Zero spinal compressive force while delivering maximum pelvic and glute bone density.",
                isSpineSparing = true
            )
        ),
        "Overhead Press" to listOf(
            ExerciseAlternativeOption(
                exerciseName = "Seated Dumbbell Shoulder Press",
                equipment = "Dumbbells",
                spineSafetyRating = "Back Supported",
                clinicalBenefit = "Bench backrest prevents lumbar hyperextension under overhead loads.",
                isSpineSparing = true
            ),
            ExerciseAlternativeOption(
                exerciseName = "Incline Dumbbell Chest Press",
                equipment = "Dumbbells",
                spineSafetyRating = "Spine-Neutral",
                clinicalBenefit = "Distributes load across upper chest, shoulders, and triceps with stable bench support.",
                isSpineSparing = true
            ),
            ExerciseAlternativeOption(
                exerciseName = "Landmine Shoulder Press",
                equipment = "Landmine / Barbell",
                spineSafetyRating = "Natural Arc",
                clinicalBenefit = "Diagonal press trajectory avoids impinging tight shoulders or compressing cervical spine.",
                isSpineSparing = true
            )
        ),
        "Barbell Bent-Over Row" to listOf(
            ExerciseAlternativeOption(
                exerciseName = "Chest-Supported Incline Row",
                equipment = "Dumbbells & Bench",
                spineSafetyRating = "Zero Lumbar Load",
                clinicalBenefit = "Chest rests on 45° bench, completely unloading lower back erectors.",
                isSpineSparing = true
            ),
            ExerciseAlternativeOption(
                exerciseName = "Seated Cable Row",
                equipment = "Cable Machine",
                spineSafetyRating = "Controlled Ergonomics",
                clinicalBenefit = "Constant tension for postural scapular retractors without hip hinge fatigue.",
                isSpineSparing = true
            ),
            ExerciseAlternativeOption(
                exerciseName = "Single-Arm Dumbbell Row",
                equipment = "Dumbbell & Bench",
                spineSafetyRating = "3-Point Stability",
                clinicalBenefit = "Hand and knee on bench provide a stable tripod base.",
                isSpineSparing = true
            )
        ),
        "Walking Lunges" to listOf(
            ExerciseAlternativeOption(
                exerciseName = "Dumbbell Step-Ups",
                equipment = "Dumbbells & Box",
                spineSafetyRating = "Low Knee Shear",
                clinicalBenefit = "Reduces deceleration impact while preserving unilateral hip and femur bone density.",
                isSpineSparing = true
            ),
            ExerciseAlternativeOption(
                exerciseName = "Static Split Squat with Hand Support",
                equipment = "Bodyweight / Dumbbells",
                spineSafetyRating = "High Balance Safety",
                clinicalBenefit = "Non-moving feet eliminate dynamic tripping risk while training hip stability.",
                isSpineSparing = true
            )
        )
    )

    fun getAlternativesFor(exerciseName: String): List<ExerciseAlternativeOption> {
        val matches = substitutionMap.entries.firstOrNull { exerciseName.contains(it.key, ignoreCase = true) }
        return matches?.value ?: listOf(
            ExerciseAlternativeOption(
                exerciseName = "Dumbbell Alternative to $exerciseName",
                equipment = "Dumbbells",
                spineSafetyRating = "Spine-Sparing",
                clinicalBenefit = "Provides independent arm/leg freedom and reduces spinal compression.",
                isSpineSparing = true
            ),
            ExerciseAlternativeOption(
                exerciseName = "Machine / Cable Alternative",
                equipment = "Machine",
                spineSafetyRating = "Supported Movement",
                clinicalBenefit = "Guided trajectory protects joints from balance slips.",
                isSpineSparing = true
            )
        )
    }
}

/**
 * 1-Tap Exercise Substitution Dialog for Active Workout Mode
 */
@Composable
fun ExerciseSubstitutionDialog(
    currentExerciseName: String,
    onSelectAlternative: (newExerciseName: String) -> Unit,
    onDismiss: () -> Unit
) {
    val alternatives = remember(currentExerciseName) {
        ExerciseSubstitutionRepository.getAlternativesFor(currentExerciseName)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Swap Exercise / Spine-Safe",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Current: $currentExerciseName",
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Need a joint-friendly, spine-sparing variation or have different equipment available? Choose a safe alternative below:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                alternatives.forEach { alt ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectAlternative(alt.exerciseName)
                                onDismiss()
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = alt.exerciseName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (alt.isSpineSparing) Color(0xFF00E676).copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = if (alt.isSpineSparing) Color(0xFF00C853) else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = alt.spineSafetyRating,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = if (alt.isSpineSparing) Color(0xFF00796B) else MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Equipment: ${alt.equipment}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = alt.clinicalBenefit,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Keep Current Exercise")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
