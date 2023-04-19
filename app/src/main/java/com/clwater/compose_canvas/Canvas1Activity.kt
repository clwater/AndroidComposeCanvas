package com.clwater.compose_canvas

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.clwater.compose_canvas.ui.theme.AndroidComposeCanvasTheme

class Canvas1Activity : ComponentActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, Canvas1Activity::class.java))
        }
    }

    class Canvas1ViewModel : ViewModel(){
        var sliderValue = mutableStateOf(0f)
    }

    val model by viewModels<Canvas1ViewModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidComposeCanvasTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Canvas_1()
                        Slider(value = model.sliderValue.value,
                            onValueChange = {
                                model.sliderValue.value = it
                            },
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .fillMaxSize(),
                            steps = 100
                        )
                    }
                }
            }
        }
    }


    val mCanvasWidth = 240.dp
    val mCanvasHeight = 100.dp
    val mCanvasRadius = mCanvasHeight / 2f
    val mShadowWidth = mCanvasHeight / 9f
    val mButtonWidth = mCanvasWidth - mCanvasHeight / 10f * 2
    val mButtonHeight = mCanvasHeight - mCanvasHeight / 10f * 2
    val mSunCloudRadius = mCanvasRadius - mCanvasHeight / 10f
    val mLightBackgroundColor = listOf(
        Color(0xFF1565C0),
        Color(0xFF1E88E5),
        Color(0xFF2196F3),
        Color(0xFF42A5F5),
    )

    val mSunColor = Color(0xFFFFD54F)
    val mSunTopShadowColor = Color(0xCCFFFFFF)
    val mSunBottomShadowColor = Color(0x80827717)

    val mMoonColor = Color(0xFF979797)
    val mMoonTopShadowColor = Color(0xCCFFFFFF)
    val mMoonBottomShadowColor = Color(0xFF5E5E5E)

    val mSunRadius = mSunCloudRadius * 0.9f
    val mMoonRadius = mSunCloudRadius * 0.9f

    val mCommonBackgroundColor = Color.Gray

    @Preview
    @Composable
    fun Canvas_1() {
        Box(
            modifier = Modifier
                .padding(top = 100.dp, start = 100.dp)
                .width(mCanvasWidth)
                .height(mCanvasHeight)
        ) {
            Background()
            SunCloud()
//        BackgroundShadow()
//        Sun()
//        Moon()

            SunAndMoon(model.sliderValue.value, Star.Sun)
        }
    }

    enum class Star {
        Sun,
        Moon,
        ToSun,
        ToMoon
    }

    @Composable
    fun SunAndMoon(progress: Float, star: Star) {
        Box(
            modifier = Modifier
                .width(mCanvasWidth)
                .height(mCanvasHeight)
        ) {
            Box(modifier = Modifier
                .height(100.dp)
                .width(100.dp)) {
                Moon()
                Sun(progress)

            }
        }
    }

    @Composable
    fun Moon() {
        val infiniteTransition = rememberInfiniteTransition()
        val offset by infiniteTransition.animateFloat(
            initialValue = -1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1000,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )


        Canvas(
            modifier = Modifier
                .width(mMoonRadius * 2f)
                .height(mMoonRadius * 2f)
                .offset(
//                    x = mCanvasWidth - (mCanvasHeight - mMoonRadius * 2f) / 2f - mMoonRadius * 2f,
                    x = mCanvasWidth / 2f - mSunRadius,
                    y = (mCanvasHeight - mMoonRadius * 2f) / 2f
                )
                .graphicsLayer(alpha = 0.99f)
                .clip(RoundedCornerShape(mCanvasRadius))
                .clipToBounds(),
        ) {
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                drawCircle(
                    color = mMoonTopShadowColor,
                    radius = mMoonRadius.toPx() + mMoonRadius.toPx() * 0.1f,
                    center = Offset(size.width / 2f, size.height / 2f),
                )
                drawCircle(
                    color = Color.Transparent,
                    radius = mMoonRadius.toPx() * 1.05f,
                    center = Offset(
                        size.width / 2f + mMoonRadius.toPx() * 0.05f + mMoonRadius.toPx() * 0.005f * offset,
                        size.height / 2f + mMoonRadius.toPx() * 0.1f + mMoonRadius.toPx() * 0.005f * offset
                    ),
                    blendMode = BlendMode.Clear
                )
                restoreToCount(checkPoint)
            }

            drawCircle(
                color = mMoonColor,
                radius = mMoonRadius.toPx() * 1.05f,
                center = Offset(
                    size.width / 2f + mMoonRadius.toPx() * 0.05f + mMoonRadius.toPx() * 0.005f * offset,
                    size.height / 2f + mMoonRadius.toPx() * 0.1f + mMoonRadius.toPx() * 0.005f * offset
                ),
            )

            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                drawCircle(
                    color = mMoonBottomShadowColor,
                    radius = mMoonRadius.toPx() + mMoonRadius.toPx() * 0.1f,
                    center = Offset(size.width / 2f, size.height / 2f)
                )
                drawCircle(
                    color = Color.Transparent,
                    radius = mMoonRadius.toPx(),
                    center = Offset(
                        size.width / 2f - mMoonRadius.toPx() * 0.05f + mMoonRadius.toPx() * 0.005f * offset,
                        size.height / 2f - mMoonRadius.toPx() * 0.1f + mMoonRadius.toPx() * 0.005f * offset
                    ),
                    blendMode = BlendMode.Clear
                )
                restoreToCount(checkPoint)
            }

        }
    }

//@Composable
//fun BackgroundShadow() {
//    Canvas(
//        modifier = Modifier
//            .width(mCanvasWidth)
//            .height(mCanvasHeight)
//            .clipToBounds()
//            .clip(RoundedCornerShape(mCanvasRadius))
//    ) {
//        val leftShader = RadialGradientShader(
//            center = Offset(mCanvasRadius.toPx(), mCanvasRadius.toPx()),
//            radius = mCanvasRadius.toPx(),
//            colors = listOf(
//                Color.Red,
//                Color.Transparent,
//                Color.Green
//            ),
//        )
//        val leftPaint = Paint().apply {
//            shader = leftShader
//        }
//        drawIntoCanvas {
//            it.drawArc(
//                left = 0f,
//                top = 0f,
//                right = mCanvasRadius.toPx() * 2,
//                bottom = mCanvasRadius.toPx() * 2,
//                startAngle = 225f,
//                sweepAngle = 45f,
//                useCenter = true,
//                paint = leftPaint
//            )
//        }
//
//    }
//
//}

    @Composable
    fun SunCloud() {
        val cloudOffsetX = (mCanvasWidth - mSunRadius * 1.1f) / 7f
        val cloudOffsetY = mCanvasHeight / 2f / 10f
        val baseOffsetX = -mSunCloudRadius / 5f
        val baseOffsetY = mCanvasHeight / 6f
        val cloudShadowOffsetY = -mCanvasHeight / 8f

        val cloudColor: Color = Color(0xFFFFFFFF)
        val cloudColorShadow: Color = Color(0xFFFFFFFF)

        val offsetRadius = listOf(1f, 0.8f, 0.6f, 0.5f, 0.6f, 0.8f, 0.6f)
        val offsetX = listOf(0, 2, 4, 6, 7, 8, 8)
        val shadowOffsetY = listOf(1f, 2f, 2f, 2f, 1f, 1f, 1f)
        val shadowOffsetX = listOf(0f, 0f, 0f, 0f, 0f, 0f, -0.8f)

        val infiniteTransition = rememberInfiniteTransition()
        val animationOffsetX by infiniteTransition.animateFloat(
            initialValue = -1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3100,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )

        val animationOffsetY by infiniteTransition.animateFloat(
            initialValue = -1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2900,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )

        val animationOffsetRadius by infiniteTransition.animateFloat(
            initialValue = -1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )



        Canvas(
            modifier = Modifier
                .width(mCanvasWidth)
                .height(mCanvasHeight)
                .alpha(0.5f)
                .clip(RoundedCornerShape(mCanvasRadius)),
        ) {

            for (i in 0..6) {
                drawCircle(
                    color = cloudColorShadow,
                    radius = mSunCloudRadius.toPx() * offsetRadius[i] + mSunCloudRadius.toPx() * 0.08f * animationOffsetRadius,
                    center = Offset(
                        size.width - cloudOffsetX.toPx() * i + baseOffsetX.toPx() - baseOffsetX.toPx() * shadowOffsetX[i] + size.width * 0.05f * animationOffsetX,
                        size.height / 2f + cloudOffsetY.toPx() * offsetX[i] + baseOffsetY.toPx() + cloudShadowOffsetY.toPx() * shadowOffsetY[i] + size.height / 2f * 0.05f * animationOffsetY
                    )
                )
            }
        }


        Canvas(
            modifier = Modifier
                .width(mCanvasWidth)
                .height(mCanvasHeight)
                .clip(RoundedCornerShape(mCanvasRadius)),
        ) {
            for (i in 0..6) {
                drawCircle(
                    color = cloudColor,
                    radius = mSunCloudRadius.toPx() * offsetRadius[i] + mSunCloudRadius.toPx() * 0.06f * animationOffsetRadius,
                    center = Offset(
                        size.width - cloudOffsetX.toPx() * i + baseOffsetX.toPx() + size.width * 0.04f * animationOffsetX,
                        size.height / 2f + cloudOffsetY.toPx() * offsetX[i] + baseOffsetY.toPx() + size.height / 2f * 0.04f * animationOffsetY
                    )
                )
            }
        }
    }

    @Composable
    fun Sun(progress: Float) {
        val infiniteTransition = rememberInfiniteTransition()
        val offset by infiniteTransition.animateFloat(
            initialValue = -1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1000,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )

        val progressX = mSunRadius * 2f * progress


        Canvas(
            modifier = Modifier
                .width(mSunRadius * 2f)
                .height(mSunRadius * 2f)
                .offset(
                    x = mCanvasWidth / 2f -  mSunRadius ,
//                    x = (mCanvasHeight - mSunRadius * 2f) / 2f,
                    y = (mCanvasHeight - mSunRadius * 2f) / 2f
                )
                .graphicsLayer(alpha = 0.99f)
                .clip(RoundedCornerShape(mCanvasRadius))
                .clipToBounds(),
        ) {
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                drawCircle(
                    color = mSunTopShadowColor,
                    radius = mSunRadius.toPx() + mSunRadius.toPx() * 0.1f,
                    center = Offset(size.width / 2f + progressX.toPx(), size.height / 2f),
                )
                drawCircle(
                    color = Color.Transparent,
                    radius = mSunRadius.toPx() * 1.05f,
                    center = Offset(
                        size.width / 2f + mSunRadius.toPx() * 0.05f + mSunRadius.toPx() * 0.005f * offset
                                + progressX.toPx(),
                        size.height / 2f + mSunRadius.toPx() * 0.1f + mSunRadius.toPx() * 0.005f * offset
                    ),
                    blendMode = BlendMode.Clear
                )
                restoreToCount(checkPoint)
            }

            drawCircle(
                color = mSunColor,
                radius = mSunRadius.toPx() * 1.05f,
                center = Offset(
                    size.width / 2f + mSunRadius.toPx() * 0.05f + mSunRadius.toPx() * 0.005f * offset
                            + progressX.toPx(),
                    size.height / 2f + mSunRadius.toPx() * 0.1f + mSunRadius.toPx() * 0.005f * offset
                ),
            )

            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                drawCircle(
                    color = mSunBottomShadowColor,
                    radius = mSunRadius.toPx() + mSunRadius.toPx() * 0.1f,
                    center = Offset(size.width / 2f + progressX.toPx(), size.height / 2f)
                )
                drawCircle(
                    color = Color.Transparent,
                    radius = mSunRadius.toPx(),
                    center = Offset(
                        size.width / 2f - mSunRadius.toPx() * 0.05f + mSunRadius.toPx() * 0.005f * offset
                                + progressX.toPx(),
                        size.height / 2f - mSunRadius.toPx() * 0.1f + mSunRadius.toPx() * 0.005f * offset
                    ),
                    blendMode = BlendMode.Clear
                )
                restoreToCount(checkPoint)
            }

        }
    }

    @Composable
    fun Background() {
        Canvas(
            modifier = Modifier
                .width(mCanvasWidth)
                .height(mCanvasHeight)
                .clip(RoundedCornerShape(mCanvasRadius))
                .clipToBounds(),
            onDraw = {
                val maxRadius = mCanvasWidth.toPx() - mCanvasRadius.toPx() * 1.5f
                val minRadius = maxRadius * 0.3f

                drawCircle(
                    color = mLightBackgroundColor[0],
                    radius = maxRadius * 1.1f,
                    center = Offset(mSunCloudRadius.toPx() * 1.5f, mCanvasHeight.toPx() / 2f)
                )

                drawCircle(
                    color = mLightBackgroundColor[1],
                    radius = minRadius + (maxRadius - minRadius) / 7f * 4f,
                    center = Offset(mSunCloudRadius.toPx() * 1.5f, mCanvasHeight.toPx() / 2f)
                )

                drawCircle(
                    color = mLightBackgroundColor[2],
                    radius = minRadius + (maxRadius - minRadius) / 7f * 2f,
                    center = Offset(mSunCloudRadius.toPx() * 1.5f, mCanvasHeight.toPx() / 2f)
                )

                drawCircle(
                    color = mLightBackgroundColor[3],
                    radius = minRadius,
                    center = Offset(mSunCloudRadius.toPx() * 1.5f, mCanvasHeight.toPx() / 2f)
                )
            }
        )
    }


    fun getBitmapCircle(radius: Int, color: Color): Bitmap {
        val bitmap = Bitmap.createBitmap(
            radius * 2,
            radius * 2,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = color.toArgb()
        canvas.drawCircle(radius.toFloat(), radius.toFloat(), radius.toFloat(), paint)

        return bitmap
    }

    // 怎样将Canvas转换为Bitmap
// 代码
//    val bitmap = Bitmap.createBitmap(
//        mCanvasWidth.toInt(),
//        mCanvasHeight.toInt(),
//        Bitmap.Config.ARGB_8888
//    )
    fun DrawScope.drawSunTopShadow() {
        val centerX = mSunRadius + mCanvasHeight - mSunRadius * 2f
        val centerY = mCanvasHeight / 2f


    }

}