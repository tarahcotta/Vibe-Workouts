package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Portrait Style Reference Tokens
val PortraitInk = Color(0xFF08304C)
val NauticalTeal = Color(0xFF084E72)
val WhiteCanvas = Color(0xFFFFFFFF)
val SkyWash = Color(0xFFE8F1FF)
val MintWash = Color(0xFFD7FFE2)
val PeachWash = Color(0xFFFFEBD6)
val CharcoalOutline = Color(0xFF353535)
val GraphiteBody = Color(0xFF2C2C2C)
val SlateHelper = Color(0xFF797979)
val IronQuiet = Color(0xFF585858)
val AshDivider = Color(0xFFDEDEDE)
val FogEdge = Color(0xFFC7C7C7)
val MistHairline = Color(0xFFEEEEEE)

// Rainbow Spectrum Gradient Stops
val RainbowBlue = Color(0xFF26C0FF)
val RainbowMagenta = Color(0xFFE600C2)
val RainbowRed = Color(0xFFFF4940)
val RainbowOrange = Color(0xFFFFA130)
val RainbowYellow = Color(0xFFFFC837)
val RainbowGreen = Color(0xFF00CC3D)

val RainbowBrush = Brush.horizontalGradient(
    colors = listOf(
        RainbowBlue,
        RainbowMagenta,
        RainbowRed,
        RainbowOrange,
        RainbowYellow,
        RainbowGreen
    )
)

// Light Color Scheme mapped to Portrait Style
val GeoPrimaryLight = PortraitInk
val GeoOnPrimaryLight = WhiteCanvas
val GeoPrimaryContainerLight = SkyWash
val GeoOnPrimaryContainerLight = PortraitInk

val GeoSecondaryLight = NauticalTeal
val GeoOnSecondaryLight = WhiteCanvas
val GeoSecondaryContainerLight = MintWash
val GeoOnSecondaryContainerLight = Color(0xFF004D1A)

val GeoTertiaryLight = Color(0xFFC45A00)
val GeoOnTertiaryLight = WhiteCanvas
val GeoTertiaryContainerLight = PeachWash
val GeoOnTertiaryContainerLight = Color(0xFF4A1A00)

val GeoBackgroundLight = WhiteCanvas
val GeoOnBackgroundLight = PortraitInk
val GeoSurfaceLight = WhiteCanvas
val GeoOnSurfaceLight = PortraitInk
val GeoSurfaceVariantLight = SkyWash
val GeoOnSurfaceVariantLight = SlateHelper
val GeoOutlineLight = AshDivider

// Dark Palette
val GeoPrimaryDark = Color(0xFF8EACB8)
val GeoOnPrimaryDark = Color(0xFF10242E)
val GeoPrimaryContainerDark = Color(0xFF1B3340)
val GeoOnPrimaryContainerDark = Color(0xFFD8E4EC)

val GeoSecondaryDark = Color(0xFFE2BC89)
val GeoOnSecondaryDark = Color(0xFF402C12)
val GeoSecondaryContainerDark = Color(0xFF5A401D)
val GeoOnSecondaryContainerDark = Color(0xFFF7EAD7)

val GeoTertiaryDark = Color(0xFFE2C49B)
val GeoOnTertiaryDark = Color(0xFF422B0C)
val GeoTertiaryContainerDark = Color(0xFF5D3F16)
val GeoOnTertiaryContainerDark = Color(0xFFFAF0E3)

val GeoBackgroundDark = Color(0xFF0B1620)
val GeoOnBackgroundDark = Color(0xFFE2EAEE)
val GeoSurfaceDark = Color(0xFF101E28)
val GeoOnSurfaceDark = Color(0xFFE2EAEE)
val GeoSurfaceVariantDark = Color(0xFF182936)
val GeoOnSurfaceVariantDark = Color(0xFFA1B3BE)

// Functional Goal Accent Colors
val BoneDensityGold = Color(0xFFFFA130)
val PostureTeal = NauticalTeal
val JointSafetyCoral = Color(0xFFFF4940)
val BalanceIndigo = PortraitInk
val EmeraldTertiaryLight = Color(0xFF00CC3D)




