package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun CustomFlowRow(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val horizontalSpacingPx = horizontalSpacing.roundToPx()
        val verticalSpacingPx = verticalSpacing.roundToPx()

        var currentRowWidth = 0
        var currentRowHeight = 0
        var totalHeight = 0
        var maxRowWidth = 0
        val rows = mutableListOf<List<Placeable>>()
        var currentRowPlaceables = mutableListOf<Placeable>()

        val placeables = measurables.map { measurable ->
            val placeable = measurable.measure(constraints.copy(minWidth = 0))
            if (currentRowWidth + placeable.width > constraints.maxWidth && currentRowPlaceables.isNotEmpty()) {
                rows.add(currentRowPlaceables)
                totalHeight += currentRowHeight + verticalSpacingPx
                maxRowWidth = max(maxRowWidth, currentRowWidth - horizontalSpacingPx)
                currentRowWidth = 0
                currentRowHeight = 0
                currentRowPlaceables = mutableListOf()
            }
            currentRowPlaceables.add(placeable)
            currentRowWidth += placeable.width + horizontalSpacingPx
            currentRowHeight = max(currentRowHeight, placeable.height)
            placeable
        }

        if (currentRowPlaceables.isNotEmpty()) {
            rows.add(currentRowPlaceables)
            totalHeight += currentRowHeight
            maxRowWidth = max(maxRowWidth, currentRowWidth - horizontalSpacingPx)
        }

        val finalWidth = if (constraints.hasBoundedWidth) constraints.maxWidth else maxRowWidth

        layout(finalWidth, totalHeight) {
            var y = 0
            rows.forEach { row ->
                var x = 0
                var rowMaxHeight = 0
                row.forEach { placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + horizontalSpacingPx
                    rowMaxHeight = max(rowMaxHeight, placeable.height)
                }
                y += rowMaxHeight + verticalSpacingPx
            }
        }
    }
}
