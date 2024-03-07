package com.clwater.compose_canvas.shape

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
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

        Column(modifier = Modifier.padding(4.dp)) {
            Box(
                modifier = Modifier
                    .drawWithCache {
                        onDrawBehind {
                            for (i in 0 until RoundedPolygonType.values().size) {
                                val shape = RoundedPolygonType.values()[i]
                                    .getRoundPolygon(
                                        itemSize.toPx() / 2f, boxSize.toPx(),
                                        Offset(
                                            (boxSize.toPx() - itemSize.toPx() / 40f) * i,
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
                for (i in 0 until  RoundedPolygonType.values().size) {
                    Text(text = RoundedPolygonType.values()[i].getTypeName(), modifier = Modifier
                        .weight(1f), fontSize = 12.sp, textAlign = TextAlign.Center)
                }
            }
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
        Color(0xFF3FCEBC),
    )

    enum class RoundedPolygonType(private val typeName: String) {
        Common("Common"),
        CIRCLE("Circle"),
        PILL("Pill"),
        PILL_STAR("PillStar"),
        RECTANGLE("Rectangle"),
        STAR("Star"),
        ;
        fun getTypeName() = typeName
        fun getRoundPolygon(radius: Float, boxSize: Float, offset: Offset) = when (this) {
            Common -> RoundedPolygon(
                numVertices = 12,
                rounding = CornerRounding(0.2f),
                radius = radius,
                centerX = offset.x + boxSize / 2f,
                centerY = offset.y
            )
            CIRCLE -> RoundedPolygon.circle(
                numVertices = 5,
                radius = radius,
                centerX = offset.x + boxSize / 2f,
                centerY = offset.y
            )
            PILL -> RoundedPolygon.pill(
                width = radius ,
                height = radius / 2,
                smoothing = 100f,
                centerX = offset.x + boxSize / 2f,
                centerY = offset.y
            )
            PILL_STAR -> RoundedPolygon.pillStar(
                width = radius,
                height = radius / 2,
                numVerticesPerRadius = 8,
                centerX = offset.x + boxSize / 2f,
                centerY = offset.y
            )
            RECTANGLE -> RoundedPolygon.rectangle(
                width = radius,
                height = radius / 2,
                centerX = offset.x + boxSize / 2f,
                centerY = offset.y
            )
            STAR -> RoundedPolygon.star(
                numVerticesPerRadius = 6,
                radius = radius,
                innerRadius = radius / 2,
                centerX = offset.x + boxSize / 2f,
                centerY = offset.y
            )

        }
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