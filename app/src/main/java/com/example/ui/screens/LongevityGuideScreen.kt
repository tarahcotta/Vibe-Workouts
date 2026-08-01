package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LongevityGuideScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Hero Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Longevity Science & Coaching Cues",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Evidence-based strength directives for midlife, bone mineral density, and lifelong vitality.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Pillar 1: Bone Mineral Density
        GuidePillarCard(
            title = "1. Bone Density & Axial Strain",
            subtitle = "Why light 'pink dumbbells' are insufficient for bone remodeling",
            icon = Icons.Default.Shield,
            tag = "Bone Density",
            content = "Bone remodeling (osteoblast stimulation) requires a mechanical strain threshold above daily activities (~1.5–3x bodyweight load or dynamic impact). Multi-joint axial loading (Squats, RDLs, Overhead Press) compresses long bones and vertebrae, signaling calcium deposition and bone mineral density preservation."
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Pillar 2: Sarcopenia Prevention
        GuidePillarCard(
            title = "2. Sarcopenia Prevention & Muscle Quality",
            subtitle = "Preserving Type II fast-twitch muscle fibers",
            icon = Icons.Default.FitnessCenter,
            tag = "Muscle Mass",
            content = "Age-related muscle loss (sarcopenia) accelerates in midlife, disproportionately targeting explosive Type II muscle fibers. Training compound movements at RPE 7-8 in 6-8 and 10-12 rep zones preserves motor units, insulin sensitivity, joint alignment, and metabolic capacity."
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Pillar 3: Fall Prevention & Single-Leg Stability
        GuidePillarCard(
            title = "3. Fall Prevention & Balance Capacity",
            subtitle = "Proprioception, hip stability & foot stiffness",
            icon = Icons.Default.AccessibilityNew,
            tag = "Balance",
            content = "Unilateral strength (Step-ups, Split Squats, Single-leg RDLs) trains pelvic alignment and lateral hip stabilizers (Gluteus Medius). Building foot stiffness and ankle proprioception mitigates fall risk and maintains gait confidence for long-term independence."
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Pillar 4: Grip Strength & Longevity Biomarker
        GuidePillarCard(
            title = "4. Grip Strength & Heavy Carries",
            subtitle = "Grip strength correlates strongly with all-cause survival",
            icon = Icons.Default.Speed,
            tag = "Grip & Core",
            content = "Grip strength serves as a primary clinical proxy for systemic muscle quality and neuromuscular integrity. Heavy carries (Farmer's Walks, Suitcase Holds) build dense forearm grip, scapular stability, and anti-lateral flexion core endurance."
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Pillar 5: RPE & Joint Safety Protocols
        GuidePillarCard(
            title = "5. RPE 7-8 & Joint Discomfort Scaling",
            subtitle = "How to train hard without triggering joint inflammation",
            icon = Icons.Default.Psychology,
            tag = "Joint Safety",
            content = "RPE (Rate of Perceived Exertion) ensures you leave 2-3 reps in reserve while maintaining sufficient load stimulus. If joint strain occurs (pain >3/10), scale range of motion (e.g. Box Squats), shift to neutral grip, or swap to chest-supported variations during deload weeks."
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun GuidePillarCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tag: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                GoalBadge(goal = tag)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }
    }
}
