package com.nicolascommandeur.diablo4cowcompanion

import android.animation.ValueAnimator
import android.os.SystemClock
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt

@Composable
internal fun CowVisitor(
    session: CowVisitorSession?,
    splashFinished: Boolean,
    countBounds: Rect? = null
) {
    if (session == null || !session.lucky) return

    val view = LocalView.current
    var visit by remember(session) { mutableStateOf(session.advance(0)) }
    var animate by remember { mutableStateOf(ValueAnimator.areAnimatorsEnabled()) }

    LaunchedEffect(session, splashFinished, view) {
        if (!splashFinished) return@LaunchedEffect

        val lifecycle = view.findViewTreeLifecycleOwner()?.lifecycle ?: return@LaunchedEffect

        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            // Reset the clock on resume: time in the background never advances a visit.
            var previous = SystemClock.uptimeMillis()
            while (isActive) {
                val now = SystemClock.uptimeMillis()
                visit = session.advance(elapsedMs = now - previous)
                previous = now
                animate = ValueAnimator.areAnimatorsEnabled()
                delay(
                    timeMillis = if (visit == null) 200L
                    else if (animate) 16L
                    else 100L
                )
            }
        }
    }

    if (splashFinished) visit?.let { current ->
        CowVisitorStage(
            current,
            animate,
            countBounds,
            onTap = { visit = session.tap() }
        )
    }
}

/** Transparent overlay clipped at the navigation edge; only the cow handles touches. */
@Composable
internal fun CowVisitorStage(
    visit: CowVisit,
    animate: Boolean,
    countBounds: Rect? = null,
    onTap: () -> Unit
) {
    val sprites = ImageBitmap.imageResource(R.drawable.hell_bovine_walk)
    val density = LocalDensity.current
    var origin by remember { mutableStateOf(Offset.Zero) }
    var bubbleSize by remember { mutableStateOf(IntSize.Zero) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .testTag("cow_visitor_stage")
            .onGloballyPositioned { origin = it.boundsInRoot().topLeft }
    ) {
        val width = with(density) { maxWidth.toPx() }
        val height = with(density) { maxHeight.toPx() }

        val spriteWidth = with(density) { 96.dp.toPx() }
        val spriteHeight = with(density) { 92.dp.toPx() }

        val margin = with(density) { 12.dp.toPx() }

        val startX = (width - spriteWidth) / 2f

        val target = countBounds?.translate(-origin)?.takeIf {
            it.width > 0 && it.height > 0 && it.top >= 0 && it.bottom <= height
        }

        val besideCount = target != null && target.right + margin + spriteWidth <= width - margin

        val targetX = when {
            target == null -> startX
            besideCount -> target.right + margin
            else -> target.center.x - spriteWidth / 2f
        }.coerceIn(0f, (width - spriteWidth).coerceAtLeast(0f))
        val targetY = (when {
            target == null -> height * .45f - spriteHeight / 2f
            besideCount -> target.center.y - spriteHeight / 2f
            else -> target.top - spriteHeight * .75f
        }).coerceIn(0f, (height - spriteHeight).coerceAtLeast(0f))

        val peekY = height - spriteHeight * .48f * visit.peekFraction

        val x = if (!animate) (width - spriteWidth - margin).coerceAtLeast(0f)
        else if (visit.leaving) targetX + (width - targetX) * visit.exitFraction
        else startX + (targetX - startX) * visit.enterFraction

        val y = if (!animate) (height - spriteHeight - margin).coerceAtLeast(0f)
        else peekY + (targetY - peekY) * visit.enterFraction

        Canvas(
            modifier = Modifier
                .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                .size(96.dp, 92.dp)
                .testTag("hell_bovine")
                .semantics { contentDescription = "Hell Bovine" }
                .clickable(
                    role = Role.Button,
                    onClickLabel = "Talk to the cow",
                    onClick = onTap
                )
        ) {
            drawImage(
                sprites,
                srcOffset = IntOffset(
                    x = (if (animate) visit.frame else 0) * 157,
                    y = when {
                        !animate -> 0
                        visit.leaving -> 151
                        visit.entering -> 302 // Walk away from the viewer toward the count.
                        else -> 0
                    }
                ),
                srcSize = IntSize(width = 157, height = 151),
                dstSize = IntSize(width = size.width.toInt(), height = size.height.toInt()),
                filterQuality = FilterQuality.None
            )
        }
        if (!animate || visit.showBubble) {
            val bubbleX = (x + spriteWidth / 2f - bubbleSize.width / 2f)
                .coerceIn(margin, (width - bubbleSize.width - margin).coerceAtLeast(margin))

            // The transparent top padding of the original sprite leaves room for the tail.
            val bubbleY = (y + spriteHeight * .16f - bubbleSize.height).coerceAtLeast(0f)

            Text(
                visit.line,
                color = Color(0xFF211B16),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset { IntOffset(x = bubbleX.roundToInt(), y = bubbleY.roundToInt()) }
                    .widthIn(max = 216.dp)
                    .onSizeChanged { bubbleSize = it }
                    .drawBehind {
                        val stroke = 1.5.dp.toPx()
                        val inset = stroke / 2
                        val left = inset
                        val top = inset
                        val right = size.width - inset
                        val bottom = size.height - 9.dp.toPx() - inset
                        val radius = minOf(15.dp.toPx(), (right - left) / 2, (bottom - top) / 2)
                        val tailHalfWidth = minOf(7.dp.toPx(), (right - left - 2 * radius) / 2)
                        // Keep the entire tail base on the straight part of the bottom edge.
                        val tipX = (x + spriteWidth / 2f - bubbleX).coerceIn(
                            left + radius + tailHalfWidth,
                            right - radius - tailHalfWidth
                        )
                        // One perimeter means there is no interior border or anti-alias seam.
                        val bubble = Path().apply {
                            moveTo(left + radius, top)
                            lineTo(right - radius, top)
                            quadraticTo(right, top, right, top + radius)
                            lineTo(right, bottom - radius)
                            quadraticTo(right, bottom, right - radius, bottom)
                            lineTo(tipX + tailHalfWidth, bottom)
                            lineTo(tipX, size.height - inset)
                            lineTo(tipX - tailHalfWidth, bottom)
                            lineTo(left + radius, bottom)
                            quadraticTo(left, bottom, left, bottom - radius)
                            lineTo(left, top + radius)
                            quadraticTo(left, top, left + radius, top)
                            close()
                        }
                        drawPath(bubble, Color.White)
                        drawPath(
                            bubble,
                            Color(0xFF211B16),
                            style = Stroke(width = stroke, join = StrokeJoin.Round)
                        )
                    }
                    .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 19.dp)
            )
        }
    }
}
