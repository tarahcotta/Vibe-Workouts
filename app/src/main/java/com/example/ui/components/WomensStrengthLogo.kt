package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BoneDensityGold
import com.example.ui.theme.PostureTeal

/**
 * Custom vector logo matching the brand identity:
 * - Geometric Hexagon split vertically into Slate Teal (Left) & Copper Gold (Right)
 * - Inner 'A' / Diamond Frame & Stylized Woman Silhouette
 * - Typography: "WOMEN'S STRENGTH & LONGEVITY" & "AGE POWERFULLY & RECOVER"
 */
@Composable
fun WomensStrengthLogoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    slateColor: Color = PostureTeal,
    copperColor: Color = BoneDensityGold
) {
    Canvas(
        modifier = modifier
            .size(size)
            .testTag("womens_strength_logo_icon")
    ) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val strokeWidth = w * 0.065f

        // 1. Left Hexagon Half (Slate Teal)
        val leftHexPath = Path().apply {
            moveTo(cx, h * 0.02f)
            lineTo(w * 0.12f, h * 0.25f)
            lineTo(w * 0.12f, h * 0.75f)
            lineTo(cx, h * 0.98f)
            lineTo(cx, h * 0.82f)
            lineTo(w * 0.24f, h * 0.68f)
            lineTo(w * 0.24f, h * 0.32f)
            lineTo(cx, h * 0.18f)
            close()
        }

        // 2. Right Hexagon Half (Copper Gold)
        val rightHexPath = Path().apply {
            moveTo(cx, h * 0.02f)
            lineTo(w * 0.88f, h * 0.25f)
            lineTo(w * 0.88f, h * 0.75f)
            lineTo(cx, h * 0.98f)
            lineTo(cx, h * 0.82f)
            lineTo(w * 0.76f, h * 0.68f)
            lineTo(w * 0.76f, h * 0.32f)
            lineTo(cx, h * 0.18f)
            close()
        }

        drawPath(path = leftHexPath, color = slateColor)
        drawPath(path = rightHexPath, color = copperColor)

        // 3. Central Inner 'A' / Apex Frame - Left Side
        val leftAPath = Path().apply {
            moveTo(cx, h * 0.08f)
            lineTo(w * 0.28f, h * 0.52f)
            lineTo(w * 0.42f, h * 0.46f)
            lineTo(cx, h * 0.26f)
            close()
        }

        // 4. Central Inner 'A' / Apex Frame - Right Side
        val rightAPath = Path().apply {
            moveTo(cx, h * 0.08f)
            lineTo(w * 0.72f, h * 0.52f)
            lineTo(w * 0.58f, h * 0.46f)
            lineTo(cx, h * 0.26f)
            close()
        }

        drawPath(path = leftAPath, color = slateColor)
        drawPath(path = rightAPath, color = copperColor)

        // 5. Stylized Woman Silhouette (Profile looking up)
        // Face outline & hair curves
        val facePath = Path().apply {
            // Hair swoops
            moveTo(w * 0.40f, h * 0.42f)
            cubicTo(w * 0.42f, h * 0.33f, w * 0.48f, h * 0.31f, w * 0.52f, h * 0.31f)
            cubicTo(w * 0.57f, h * 0.31f, w * 0.60f, h * 0.36f, w * 0.59f, h * 0.41f)
            // Nose & chin profile
            lineTo(w * 0.61f, h * 0.42f)
            lineTo(w * 0.57f, h * 0.45f)
            lineTo(w * 0.58f, h * 0.48f)
            // Neck / jaw line back down
            cubicTo(w * 0.52f, h * 0.52f, w * 0.46f, h * 0.50f, w * 0.44f, h * 0.46f)
            close()
        }

        drawPath(path = facePath, color = slateColor)

        // Hair highlights (Copper Gold)
        val hairPath = Path().apply {
            moveTo(w * 0.38f, h * 0.44f)
            cubicTo(w * 0.42f, h * 0.35f, w * 0.46f, h * 0.33f, w * 0.50f, h * 0.33f)
            cubicTo(w * 0.46f, h * 0.38f, w * 0.44f, h * 0.42f, w * 0.40f, h * 0.48f)
            close()
        }
        drawPath(path = hairPath, color = copperColor)

        // 6. Bottom Diamond / Support Loop - Left
        val bottomLoopLeft = Path().apply {
            moveTo(cx, h * 0.90f)
            lineTo(w * 0.30f, h * 0.64f)
            lineTo(w * 0.38f, h * 0.58f)
            lineTo(cx, h * 0.76f)
            close()
        }

        // 7. Bottom Diamond / Support Loop - Right
        val bottomLoopRight = Path().apply {
            moveTo(cx, h * 0.90f)
            lineTo(w * 0.70f, h * 0.64f)
            lineTo(w * 0.62f, h * 0.58f)
            lineTo(cx, h * 0.76f)
            close()
        }

        drawPath(path = bottomLoopLeft, color = slateColor)
        drawPath(path = bottomLoopRight, color = copperColor)
    }
}

/**
 * Full Logo with Header Typography matching "WOMEN'S STRENGTH & LONGEVITY" & "AGE POWERFULLY & RECOVER".
 */
@Composable
fun WomensStrengthHeaderLogo(
    modifier: Modifier = Modifier,
    iconSize: Dp = 56.dp,
    showSubtitles: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WomensStrengthLogoIcon(size = iconSize)

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "WOMEN'S STRENGTH & LONGEVITY",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = PostureTeal,
            letterSpacing = 1.2.sp,
            textAlign = TextAlign.Center
        )

        if (showSubtitles) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "AGE POWERFULLY & RECOVER",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = BoneDensityGold,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
