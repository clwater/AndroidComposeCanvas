package com.clwater.compose_canvas.shape

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.ViewModel
import com.clwater.compose_canvas.R
import com.clwater.compose_canvas.shape.tmp.toComposePath
import com.clwater.compose_canvas.ui.theme.AndroidComposeCanvasTheme
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

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

    class ShapeViewModel : ViewModel() {
        var itemSize = 0.dp
        var startPolygon =
            RoundedPolygonModel(
                mutableStateOf(RoundedPolygonType.Common),
                CommonParam(),
                CircleParam()
            )
        var endPolygon = RoundedPolygonModel(
            mutableStateOf(RoundedPolygonType.STAR),
            CommonParam(),
            CircleParam()
        )

    }

    private val model by viewModels<ShapeViewModel>()

    @Composable
    fun ShapeCustoms() {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val smallItemSize: Dp = (screenWidth - 8.dp - 2.dp * 2 * 6) / 6f
        val boxSize: Dp = screenWidth / 6f

        model.itemSize = screenWidth / 3f
        val itemSizePx = model.itemSize.dpToPx()

        LaunchedEffect(Unit) {
            model.startPolygon.pillParam.widthAnimation.value = 1f
            model.startPolygon.pillParam.width.value =
                itemSizePx / 2f * model.startPolygon.pillParam.widthAnimation.value
            model.startPolygon.pillParam.heightAnimation.value = 0.5f
            model.startPolygon.pillParam.height.value =
                itemSizePx / 2f * model.startPolygon.pillParam.heightAnimation.value

            model.endPolygon.pillParam.widthAnimation.value = 1f
            model.endPolygon.pillParam.width.value =
                itemSizePx / 2f * model.startPolygon.pillParam.widthAnimation.value
            model.endPolygon.pillParam.heightAnimation.value = 0.5f
            model.endPolygon.pillParam.height.value =
                itemSizePx / 2f * model.startPolygon.pillParam.heightAnimation.value


            model.startPolygon.pillStarParam.widthAnimation.value = 1f
            model.startPolygon.pillStarParam.width.value =
                itemSizePx / 2f * model.startPolygon.pillStarParam.widthAnimation.value
            model.startPolygon.pillStarParam.heightAnimation.value = 0.5f
            model.startPolygon.pillStarParam.height.value =
                itemSizePx / 2f * model.startPolygon.pillStarParam.heightAnimation.value

            model.endPolygon.pillStarParam.widthAnimation.value = 1f
            model.endPolygon.pillStarParam.width.value =
                itemSizePx / 2f * model.startPolygon.pillStarParam.widthAnimation.value
            model.endPolygon.pillStarParam.heightAnimation.value = 0.5f
            model.endPolygon.pillStarParam.height.value =
                itemSizePx / 2f * model.startPolygon.pillStarParam.heightAnimation.value


            model.startPolygon.rectangleParam.widthAnimation.value = 2f
            model.startPolygon.rectangleParam.width.value =
                itemSizePx / 2f * model.startPolygon.rectangleParam.widthAnimation.value
            model.startPolygon.rectangleParam.heightAnimation.value = 1f
            model.startPolygon.rectangleParam.height.value =
                itemSizePx / 2f * model.startPolygon.rectangleParam.heightAnimation.value

            model.endPolygon.rectangleParam.widthAnimation.value = 2f
            model.endPolygon.rectangleParam.width.value =
                itemSizePx / 2f * model.startPolygon.rectangleParam.widthAnimation.value
            model.endPolygon.rectangleParam.heightAnimation.value = 1f
            model.endPolygon.rectangleParam.height.value =
                itemSizePx / 2f * model.startPolygon.rectangleParam.heightAnimation.value


            model.startPolygon.starParam.radiusAnimation.value = 1f
            model.startPolygon.starParam.radius.value =
                itemSizePx / 2f * model.startPolygon.starParam.radiusAnimation.value

            model.endPolygon.starParam.radiusAnimation.value = 1f
            model.endPolygon.starParam.radius.value =
                itemSizePx / 2f * model.startPolygon.starParam.radiusAnimation.value


            model.startPolygon.starParam.innerRadiusAnimation.value = 0.5f
            model.startPolygon.starParam.innerRadius.value =
                itemSizePx / 2f * model.startPolygon.starParam.innerRadiusAnimation.value

            model.endPolygon.starParam.innerRadiusAnimation.value = 0.5f
            model.endPolygon.starParam.innerRadius.value =
                itemSizePx / 2f * model.startPolygon.starParam.innerRadiusAnimation.value

        }




        Column(modifier = Modifier.padding(4.dp)) {
            Box(
                modifier = Modifier
                    .drawWithCache {
                        onDrawBehind {
                            for (i in 0 until RoundedPolygonType.values().size) {
                                val shape = getRoundPolygon(
                                    RoundedPolygonType.values()[i],
                                    smallItemSize.toPx() / 2f,
                                    Offset(
                                        (boxSize.toPx() - smallItemSize.toPx() / 40f) * i + boxSize.toPx() / 2f,
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

            RoundedPolygonAdjust(true, screenWidth / 3f + 8.dp, colors[6])
            RoundedPolygonAdjust(false, screenWidth / 3f + 8.dp, colors[7])
            AnimationRoundPolygon()
        }

    }

    @Composable
    fun AnimationRoundPolygon() {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp

        var shapeStart = remember {
            getRoundPolygonAnimation(model.startPolygon)

        }
        var shapeEnd = remember {
            getRoundPolygonAnimation(model.endPolygon)
        }


        val infiniteTransition = rememberInfiniteTransition("infinite outline movement")
        val animatedProgress = infiniteTransition.animateFloat(
            initialValue = 0.5f,
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


        val shape = remember {
            mutableStateOf(
                CustomRotatingMorphShape(
                    Morph(shapeStart, shapeEnd),
                    animatedProgress.value,
                    animatedRotation.value
                )
            )
        }

        LaunchedEffect(
            model.startPolygon.type.value,
            model.endPolygon.type.value,
            animatedProgress.value
        ) {
            shapeStart = getRoundPolygonAnimation(model.startPolygon)
            shapeEnd = getRoundPolygonAnimation(model.endPolygon)
            shape.value = CustomRotatingMorphShape(
                Morph(shapeStart, shapeEnd),
//                animatedProgress.value,
                0f,
                animatedRotation.value
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenWidth / 3f),
        ) {
            Image(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(shape.value)
                    .size(model.itemSize)
            )
        }
    }

    @Composable
    fun RoundedPolygonAdjust(
        isStart: Boolean,
        height: Dp,
        color: Color,
    ) {
        val showDropdownMenu = remember {
            mutableStateOf(false)
        }

        Row(
            modifier = Modifier
                .padding(4.dp)
                .border(width = 1.dp, color = color, shape = RoundedCornerShape(10.dp))
                .padding(4.dp)
        ) {
            Column(modifier = Modifier.width(model.itemSize)) {
                Box(
                    modifier = Modifier
                        .height(height)
                        .fillMaxWidth()
                        .drawWithCache {
                            val roundedPolygon = getRoundPolygon(
                                if (isStart) model.startPolygon else model.endPolygon,
                                size.width / 2f
                            )
                            val roundedPolygonPath = roundedPolygon.cubics
                                .toPath()
                            onDrawBehind {
                                drawPath(roundedPolygonPath, color = color)
                            }
                        }
                )

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    onClick = { showDropdownMenu.value = true }) {
                    Text(text = (if (isStart) model.startPolygon else model.endPolygon).type.value.getTypeName())
                    DropdownMenu(
                        expanded = showDropdownMenu.value, onDismissRequest = {
                            showDropdownMenu.value = false
                        }) {
                        RoundedPolygonType.values().forEach {
                            DropdownMenuItem(onClick = {
                                (if (isStart) model.startPolygon else model.endPolygon).type.value =
                                    it
                            },
                                text = { Text(text = it.getTypeName()) })
                        }
                    }
                }

            }


            Column {
                val roundingRadiusPx = model.itemSize.dpToPx()
                when ((if (isStart) model.startPolygon else model.endPolygon).type.value) {
                    RoundedPolygonType.Common -> {
                        Text("numVertices: ${(if (isStart) model.startPolygon else model.endPolygon).commonParam.numVertices.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).commonParam.numVertices.value.toFloat(),
                            steps = 0,
                            valueRange = 3f..12f,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).commonParam.numVertices.value =
                                    it.roundToInt()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(text = "rounding(Radius): ${(if (isStart) model.startPolygon else model.endPolygon).commonParam.roundingRadiusAnimation.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).commonParam.roundingRadiusAnimation.value,
                            steps = 0,
                            valueRange = 0f..1f,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).commonParam.roundingRadius.value =
                                    roundingRadiusPx / 2f * it
                                (if (isStart) model.startPolygon else model.endPolygon).commonParam.roundingRadiusAnimation.value =
                                    get2Float(it)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(text = "rounding(Smoothing): ${(if (isStart) model.startPolygon else model.endPolygon).commonParam.roundingSmoothing.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).commonParam.roundingSmoothing.value,
                            steps = 0,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).commonParam.roundingSmoothing.value =
                                    get2Float(it)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    RoundedPolygonType.CIRCLE -> {
                        Text("numVertices: ${(if (isStart) model.startPolygon else model.endPolygon).circleParam.numVertices.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).circleParam.numVertices.value.toFloat(),
                            steps = 0,
                            valueRange = 3f..8f,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).circleParam.numVertices.value =
                                    it.roundToInt()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    RoundedPolygonType.PILL -> {
                        Text("Smoothing: ${(if (isStart) model.startPolygon else model.endPolygon).pillParam.smoothing.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).pillParam.smoothing.value,
                            steps = 0,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).pillParam.smoothing.value =
                                    get2Float(it)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text("Width: ${(if (isStart) model.startPolygon else model.endPolygon).pillParam.widthAnimation.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).pillParam.widthAnimation.value,
                            steps = 0,
                            valueRange = 0.01f..1f,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).pillParam.widthAnimation.value =
                                    get2Float(it)
                                (if (isStart) model.startPolygon else model.endPolygon).pillParam.width.value =
                                    roundingRadiusPx / 2f * it

                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text("Height: ${(if (isStart) model.startPolygon else model.endPolygon).pillParam.heightAnimation.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).pillParam.heightAnimation.value,
                            steps = 0,
                            valueRange = 0.01f..1f,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).pillParam.heightAnimation.value =
                                    get2Float(it)
                                (if (isStart) model.startPolygon else model.endPolygon).pillParam.height.value =
                                    roundingRadiusPx / 2f * it
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    RoundedPolygonType.PILL_STAR -> {
                        Text("NumVerticesPerRadius: ${(if (isStart) model.startPolygon else model.endPolygon).pillStarParam.numVerticesPerRadius.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.numVerticesPerRadius.value.toFloat(),
                            steps = 0,
                            valueRange = 3f..12f,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.numVerticesPerRadius.value =
                                    it.roundToInt()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("InnerRadiusRatio: ${(if (isStart) model.startPolygon else model.endPolygon).pillStarParam.innerRadiusRatio.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.innerRadiusRatio.value,
                            steps = 0,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.innerRadiusRatio.value =
                                    get2Float(it)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Rounding(Radius): ${(if (isStart) model.startPolygon else model.endPolygon).pillStarParam.roundingRadiusAnimation.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.roundingRadiusAnimation.value,
                            steps = 0,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.roundingRadiusAnimation.value =
                                    get2Float(it)
                                (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.roundingRadius.value =
                                    roundingRadiusPx / 2f * it
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(text = "Rounding(Smoothing): ${(if (isStart) model.startPolygon else model.endPolygon).pillStarParam.roundingSmoothing.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.roundingSmoothing.value,
                            steps = 0,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.roundingSmoothing.value =
                                    get2Float(it)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )


                        Text("InnerRounding(Radius): ${(if (isStart) model.startPolygon else model.endPolygon).pillStarParam.innerRoundingRadiusAnimation.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.innerRoundingRadiusAnimation.value,
                            steps = 0,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.innerRoundingRadiusAnimation.value =
                                    get2Float(it)
                                (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.innerRoundingRadius.value =
                                    roundingRadiusPx / 2f * it
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(text = "InnerRounding(Smoothing): ${(if (isStart) model.startPolygon else model.endPolygon).pillStarParam.innerSmoothing.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.innerSmoothing.value,
                            steps = 0,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.innerSmoothing.value =
                                    get2Float(it)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("VertexSpacing: ${(if (isStart) model.startPolygon else model.endPolygon).pillStarParam.vertexSpacing.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.vertexSpacing.value,
                            steps = 0,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.vertexSpacing.value =
                                    get2Float(it)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Width: ${(if (isStart) model.startPolygon else model.endPolygon).pillStarParam.widthAnimation.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.widthAnimation.value,
                            steps = 0,
                            valueRange = 0.01f..1f,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.widthAnimation.value =
                                    get2Float(it)
                                (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.width.value =
                                    roundingRadiusPx / 2f * it

                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text("Height: ${(if (isStart) model.startPolygon else model.endPolygon).pillStarParam.heightAnimation.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.heightAnimation.value,
                            steps = 0,
                            valueRange = 0.01f..1f,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.heightAnimation.value =
                                    get2Float(it)
                                (if (isStart) model.startPolygon else model.endPolygon).pillStarParam.height.value =
                                    roundingRadiusPx / 2f * it
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    RoundedPolygonType.RECTANGLE -> {
                        Text("Rounding(Radius): ${(if (isStart) model.startPolygon else model.endPolygon).rectangleParam.roundingRadiusAnimation.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).rectangleParam.roundingRadiusAnimation.value,
                            steps = 0,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).rectangleParam.roundingRadiusAnimation.value =
                                    get2Float(it)
                                (if (isStart) model.startPolygon else model.endPolygon).rectangleParam.roundingRadius.value =
                                    roundingRadiusPx / 2f * it
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(text = "Rounding(Smoothing): ${(if (isStart) model.startPolygon else model.endPolygon).rectangleParam.roundingSmoothing.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).rectangleParam.roundingSmoothing.value,
                            steps = 0,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).rectangleParam.roundingSmoothing.value =
                                    get2Float(it)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )


                        Text("Width: ${(if (isStart) model.startPolygon else model.endPolygon).rectangleParam.widthAnimation.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).rectangleParam.widthAnimation.value,
                            steps = 0,
                            valueRange = 0.01f..2f,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).rectangleParam.widthAnimation.value =
                                    get2Float(it)
                                (if (isStart) model.startPolygon else model.endPolygon).rectangleParam.width.value =
                                    roundingRadiusPx / 2f * it

                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text("Height: ${(if (isStart) model.startPolygon else model.endPolygon).rectangleParam.heightAnimation.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).rectangleParam.heightAnimation.value,
                            steps = 0,
                            valueRange = 0.01f..2f,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).rectangleParam.heightAnimation.value =
                                    get2Float(it)
                                (if (isStart) model.startPolygon else model.endPolygon).rectangleParam.height.value =
                                    roundingRadiusPx / 2f * it
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    RoundedPolygonType.STAR ->{
                        Text("NumVerticesPerRadius: ${(if (isStart) model.startPolygon else model.endPolygon).starParam.numVerticesPerRadius.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).starParam.numVerticesPerRadius.value.toFloat(),
                            steps = 0,
                            valueRange = 3f..12f,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).starParam.numVerticesPerRadius.value =
                                    it.roundToInt()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Radius: ${(if (isStart) model.startPolygon else model.endPolygon).starParam.radiusAnimation.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).starParam.radiusAnimation.value,
                            steps = 0,
                            valueRange = 0.01f..1f,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).starParam.radiusAnimation.value =
                                    get2Float(it)
                                (if (isStart) model.startPolygon else model.endPolygon).starParam.radius.value =
                                    roundingRadiusPx / 2f * it
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("InnerRadius: ${(if (isStart) model.startPolygon else model.endPolygon).starParam.innerRadiusAnimation.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).starParam.innerRadiusAnimation.value,
                            steps = 0,
                            valueRange = 0.01f..1f,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).starParam.innerRadiusAnimation.value =
                                    get2Float(it)
                                (if (isStart) model.startPolygon else model.endPolygon).starParam.innerRadius.value =
                                    roundingRadiusPx / 2f * it
                            },
                            modifier = Modifier.fillMaxWidth()
                        )


                        Text("Rounding(Radius): ${(if (isStart) model.startPolygon else model.endPolygon).starParam.roundingRadiusAnimation.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).starParam.roundingRadiusAnimation.value,
                            steps = 0,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).starParam.roundingRadiusAnimation.value =
                                    get2Float(it)
                                (if (isStart) model.startPolygon else model.endPolygon).starParam.roundingRadius.value =
                                    roundingRadiusPx / 2f * it
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(text = "Rounding(Smoothing): ${(if (isStart) model.startPolygon else model.endPolygon).starParam.roundingSmoothing.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).starParam.roundingSmoothing.value,
                            steps = 0,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).starParam.roundingSmoothing.value =
                                    get2Float(it)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )


                        Text("InnerRounding(Radius): ${(if (isStart) model.startPolygon else model.endPolygon).starParam.innerRoundingRadiusAnimation.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).starParam.innerRoundingRadiusAnimation.value,
                            steps = 0,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).starParam.innerRoundingRadiusAnimation.value =
                                    get2Float(it)
                                (if (isStart) model.startPolygon else model.endPolygon).starParam.innerRoundingRadius.value =
                                    roundingRadiusPx / 2f * it
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(text = "InnerRounding(Smoothing): ${(if (isStart) model.startPolygon else model.endPolygon).starParam.innerRoundingSmoothing.value}")
                        Slider(
                            value = (if (isStart) model.startPolygon else model.endPolygon).starParam.innerRoundingSmoothing.value,
                            steps = 0,
                            onValueChange = {
                                (if (isStart) model.startPolygon else model.endPolygon).starParam.innerRoundingSmoothing.value =
                                    get2Float(it)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }


            }
        }
    }

    fun getRoundPolygonAnimation(roundedModel: RoundedPolygonModel): RoundedPolygon {
        return when (roundedModel.type.value) {
            RoundedPolygonType.Common -> RoundedPolygon(
                numVertices = roundedModel.commonParam.numVertices.value,
                rounding = CornerRounding(
                    radius = roundedModel.commonParam.roundingRadiusAnimation.value,
                    smoothing = roundedModel.commonParam.roundingSmoothing.value
                ),
            )

            RoundedPolygonType.CIRCLE -> RoundedPolygon.circle(
                numVertices = roundedModel.circleParam.numVertices.value,
            )

            RoundedPolygonType.PILL -> RoundedPolygon.pill(
                width = roundedModel.pillParam.widthAnimation.value,
                height = roundedModel.pillParam.heightAnimation.value,
                smoothing = roundedModel.pillParam.smoothing.value,
            )

            RoundedPolygonType.PILL_STAR -> RoundedPolygon.pillStar(
                width = roundedModel.pillStarParam.widthAnimation.value,
                height = roundedModel.pillStarParam.heightAnimation.value,
                numVerticesPerRadius = roundedModel.pillStarParam.numVerticesPerRadius.value,
                innerRadiusRatio = roundedModel.pillStarParam.innerRadiusRatio.value,
                rounding = CornerRounding(
                    radius = roundedModel.pillStarParam.roundingRadiusAnimation.value,
                    smoothing = roundedModel.pillStarParam.vertexSpacing.value
                ),
                innerRounding = CornerRounding(
                    radius = roundedModel.pillStarParam.innerRoundingRadiusAnimation.value,
                    smoothing = roundedModel.pillStarParam.vertexSpacing.value
                ),
                vertexSpacing = roundedModel.pillStarParam.vertexSpacing.value
            )

            RoundedPolygonType.RECTANGLE -> RoundedPolygon.rectangle(
                width = roundedModel.rectangleParam.widthAnimation.value,
                height =roundedModel.rectangleParam.heightAnimation.value,
                rounding = CornerRounding(
                    radius = roundedModel.rectangleParam.roundingRadiusAnimation.value,
                    smoothing = roundedModel.rectangleParam.roundingSmoothing.value
                ),
            )

            RoundedPolygonType.STAR -> RoundedPolygon.star(
                numVerticesPerRadius = roundedModel.starParam.numVerticesPerRadius.value,
                radius = roundedModel.starParam.radiusAnimation.value,
                innerRadius = roundedModel.starParam.innerRadiusAnimation.value,
                rounding = CornerRounding(
                    radius = roundedModel.starParam.roundingRadius.value,
                    smoothing = roundedModel.starParam.roundingSmoothing.value
                ),
                innerRounding = CornerRounding(
                    radius = roundedModel.starParam.innerRoundingRadius.value,
                    smoothing = roundedModel.starParam.innerRoundingSmoothing.value
                ),
            )

        }
    }

    fun getRoundPolygon(model: RoundedPolygonModel, radius: Float): RoundedPolygon {
        return getRoundPolygon(model, radius, Offset(radius, radius))
    }

    fun getRoundPolygon(type: RoundedPolygonType, radius: Float, offset: Offset): RoundedPolygon {
        return getRoundPolygon(
            RoundedPolygonModel(
                mutableStateOf(type),
            ), radius, offset, true
        )
    }

    fun getRoundPolygon(
        roundedModel: RoundedPolygonModel,
        radius: Float,
        offset: Offset
    ): RoundedPolygon {
        return getRoundPolygon(roundedModel, radius, offset, false)
    }

    fun getRoundPolygon(
        roundedModel: RoundedPolygonModel,
        radius: Float,
        offset: Offset,
        isFixed: Boolean
    ): RoundedPolygon {
        return when (roundedModel.type.value) {
            RoundedPolygonType.Common -> RoundedPolygon(
                numVertices = roundedModel.commonParam.numVertices.value,
                rounding = CornerRounding(
                    radius = roundedModel.commonParam.roundingRadius.value,
                    smoothing = roundedModel.commonParam.roundingSmoothing.value
                ),
                radius = radius,
                centerX = offset.x,
                centerY = offset.y,

                )

            RoundedPolygonType.CIRCLE -> RoundedPolygon.circle(
                numVertices = roundedModel.circleParam.numVertices.value,
                radius = radius,
                centerX = offset.x,
                centerY = offset.y
            )

            RoundedPolygonType.PILL -> RoundedPolygon.pill(
                width = if (isFixed) radius else roundedModel.pillParam.width.value,
                height = if (isFixed) radius else roundedModel.pillParam.height.value,
                smoothing = roundedModel.pillParam.smoothing.value,
                centerX = offset.x,
                centerY = offset.y
            )

            RoundedPolygonType.PILL_STAR -> RoundedPolygon.pillStar(
                width = if (isFixed) radius else roundedModel.pillStarParam.width.value,
                height = if (isFixed) radius else roundedModel.pillStarParam.height.value,
                numVerticesPerRadius = roundedModel.pillStarParam.numVerticesPerRadius.value,
                innerRadiusRatio = roundedModel.pillStarParam.innerRadiusRatio.value,
                rounding = CornerRounding(
                    radius = roundedModel.pillStarParam.roundingRadius.value,
                    smoothing = roundedModel.pillStarParam.roundingSmoothing.value
                ),
                innerRounding = CornerRounding(
                    radius = roundedModel.pillStarParam.innerRoundingRadius.value,
                    smoothing = roundedModel.pillStarParam.innerSmoothing.value
                ),
                vertexSpacing = roundedModel.pillStarParam.vertexSpacing.value,
                centerX = offset.x,
                centerY = offset.y
            )

            RoundedPolygonType.RECTANGLE -> RoundedPolygon.rectangle(
                width = if (isFixed) radius else roundedModel.rectangleParam.width.value,
                height = if (isFixed) radius else roundedModel.rectangleParam.height.value,
                rounding = CornerRounding(
                    radius = roundedModel.rectangleParam.roundingRadius.value,
                    smoothing = roundedModel.rectangleParam.roundingSmoothing.value
                ),
                centerX = offset.x,
                centerY = offset.y
            )

            RoundedPolygonType.STAR -> RoundedPolygon.star(
                numVerticesPerRadius = roundedModel.starParam.numVerticesPerRadius.value,
                radius = if (isFixed) radius else roundedModel.starParam.radius.value,
                innerRadius = if (isFixed) radius / 2 else roundedModel.starParam.innerRadius.value,
                rounding = CornerRounding(
                    radius = roundedModel.starParam.roundingRadius.value,
                    smoothing = roundedModel.starParam.roundingSmoothing.value
                ),
                innerRounding = CornerRounding(
                    radius = roundedModel.starParam.innerRoundingRadius.value,
                    smoothing = roundedModel.starParam.innerRoundingSmoothing.value
                ),
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


    fun get2Float(usedFloat: Float): Float {
        val df = DecimalFormat("#.00")
        df.roundingMode = RoundingMode.CEILING
        return df.format(usedFloat).toFloat()
    }

    @Composable
    fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }


    @Composable
    fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

    data class RoundedPolygonModel(
        var type: MutableState<RoundedPolygonType> = mutableStateOf(
            RoundedPolygonType.Common
        ),
        val commonParam: CommonParam = CommonParam(),
        val circleParam: CircleParam = CircleParam(),
        val pillParam: PillParam = PillParam(),
        val pillStarParam: PillStarParam = PillStarParam(),
        val rectangleParam: RectangleParam = RectangleParam(),
        val starParam: StarParam = StarParam()
    )

    data class CommonParam(
        var numVertices: MutableState<Int> = mutableStateOf(7),
        var roundingRadius: MutableState<Float> = mutableStateOf(0f),
        var roundingRadiusAnimation: MutableState<Float> = mutableStateOf(0f),
        var roundingSmoothing: MutableState<Float> = mutableStateOf(0.2f)
    )

    data class CircleParam(
        var numVertices: MutableState<Int> = mutableStateOf(7),
    )

    data class PillParam(
        val smoothing: MutableState<Float> = mutableStateOf(0f),
        val width: MutableState<Float> = mutableStateOf(0f),
        val widthAnimation: MutableState<Float> = mutableStateOf(0f),
        val height: MutableState<Float> = mutableStateOf(0f),
        val heightAnimation: MutableState<Float> = mutableStateOf(0f),
    )

    data class PillStarParam(
        val numVerticesPerRadius: MutableState<Int> = mutableStateOf(8),
        val innerRadiusRatio: MutableState<Float> = mutableStateOf(0.5f),
        val roundingRadius: MutableState<Float> = mutableStateOf(0f),
        val roundingRadiusAnimation: MutableState<Float> = mutableStateOf(0f),
        val roundingSmoothing: MutableState<Float> = mutableStateOf(0.5f),
        val innerRoundingRadius: MutableState<Float> = mutableStateOf(0f),
        val innerRoundingRadiusAnimation: MutableState<Float> = mutableStateOf(0f),
        val innerSmoothing: MutableState<Float> = mutableStateOf(0.5f),
        val vertexSpacing: MutableState<Float> = mutableStateOf(0.5f),
        val width: MutableState<Float> = mutableStateOf(0f),
        val widthAnimation: MutableState<Float> = mutableStateOf(0f),
        val height: MutableState<Float> = mutableStateOf(0f),
        val heightAnimation: MutableState<Float> = mutableStateOf(0f),
    )

    data class RectangleParam(
        val roundingRadius: MutableState<Float> = mutableStateOf(0f),
        val roundingRadiusAnimation: MutableState<Float> = mutableStateOf(0f),
        val roundingSmoothing: MutableState<Float> = mutableStateOf(0.5f),
        val width: MutableState<Float> = mutableStateOf(0f),
        val widthAnimation: MutableState<Float> = mutableStateOf(0f),
        val height: MutableState<Float> = mutableStateOf(0f),
        val heightAnimation: MutableState<Float> = mutableStateOf(0f),
    )

    data class StarParam(
        val numVerticesPerRadius: MutableState<Int> = mutableStateOf(5),
        val radius: MutableState<Float> = mutableStateOf(1f),
        val radiusAnimation: MutableState<Float> = mutableStateOf(1f),
        val innerRadius: MutableState<Float> = mutableStateOf(0.5f),
        val innerRadiusAnimation: MutableState<Float> = mutableStateOf(0.5f),
        val roundingRadius: MutableState<Float> = mutableStateOf(0f),
        val roundingRadiusAnimation: MutableState<Float> = mutableStateOf(0f),
        val roundingSmoothing: MutableState<Float> = mutableStateOf(0.5f),
        val innerRoundingRadius: MutableState<Float> = mutableStateOf(0f),
        val innerRoundingRadiusAnimation: MutableState<Float> = mutableStateOf(0f),
        val innerRoundingSmoothing: MutableState<Float> = mutableStateOf(0.5f),

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