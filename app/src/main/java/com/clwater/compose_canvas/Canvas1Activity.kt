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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.clwater.compose_canvas.ui.theme.AndroidComposeCanvasTheme
import kotlin.random.Random

class Canvas1Activity : ComponentActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, Canvas1Activity::class.java))
        }
    }

    class NightStar{
        var x = mutableStateOf(0f)
        var y = mutableStateOf(0f)
        var radius = mutableStateOf(0f)
    }

    class Canvas1ViewModel : ViewModel(){
        var progress = mutableStateOf(0f)
        var startStatus = mutableStateOf(Star.ToSun)
        var nightStar = mutableStateOf(listOf<NightStar>())

    }

    fun getRandom(min: Float, max: Float): Float {
        return Random.nextFloat() * (max - min) + min
    }

    fun initNightStart(){
        val nightStartX: Dp = (mCanvasHeight - mStarRadius * 2f)
        val nightEndX: Dp = (mCanvasWidth - (mCanvasHeight + mStarRadius * 2f))
        val nightStartY: Dp = (mCanvasHeight - mStarRadius * 2f)
        val nightEndY: Dp = (mCanvasHeight - (mCanvasHeight + mStarRadius * 2f))
        model.nightStar.value = listOf()
        for (i in 0..10){
                val star = NightStar()
                star.x.value = getRandom(nightStartX.value, nightEndX.value)
                star.y.value = getRandom(nightStartY.value, nightEndY.value)
                star.radius.value = getRandom(10f, 20f)
                model.nightStar.value = model.nightStar.value + star

        }
    }

    val model by viewModels<Canvas1ViewModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNightStart()
        setContent {
            AndroidComposeCanvasTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().background(Color.Blue)
                    ) {
                        Canvas_1()
                        Slider(value = model.progress.value,
                            onValueChange = {
//                                Log.d("gzb", it.toString())
                                model.progress.value = it
                            },
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth(),
                            steps = 100
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(1f),
                            horizontalArrangement = Arrangement.Center){
                            Button(
                                onClick = {
                                model.startStatus.value = Star.ToMoon
                                model.progress.value = 0f
                            }) {
                                Text("To Moon")
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                            Button(onClick = {
                                model.startStatus.value = Star.ToSun
                                model.progress.value = 1f
                            }) {
                                Text("To Sun")
                            }
                        }
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
    val mPerDistance = 0.2f

    val mLightBackgroundColor = listOf(
        Color(0xFF1565C0),
        Color(0xFF1E88E5),
        Color(0xFF2196F3),
        Color(0xFF42A5F5),
    )
    val mNightBackgroundColor = listOf(
        Color(0xFF1C1E2B),
        Color(0xFF2E323C),
        Color(0xFF3E424E),
        Color(0xFF4F555D),
    )
    

    val mSunColor = Color(0xFFFFD54F)
    val mSunTopShadowColor = Color(0xCCFFFFFF)
    val mSunBottomShadowColor = Color(0x80827717)

    val mMoonColor = Color(0xFFC3C9D1)
    val mMoonTopShadowColor = Color(0xCCFFFFFF)
    val mMoonBottomShadowColor = Color(0xFF5E5E5E)

    val mStarRadius = mSunCloudRadius * 0.9f

    val mCommonBackgroundColor = Color.Gray

    val mStarMove = mCanvasWidth - (mCanvasHeight - mStarRadius * 2f) - mStarRadius * 2f


    @Preview
    @Composable
    fun Canvas_1() {
        Box(
            modifier = Modifier
                .padding(top = 100.dp, start = 100.dp)
                .width(mCanvasWidth)
                .height(mCanvasHeight)
        ) {
            Background(model.progress.value)
            SunCloud(model.progress.value)
            NightStarts(model.progress.value)
            SunAndMoon(model.progress.value, model.startStatus.value)
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
                .height(mStarRadius * 2)
                .width(mStarRadius * 2)) {
                when(star){
                    Star.Sun ->
                        Sun()
                    Star.Moon ->
                        Moon()
                    Star.ToSun ->{
                        Moon(progress, true)
                        Sun(progress, true)
                    }
                    Star.ToMoon ->{
                        Sun(progress, false)
                        Moon(progress, false)
                    }
                }
            }
        }
    }


    @Composable
    fun SunCloud(progress: Float) {
        val cloudOffsetX = (mCanvasWidth - mStarRadius * 1.1f) / 7f
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



        Box(modifier = Modifier.clip(RoundedCornerShape(mCanvasRadius))) {
            Canvas(
                modifier = Modifier
                    .width(mCanvasWidth)
                    .height(mCanvasHeight)
                    .offset(y = mCanvasHeight * progress)
                    .alpha(0.5f)
                ,
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
                    .offset(y = mCanvasHeight * progress)
                ,
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

    }

    @Composable
    fun NightStarts(progress: Float) {
        for (nightStar in model.nightStar.value){
            NightStart(nightStar, progress)
        }
    }


    @Composable
    fun NightStart(nightStar: NightStar, progress: Float){

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

            val progressX = 0.dp
//            val progressX  = if(nightStar.reversal){
//                0.dp
//            }else {
//                if(progress <= mPerDistance) {
//                    mStarRadius * 2f
//                }else if (progress >= (1 - mPerDistance)){
//                    0.dp
//                } else {
//                    mStarRadius * 2f - mStarRadius * 2f * (progress - mPerDistance) * (1 / (1 - mPerDistance * 2))
//                }
//            }

            Canvas(
                modifier = Modifier
                    .width(mCanvasWidth)
                    .height(mCanvasHeight)
                    .offset(x = progressX)
                    .offset(y = mCanvasHeight * progress)
            ) {
                drawCircle(
                    color = Color.White,
                    radius = mStarRadius.toPx(),
                    center = Offset(
                        nightStar.x.value,
                        nightStar.y.value
                    )
                )
            }
    }


    @Composable
    fun Moon(progress: Float = 1f, reversal: Boolean = false) {
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

        val progressX  = if(reversal){
            0.dp
        }else {
                if(progress <= mPerDistance) {
                    mStarRadius * 2f
                }else if (progress >= (1 - mPerDistance)){
                    0.dp
                } else {
                    mStarRadius * 2f - mStarRadius * 2f * (progress - mPerDistance) * (1 / (1 - mPerDistance * 2))
                }
        }

        Canvas(
            modifier = Modifier
                .width(mStarRadius * 2f)
                .height(mStarRadius * 2f)
                .offset(
                    x = (mCanvasHeight - mStarRadius * 2f) / 2f + mStarMove * progress,
//                    x = (mCanvasHeight - mStarRadius * 2f) / 2f,
                    y = (mCanvasHeight - mStarRadius * 2f) / 2f
                )
                .graphicsLayer(alpha = 0.99f)
                .clip(RoundedCornerShape(mCanvasRadius))
                .clipToBounds(),
        ) {
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                drawCircle(
                    color = mMoonTopShadowColor,
                    radius = mStarRadius.toPx() + mStarRadius.toPx() * 0.1f,
                    center = Offset(size.width / 2f + progressX.toPx(), size.height / 2f),
                )
                drawCircle(
                    color = Color.Transparent,
                    radius = mStarRadius.toPx() * 1.05f,
                    center = Offset(
                        size.width / 2f + mStarRadius.toPx() * 0.05f + mStarRadius.toPx() * 0.005f * offset
                                + progressX.toPx(),
                        size.height / 2f + mStarRadius.toPx() * 0.1f + mStarRadius.toPx() * 0.005f * offset
                    ),
                    blendMode = BlendMode.Clear
                )
                restoreToCount(checkPoint)
            }

            drawCircle(
                color = mMoonColor,
                radius = mStarRadius.toPx() * 1.05f,
                center = Offset(
                    size.width / 2f + mStarRadius.toPx() * 0.05f + mStarRadius.toPx() * 0.005f * offset
                            + progressX.toPx(),
                    size.height / 2f + mStarRadius.toPx() * 0.1f + mStarRadius.toPx() * 0.005f * offset
                ),
            )

            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                drawCircle(
                    color = mMoonBottomShadowColor,
                    radius = mStarRadius.toPx() + mStarRadius.toPx() * 0.1f,
                    center = Offset(size.width / 2f + progressX.toPx(), size.height / 2f)
                )
                drawCircle(
                    color = Color.Transparent,
                    radius = mStarRadius.toPx(),
                    center = Offset(
                        size.width / 2f - mStarRadius.toPx() * 0.05f + mStarRadius.toPx() * 0.005f * offset
                                + progressX.toPx(),
                        size.height / 2f - mStarRadius.toPx() * 0.1f + mStarRadius.toPx() * 0.005f * offset
                    ),
                    blendMode = BlendMode.SrcIn
                )
                restoreToCount(checkPoint)
            }

        }
    }


    @Composable
    fun Sun(progress: Float = 0f, reversal: Boolean = false) {
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

        val progressX  = if(reversal){
            if(progress <= mPerDistance) {
                0.dp
            }else if (progress >= (1 - mPerDistance)){
                mStarRadius * 2f
            } else {
               -mStarRadius * 2f * (progress - mPerDistance) * (1 / (1 - mPerDistance * 2))
            }
        }else{
            0.dp
        }

        Canvas(
            modifier = Modifier
                .width(mStarRadius * 2f)
                .height(mStarRadius * 2f)
                .offset(
                    x = (mCanvasHeight - mStarRadius * 2f) / 2f + mStarMove * progress,
//                    x = (mCanvasHeight - mStarRadius * 2f) / 2f,
                    y = (mCanvasHeight - mStarRadius * 2f) / 2f
                )
                .graphicsLayer(alpha = 0.99f)
                .clip(RoundedCornerShape(mCanvasRadius))
                .clipToBounds(),
        ) {
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                drawCircle(
                    color = mSunTopShadowColor,
                    radius = mStarRadius.toPx() + mStarRadius.toPx() * 0.1f,
                    center = Offset(size.width / 2f + progressX.toPx(), size.height / 2f),
                )
                drawCircle(
                    color = Color.Transparent,
                    radius = mStarRadius.toPx() * 1.05f,
                    center = Offset(
                        size.width / 2f + mStarRadius.toPx() * 0.05f + mStarRadius.toPx() * 0.005f * offset
                                + progressX.toPx(),
                        size.height / 2f + mStarRadius.toPx() * 0.1f + mStarRadius.toPx() * 0.005f * offset
                    ),
                    blendMode = BlendMode.Clear
                )
                restoreToCount(checkPoint)
            }

            drawCircle(
                color = mSunColor,
                radius = mStarRadius.toPx() * 1.05f,
                center = Offset(
                    size.width / 2f + mStarRadius.toPx() * 0.05f + mStarRadius.toPx() * 0.005f * offset
                            + progressX.toPx(),
                    size.height / 2f + mStarRadius.toPx() * 0.1f + mStarRadius.toPx() * 0.005f * offset
                ),
            )

            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                drawCircle(
                    color = mSunBottomShadowColor,
                    radius = mStarRadius.toPx() + mStarRadius.toPx() * 0.1f,
                    center = Offset(size.width / 2f + progressX.toPx(), size.height / 2f)
                )
                drawCircle(
                    color = Color.Transparent,
                    radius = mStarRadius.toPx(),
                    center = Offset(
                        size.width / 2f - mStarRadius.toPx() * 0.05f + mStarRadius.toPx() * 0.005f * offset
                                + progressX.toPx(),
                        size.height / 2f - mStarRadius.toPx() * 0.1f + mStarRadius.toPx() * 0.005f * offset
                    ),
                    blendMode = BlendMode.SrcIn
                )
                restoreToCount(checkPoint)
            }

        }
    }

    private fun offsetColor(colorStart: Color, colorEnd: Color, progress: Float): Color {

        val offsetColor = if (progress < mPerDistance){
            0f
        }else if (progress > (1 - mPerDistance)){
            1f
        }else{
            (progress - mPerDistance) * (1 / (1 - mPerDistance * 2))
        }
        val red = (((colorStart.red +  (colorEnd.red - colorStart.red) * offsetColor) *  0xFF).toInt())
        val green = (((colorStart.green +  (colorEnd.green - colorStart.green) * offsetColor) *  0xFF).toInt())
        val blue = (((colorStart.blue +  (colorEnd.blue - colorStart.blue) * offsetColor) *  0xFF).toInt())
        val color = ((0xFF and 0xFF) shl 24) or
                ((red and 0xFF) shl 16) or
                ((green and 0xFF) shl 8) or
                (blue and 0xFF)
        return Color(color)

    }

    @Composable
    fun Background(progress: Float) {
        val backgroundMove = mCanvasWidth - (mCanvasHeight - mStarRadius * 2f) - mStarRadius * 2
        val offsetX = (mCanvasHeight - mStarRadius * 2f) / 2f + backgroundMove * progress + mStarRadius
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
                    color = offsetColor(mLightBackgroundColor[0], mNightBackgroundColor[0], progress),
                    radius = maxRadius * 1.2f,
                    center = Offset(offsetX.toPx(),
                        mCanvasHeight.toPx() / 2f)
                )

                drawCircle(
                    color = offsetColor(mLightBackgroundColor[1], mNightBackgroundColor[1], progress),
                    radius = minRadius + (maxRadius - minRadius) / 7f * 4f,
                    center = Offset(offsetX.toPx(),
                        mCanvasHeight.toPx() / 2f)
                )

                drawCircle(
                    color = offsetColor(mLightBackgroundColor[2], mNightBackgroundColor[2], progress),
                    radius = minRadius + (maxRadius - minRadius) / 7f * 2f,
                    center = Offset(offsetX.toPx(),
                         mCanvasHeight.toPx() / 2f)
                )

                drawCircle(
                    color = offsetColor(mLightBackgroundColor[3], mNightBackgroundColor[3], progress),
                    radius = minRadius,
                    center = Offset(offsetX.toPx(),
                        mCanvasHeight.toPx() / 2f)
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
        val centerX = mStarRadius + mCanvasHeight - mStarRadius * 2f
        val centerY = mCanvasHeight / 2f
    }

}