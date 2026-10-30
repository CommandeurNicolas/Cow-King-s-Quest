package com.nicolascommandeur.cowkingsquest.view.screens.guide

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp

@Composable
internal fun GuideCompletionCrown() {
    val gold = MaterialTheme.colorScheme.primary
    val outline = MaterialTheme.colorScheme.background
    // Decorative: the progress bar already announces full completion to accessibility services.
    Canvas(Modifier
        .padding(end = 6.dp)
        .offset(y = 4.dp)
        .size(16.dp, 14.dp)) {
        val w = size.width
        val h = size.height
        // Front silhouette: three tall tips, stepped shoulders and a pointed lower band.
        val crown = Path().apply {
            moveTo(0f, 0f)
            lineTo(w * .16f, h * .47f)
            lineTo(w * .30f, h * .55f)
            lineTo(w * .37f, h * .72f)
            lineTo(w * .5f, 0f)
            lineTo(w * .63f, h * .72f)
            lineTo(w * .70f, h * .55f)
            lineTo(w * .84f, h * .47f)
            lineTo(w, 0f)
            lineTo(w * .94f, h * .78f)
            lineTo(w * .5f, h)
            lineTo(w * .06f, h * .78f)
            close()
        }
        // Paint the outline first, leaving a 1 dp background-colored edge around the gold.
        drawPath(crown, outline, style = Stroke(2.dp.toPx(), join = StrokeJoin.Round))
        drawPath(crown, gold)
    }
}

/** Fixed organic patches: the reward remains still when revisiting the guide. */
@Composable
internal fun CowPatternProgressBar(modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(6.dp)
    Canvas(
        modifier
            .fillMaxWidth()
            .height(12.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, shape)
            .clip(shape)
    ) {
        drawRect(Color.White)
        val tile = 96.dp.toPx()
        val h = size.height
        for (index in 0..(size.width / tile).toInt()) {
            translate(left = index * tile) {
                // Big spot
                drawPath(
                    Path().apply {
                        moveTo(0f, 0f)
                        lineTo(tile * .38f, 0f)
                        cubicTo(tile * .45f, h * .6f, tile * .25f, h * .3f, tile * .29f, h)
                        lineTo(tile * .06f, h)
                        cubicTo(tile * .14f, h * .45f, 0f, h * .7f, 0f, 0f)
                        close()
                    },
                    Color.Black,
                )
                // Small spotw
                drawPath(
                    Path().apply {
                        moveTo(tile * .65f, h)
                        cubicTo(tile * .49f, h * .5f, tile * .72f, -h * .2f, tile * .81f, h * .15f)
                        cubicTo(tile * .94f, h * .55f, tile * .77f, h * .6f, tile * .91f, h)
                        close()
                    },
                    Color.Black,
                )
            }
        }
    }
}

/** One diagonal translucent band behind the text, with transparent edges and no looping. */
internal fun Modifier.rainbowSweep(progress: () -> Float): Modifier = drawWithContent {
    val bandWidth = 180.dp.toPx()
    val tilt = 0.5f
    // Include the diagonal offset so the entire band enters and exits even on tall cards.
    val left = (size.width + size.height * tilt + bandWidth) * progress() - bandWidth
    val gradientWidth = bandWidth / (1f + tilt * tilt)
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color(0x40FF7575), Color(0x40FFBF69), Color(0x40FFE680),
                Color(0x407EE8A0), Color(0x407ABFFF), Color(0x40BF9AFF),
                Color.Transparent
            ),
            start = Offset(left, 0f),
            end = Offset(left + gradientWidth, gradientWidth * tilt)
        )
    )
    drawContent()
}
