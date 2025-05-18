package com.example.medicina.components

import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.ConstraintLayoutBaseScope.VerticalAnchor

object LayoutGuidelines {
    const val y = 0.02f
    val x = (1f - (y * 3)) / 4

    fun ConstraintLayoutScope.setupColumnGuidelines(): ColumnGuidelines {
        val c1start = createGuidelineFromStart(0f)
        val c1end = createGuidelineFromStart(x)

        val c2start = createGuidelineFromStart(x + y)
        val c2end = createGuidelineFromStart((2 * x) + y)

        val c3start = createGuidelineFromStart(2 * (x + y))
        val c3end = createGuidelineFromStart((3 * x) + (2 * y))

        val c4start = createGuidelineFromStart(3 * (x + y))
        val c4end = createGuidelineFromStart(1f)

        return ColumnGuidelines(
            c1start, c1end,
            c2start, c2end,
            c3start, c3end,
            c4start, c4end
        )
    }

    data class ColumnGuidelines(
        val c1start: VerticalAnchor,
        val c1end: VerticalAnchor,
        val c2start: VerticalAnchor,
        val c2end: VerticalAnchor,
        val c3start: VerticalAnchor,
        val c3end: VerticalAnchor,
        val c4start: VerticalAnchor,
        val c4end: VerticalAnchor
    )
}
