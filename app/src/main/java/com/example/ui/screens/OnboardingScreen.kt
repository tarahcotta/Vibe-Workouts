package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WomensStrengthLogoIcon
import com.example.ui.theme.RainbowBrush
import kotlinx.coroutines.launch

data class OnboardingStepData(
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: ImageVector,
    val primaryColor: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onCompleteOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = remember {
        listOf(
            OnboardingStepData(
                title = "Strength Built for Life & Longevity",
                subtitle = "Empowering women across all life stages through science-backed resistance training tailored for bone mineral density, joint resilience, and metabolic health.",
                badge = "WELCOME TO VITAL STRENGTH",
                icon = Icons.Default.FitnessCenter,
                primaryColor = Color(0xFF4FC3F7)
            ),
            OnboardingStepData(
                title = "Progressive Overload & Bone Density",
                subtitle = "Bones are dynamic living tissue that respond directly to mechanical stress. Gradual weight increases signal osteoblasts to deposit calcium and strengthen bone architecture.",
                badge = "BONE HEALTH SCIENCE",
                icon = Icons.Default.Shield,
                primaryColor = Color(0xFFFFB74D)
            ),
            OnboardingStepData(
                title = "Joint Stability & Cartilage Health",
                subtitle = "Progressive resistance fortifies tendons, ligaments, and surrounding musculature while circulating nourishing synovial fluid to keep joints pain-free and resilient.",
                badge = "JOINT & CONNECTIVE TISSUE",
                icon = Icons.Default.HealthAndSafety,
                primaryColor = Color(0xFF81C784)
            ),
            OnboardingStepData(
                title = "Smart\nMicro-Progressions",
                subtitle = "Our live workout engine tracks your volume and guides safe, incremental gains (+2.5 lbs or +1 rep) so you build strength consistently without injury or burnout.",
                badge = "INTELLIGENT PROGRESSION",
                icon = Icons.Default.TrendingUp,
                primaryColor = Color(0xFFB39DDB)
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { steps.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WomensStrengthLogoIcon(size = 28.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Vital Strength",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (pagerState.currentPage < steps.size - 1) {
                    TextButton(
                        onClick = onCompleteOnboarding,
                        modifier = Modifier.testTag("onboarding_skip_button")
                    ) {
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Page indicator dots
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .testTag("onboarding_page_indicator")
                    ) {
                        repeat(steps.size) { pageIndex ->
                            val isSelected = pagerState.currentPage == pageIndex
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .height(8.dp)
                                    .width(if (isSelected) 28.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outlineVariant
                                    )
                            )
                        }
                    }

                    // Navigation buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (pagerState.currentPage > 0) {
                            OutlinedButton(
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("onboarding_prev_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous"
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Back")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                        }

                        Button(
                            onClick = {
                                if (pagerState.currentPage < steps.size - 1) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                } else {
                                    onCompleteOnboarding()
                                }
                            },
                            modifier = Modifier
                                .weight(if (pagerState.currentPage > 0) 1.5f else 1f)
                                .height(52.dp)
                                .testTag("onboarding_next_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = if (pagerState.currentPage == steps.size - 1) "Get Started" else "Next",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = if (pagerState.currentPage == steps.size - 1) Icons.Default.CheckCircle else Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Icon"
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { pageIndex ->
            OnboardingPageContent(
                step = steps[pageIndex],
                pageIndex = pageIndex
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(
    step: OnboardingStepData,
    pageIndex: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .testTag("onboarding_step_$pageIndex"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Badge pill
        Surface(
            color = step.primaryColor.copy(alpha = 0.12f),
            shape = CircleShape,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = step.badge,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = step.primaryColor,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Title
        Text(
            text = step.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Subtitle
        Text(
            text = step.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Interactive or Visual Content Card according to step
        when (pageIndex) {
            0 -> WelcomeStepVisualCard(step = step)
            1 -> BoneDensityInteractiveCard()
            2 -> JointHealthVisualCard()
            3 -> MicroProgressionInteractiveCard()
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun WelcomeStepVisualCard(step: OnboardingStepData) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(step.primaryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = step.icon,
                    contentDescription = "Icon",
                    tint = step.primaryColor,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Key Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatPill(
                    value = "+1.5%",
                    label = "Annual Bone Mass Gain",
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    value = "3x",
                    label = "Lower Fracture Risk",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Designed specifically for women's physiological needs, including hormonal shifts, bone density preservation, and functional longevity.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun BoneDensityInteractiveCard() {
    var loadLevel by remember { mutableFloatStateOf(2f) } // 0: Light, 1: Moderate, 2: Progressive Overload

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Interactive Bone Remodeling Simulator",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Adjust the mechanical load slider to see how bone osteoblasts respond:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Load Level Indicator Box
            val (statusTitle, statusDesc, densityPercent, color) = when (loadLevel.toInt()) {
                0 -> Quadruple("Sedentary / Light Walk", "Inadequate strain to trigger osteogenesis. Bone mass remains static or slowly degrades.", "75% Baseline Density", Color(0xFFE57373))
                1 -> Quadruple("Moderate Resistance", "Mild stimulus. Maintains current bone mineral density but limits growth.", "90% Density Maintenance", Color(0xFFFFB74D))
                else -> Quadruple("Progressive Overload", "Optimal mechanical strain! Triggers osteoblast bone synthesis and trabecular thickening.", "+115% Enhanced Density", Color(0xFF81C784))
            }

            Surface(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = statusTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                        Text(
                            text = densityPercent,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = statusDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Slider
            Text(
                text = "Mechanical Strain Level",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Slider(
                value = loadLevel,
                onValueChange = { loadLevel = it },
                valueRange = 0f..2f,
                steps = 1,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                ),
                modifier = Modifier.testTag("onboarding_bone_density_slider")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Low Strain", style = MaterialTheme.typography.labelSmall)
                Text("Moderate", style = MaterialTheme.typography.labelSmall)
                Text("Optimal Overload", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
            }
        }
    }
}

@Composable
private fun JointHealthVisualCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Connective Tissue Resilience",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF006C4C)
            )

            Spacer(modifier = Modifier.height(12.dp))

            JointBenefitItem(
                icon = Icons.Default.HealthAndSafety,
                title = "Synovial Fluid Circulation",
                description = "Weight-bearing movement pumps nutrient-rich fluid directly into cartilage, reducing stiffness."
            )

            Spacer(modifier = Modifier.height(12.dp))

            JointBenefitItem(
                icon = Icons.Default.Shield,
                title = "Tendon Collagen Thickening",
                description = "Consistent loading increases collagen fiber cross-linking, making tendons resilient against sprains."
            )

            Spacer(modifier = Modifier.height(12.dp))

            JointBenefitItem(
                icon = Icons.Default.Speed,
                title = "Postural & Spinal Alignment",
                description = "Strengthening the posterior chain supports vertebral disc pressure and protects lower back health."
            )
        }
    }
}

@Composable
private fun JointBenefitItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF006C4C).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Icon",
                    tint = Color(0xFF006C4C),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MicroProgressionInteractiveCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Live Micro-Progression Preview",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mock Progressive Overload Card
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Goblet Squat (Bone Density Focus)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFF81C784).copy(alpha = 0.12f),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "READY FOR OVERLOAD",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF81C784),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Previous Target:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("35 lbs × 10 reps", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Icon",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.2f)) {
                            Text("Recommended Today:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF81C784))
                            Text("37.5 lbs × 10 reps (+2.5 lbs)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Small, safe increases add up to massive longevity gains over 6–12 months without risking joint inflammation or overtraining.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatPill(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
