package com.clwater.compose_canvas.shape

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.dp
import androidx.core.graphics.flatten
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Cubic
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.star

@Composable
fun ShapeComposable() {
    val pathMeasurer = remember {
        PathMeasure()
    }
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val progress = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "progress"
    )
    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )
    val starPolygon = remember {
        RoundedPolygon.star(
            numVerticesPerRadius = 12,
            innerRadius = 2f / 3f,
            rounding = CornerRounding(1f / 6f)
        )
    }
    val circlePolygon = remember {
        RoundedPolygon.circle(
            numVertices = 12
        )
    }
    val morph = remember {
        Morph(starPolygon, circlePolygon)
    }
    var morphPath = remember {
        Path()
    }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .drawWithCache {
                morphPath = morph
                    .toComposePath(
                        progress = progress.value,
                        scale = size.minDimension / 2f,
                        path = morphPath
                    )
                val morphAndroidPath = morphPath.asAndroidPath()
                val flattenedStarPath = morphAndroidPath.flatten()

                pathMeasurer.setPath(morphPath, false)
                val totalLength = pathMeasurer.length

                onDrawBehind {

                    rotate(rotation.value) {
                        translate(size.width / 2f, size.height / 2f) {
                            val brush = Brush.sweepGradient(colors, center = Offset(0.5f, 0.5f))

                            val currentLength = totalLength * progress.value
                            flattenedStarPath.forEach { line ->
                                if (line.startFraction * totalLength < currentLength) {
                                    if (progress.value > line.endFraction) {
                                        drawLine(
                                            brush = brush,
                                            start = Offset(line.start.x, line.start.y),
                                            end = Offset(line.end.x, line.end.y),
                                            strokeWidth = 16.dp.toPx(),
                                            cap = StrokeCap.Round
                                        )
                                    } else {
                                        val endX = mapValue(
                                            progress.value,
                                            line.startFraction,
                                            line.endFraction,
                                            line.start.x,
                                            line.end.x
                                        )
                                        val endY = mapValue(
                                            progress.value,
                                            line.startFraction,
                                            line.endFraction,
                                            line.start.y,
                                            line.end.y
                                        )
                                        drawLine(
                                            brush =brush,
                                            start = Offset(line.start.x, line.start.y),
                                            end = Offset(endX, endY),
                                            strokeWidth = 16.dp.toPx(),
                                            cap = StrokeCap.Round
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .fillMaxSize()
    )
}

private val colors = listOf(
    Color(0xFF3FCEBC),
    Color(0xFF3CBCEB),
    Color(0xFF5F96E7),
    Color(0xFF816FE3),
    Color(0xFF9F5EE2),
    Color(0xFFBD4CE0),
    Color(0xFFDE589F),
    Color(0xFF3FCEBC),
)

private fun mapValue(
    value: Float,
    fromRangeStart: Float,
    fromRangeEnd: Float,
    toRangeStart: Float,
    toRangeEnd: Float
): Float {
    val ratio =
        (value - fromRangeStart) / (fromRangeEnd - fromRangeStart)
    return toRangeStart + ratio * (toRangeEnd - toRangeStart)
}

fun List<Cubic>.toPath(path: Path = Path(), scale: Float = 1f): Path {
    path.rewind()
    firstOrNull()?.let { first ->
        path.moveTo(first.anchor0X * scale, first.anchor0Y * scale)
    }
    for (bezier in this) {
        path.cubicTo(
            bezier.control0X * scale, bezier.control0Y * scale,
            bezier.control1X * scale, bezier.control1Y * scale,
            bezier.anchor1X * scale, bezier.anchor1Y * scale
        )
    }
    path.close()
    return path
}

fun Morph.toComposePath(progress: Float, scale: Float = 1f, path: Path = Path()): Path {
    var first = true
    path.rewind()
    forEachCubic(progress) { bezier ->
        if (first) {
            path.moveTo(bezier.anchor0X * scale, bezier.anchor0Y * scale)
            first = false
        }
        path.cubicTo(
            bezier.control0X * scale, bezier.control0Y * scale,
            bezier.control1X * scale, bezier.control1Y * scale,
            bezier.anchor1X * scale, bezier.anchor1Y * scale
        )
    }
    path.close()
    return path
}