package com.kampplus.hava.core.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.kampplus.hava.core.ui.theme.HavaColors

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(28.dp),
    contentPadding: PaddingValues = PaddingValues(20.dp),
    emphasized: Boolean = false,
    content: @Composable () -> Unit
) {
    val topColor = if (emphasized) HavaColors.GlassStrong else HavaColors.Glass

    Box(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = HavaColors.GlassShadow,
                spotColor = HavaColors.GlassShadow
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        topColor,
                        HavaColors.Glass.copy(alpha = 0.09f)
                    )
                )
            )
            .border(
                BorderStroke(
                    width = 1.dp,
                    color = HavaColors.GlassBorder
                ),
                shape = shape
            )
            .padding(contentPadding)
    ) {
        content()
    }
}
