package com.clwater.compose_canvas.shape

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Cubic
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.pill
import androidx.graphics.shapes.pillStar
import androidx.graphics.shapes.rectangle
import androidx.graphics.shapes.star
import com.clwater.compose_canvas.R
import com.clwater.compose_canvas.shape.tmp.toComposePath
import com.clwater.compose_canvas.ui.theme.AndroidComposeCanvasTheme

class ShapeActivity : ComponentActivity() {
    companion object {
        fun start(activity: ComponentActivity) {
            activity.startActivity(Intent(activity, ShapeActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidComposeCanvasTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        ShapeCustoms()
                    }
                }
            }
        }
    }

    @Composable
    fun ShapeCustoms() {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val itemSize: Dp = (screenWidth - 8.dp - 2.dp * 2 * 6) / 6f
        val boxSize: Dp = screenWidth / 6f

        val startPolygon = remember {
            mutableStateOf(RoundedPolygonModel())
        }

        val endPolygon = remember {
            mutableStateOf(RoundedPolygonModel(mutableStateOf(RoundedPolygonType.STAR)))
        }

        Column(modifier = Modifier.padding(4.dp)) {
            Box(
                modifier = Modifier
                    .drawWithCache {
                        onDrawBehind {
                            for (i in 0 until RoundedPolygonType.values().size) {
                                val shape = getRoundPolygon(
                                    RoundedPolygonType.values()[i],
                                    itemSize.toPx() / 2f,
                                    Offset(
                                        (boxSize.toPx() - itemSize.toPx() / 40f) * i + boxSize.toPx() / 2f,
                                        boxSize.toPx() / 2f
                                    )
                                )
                                val roundedPolygonPath = shape.cubics.toPath()
                                drawPath(roundedPolygonPath, color = colors[i])
                            }
                        }
                    }
                    .height(boxSize)
                    .fillMaxWidth()
            )
            Row(Modifier.padding(top = 4.dp, bottom = 4.dp)) {
                for (i in 0 until RoundedPolygonType.values().size) {
                    Text(
                        text = RoundedPolygonType.values()[i].getTypeName(), modifier = Modifier
                            .weight(1f), fontSize = 12.sp, textAlign = TextAlign.Center
                    )
                }
            }

            RoundedPolygonAdjust(startPolygon, screenWidth / 3f + 8.dp, colors[6])
            RoundedPolygonAdjust(endPolygon, screenWidth / 3f + 8.dp, colors[7])
            AnimationRoundPolygon(startPolygon, endPolygon)
        }

    }

    @Composable
    fun AnimationRoundPolygon(
        startPolygon: MutableState<RoundedPolygonModel>,
        endPolygon: MutableState<RoundedPolygonModel>,
    ) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val shapeA = remember {
            mutableStateOf(
                getRoundPolygonAnimation(startPolygon.value)
            )

        }
        val shapeB = remember {
            mutableStateOf(
                getRoundPolygonAnimation(endPolygon.value)
            )

        }

        val morph = remember {
            Morph(shapeA.value, shapeB.value)
        }
        val infiniteTransition = rememberInfiniteTransition("infinite outline movement")
        val animatedProgress = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "animatedMorphProgress"
        )
        val animatedRotation = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                tween(6000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "animatedMorphProgress"
        )
        Box(
            modifier = Modifier.fillMaxWidth().height(screenWidth / 3f),
        ) {
            Image(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(
                        CustomRotatingMorphShape(
                            morph,
                            animatedProgress.value,
                            animatedRotation.value
                        )
                    )
                    .size(screenWidth / 3f)
            )
        }
    }

    @Composable
    fun RoundedPolygonAdjust(
        startPolygon: MutableState<RoundedPolygonModel>,
        height: Dp,
        color: Color

    ) {
        Box(
            modifier = Modifier
                .height(height)
                .padding(vertical = 4.dp)
                .drawWithCache {
                    val roundedPolygon = getRoundPolygon(
                        startPolygon.value,
                        size.width / 6f
                    )
                    val roundedPolygonPath = roundedPolygon.cubics
                        .toPath()
                    onDrawBehind {
                        drawPath(roundedPolygonPath, color = color)
                    }
                }
                .fillMaxWidth()
        )
    }

    fun getRoundPolygonAnimation(model: RoundedPolygonModel): RoundedPolygon{
        return when (model.type.value) {
            RoundedPolygonType.Common -> RoundedPolygon(
                numVertices = 12,
                rounding = CornerRounding(0.2f),
                )

            RoundedPolygonType.CIRCLE -> RoundedPolygon.circle(
                numVertices = 5,
            )

            RoundedPolygonType.PILL -> RoundedPolygon.pill(
//                width = radius,
//                height = radius / 2,
                smoothing = 100f,
//                centerX = offset.x,
//                centerY = offset.y
            )

            RoundedPolygonType.PILL_STAR -> RoundedPolygon.pillStar(
//                width = radius,
//                height = radius / 2,
                numVerticesPerRadius = 8,
//                centerX = offset.x,
//                centerY = offset.y
            )

            RoundedPolygonType.RECTANGLE -> RoundedPolygon.rectangle(
//                width = radius,
//                height = radius / 2,
//                centerX = offset.x,
//                centerY = offset.y
            )

            RoundedPolygonType.STAR -> RoundedPolygon.star(
                numVerticesPerRadius = 6,
//                radius = radius,
//                innerRadius = radius / 2,
//                centerX = offset.x,
//                centerY = offset.y,
            )

        }
    }

    fun getRoundPolygon(model: RoundedPolygonModel, radius: Float): RoundedPolygon {
        return getRoundPolygon(model.type.value, radius,Offset(radius, radius))
    }

    fun getRoundPolygon(type: RoundedPolygonType, radius: Float, offset: Offset): RoundedPolygon {
        return when (type) {
            RoundedPolygonType.Common -> RoundedPolygon(
                numVertices = 12,
                rounding = CornerRounding(0.2f),
                radius = radius,
                centerX = offset.x,
                centerY = offset.y,

            )

            RoundedPolygonType.CIRCLE -> RoundedPolygon.circle(
                numVertices = 5,
                radius = radius,
                centerX = offset.x,
                centerY = offset.y
            )

            RoundedPolygonType.PILL -> RoundedPolygon.pill(
                width = radius,
                height = radius / 2,
                smoothing = 100f,
                centerX = offset.x,
                centerY = offset.y
            )

            RoundedPolygonType.PILL_STAR -> RoundedPolygon.pillStar(
                width = radius,
                height = radius / 2,
                numVerticesPerRadius = 8,
                centerX = offset.x,
                centerY = offset.y
            )

            RoundedPolygonType.RECTANGLE -> RoundedPolygon.rectangle(
                width = radius,
                height = radius / 2,
                centerX = offset.x,
                centerY = offset.y
            )

            RoundedPolygonType.STAR -> RoundedPolygon.star(
                numVerticesPerRadius = 6,
                radius = radius,
                innerRadius = radius / 2,
                centerX = offset.x,
                centerY = offset.y,
            )

        }
    }

    private val colors = listOf(
        Color(0xFF3FCEBC),
        Color(0xFF3CBCEB),
        Color(0xFF5F96E7),
        Color(0xFF816FE3),
        Color(0xFF9F5EE2),
        Color(0xFFBD4CE0),
        Color(0xFFDE589F),
        Color(0xFFCDDC39),
        Color(0xFFFF5722),
    )

    data class RoundedPolygonModel(var type: MutableState<RoundedPolygonType> = mutableStateOf(RoundedPolygonType.Common))

    enum class RoundedPolygonType(private val typeName: String) {
        Common("Common"),
        CIRCLE("Circle"),
        PILL("Pill"),
        PILL_STAR("PillStar"),
        RECTANGLE("Rectangle"),
        STAR("Star"),
        ;

        fun getTypeName() = typeName
    }


    class CustomRotatingMorphShape(
        private val morph: Morph,
        private val percentage: Float,
        private val rotation: Float
    ) : Shape {

        private val matrix = Matrix()
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            // Below assumes that you haven't changed the default radius of 1f, nor the centerX and centerY of 0f
            // By default this stretches the path to the size of the container, if you don't want stretching, use the same size.width for both x and y.
            matrix.scale(size.width / 2f, size.height / 2f)
            matrix.translate(1f, 1f)
            matrix.rotateZ(rotation)

            val path = morph.toComposePath(progress = percentage)
            path.transform(matrix)

            return Outline.Generic(path)
        }
    }


    @Composable
    fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }


    @Composable
    fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

    fun List<Cubic>.toPath(path: Path = Path(), scale: Float = 1f): Path {
        path.rewind()
        firstOrNull()?.let { first ->
            path.moveTo(first.anchor0X * scale, first.anchor0Y * scale)
        }
        for (bezier in this) {
            path.cubicTo(
                bezier.control0X * scale,
                bezier.control0Y * scale,
                bezier.control1X * scale,
                bezier.control1Y * scale,
                bezier.anchor1X * scale,
                bezier.anchor1Y * scale
            )
        }
        path.close()
        return path
    }
}