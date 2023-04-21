package com.clwater.compose_canvas

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.lifecycle.ViewModel
import com.clwater.compose_canvas.ui.theme.AndroidComposeCanvasTheme
import kotlin.random.Random


class Canvas1Activity : ComponentActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, Canvas1Activity::class.java))
        }
    }

    /**
     * Night Star Info
     */
    class NightStar{
        // position and radius
        var x = mutableStateOf(0f)
        var y = mutableStateOf(0f)
        var radius = mutableStateOf(0f)
        var alpha = mutableStateOf(0f)
        var status = mutableStateOf(NightStarStatus.Start)
    }

    /**
     * Night Star Status
     */
    enum class NightStarStatus{
        Start,
        End,
        Lighting
    }

    class Canvas1ViewModel : ViewModel(){
        var progress = mutableStateOf(0f)
        var startStatus = mutableStateOf(Star.ToSun)
        var nightStar = mutableStateListOf<NightStar>()

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
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(1f),
                            horizontalArrangement = Arrangement.Center){
                            Canvas_1()
                        }
                        Slider(value = model.progress.value,
                            onValueChange = {
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
                                    val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
                                    valueAnimator.duration = 1000
                                    valueAnimator.interpolator = DecelerateInterpolator()
                                    valueAnimator.addUpdateListener {
                                        val value = it.animatedValue as Float
                                        model.progress.value = value
                                    }
                                    valueAnimator.addListener {
                                        it.doOnEnd {
                                            model.startStatus.value = Star.Moon
                                        }
                                    }
                                    valueAnimator.start()
                            }) {
                                Text("To Moon")
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                            Button(onClick = {
                                model.startStatus.value = Star.ToSun
                                model.progress.value = 1f
                                val valueAnimator = ValueAnimator.ofFloat(1f, 0f)
                                valueAnimator.duration = 1000
                                valueAnimator.interpolator = DecelerateInterpolator()
                                valueAnimator.addUpdateListener {
                                    val value = it.animatedValue as Float
                                    model.progress.value = value
                                }
                                valueAnimator.addListener {
                                    it.doOnEnd {
                                        model.startStatus.value = Star.Sun
                                    }
                                }
                                valueAnimator.start()
                            }) {
                                Text("To Sun")
                            }
                        }
                    }
                }
            }
        }
    }


    val mCanvasWidth = 160.dp
    val mCanvasHeight = 60.dp
    val mCanvasRadius = mCanvasHeight / 2f
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
    val mSunColorDeep = Color(0xFFFFA726)
    val mSunTopShadowColor = Color(0xCCFFFFFF)
    val mSunBottomShadowColor = Color(0x80827717)

    val mMoonColor = Color(0xFFC3C9D1)
    val mMoonTopShadowColor = Color(0xCCFFFFFF)
    val mMoonBottomShadowColor = Color(0xFF5E5E5E)
    val mMoonDownColor = Color(0xFF73777E)

    val mStarRadius = mSunCloudRadius * 0.9f

    val mStarMove = mCanvasWidth - (mCanvasHeight - mStarRadius * 2f) - mStarRadius * 2f


    @Preview
    @Composable
    fun Canvas_1() {
        Box(
            modifier = Modifier
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

        val progressY = if (progress < mPerDistance){
            0f
        }else if (progress > (1f - mPerDistance)){
            1f
        }else{
            (progress - mPerDistance) / (1f - mPerDistance * 2f)
        }

        Box(modifier = Modifier.clip(RoundedCornerShape(mCanvasRadius))) {
            Canvas(
                modifier = Modifier
                    .width(mCanvasWidth)
                    .height(mCanvasHeight)
                    .offset(y = mCanvasHeight * progressY)
                    .alpha(0.5f),
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
                    .offset(y = mCanvasHeight * progressY),
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

    @Stable
    fun getRandomStart(): NightStar {
        val star = NightStar()
        star.x.value = getRandom(0f, 1f)
        star.y.value = getRandom(0f, 1f)
        star.radius.value = getRandom(0f, 1f)
        star.status.value = NightStarStatus.Start
        return star
    }

    @Composable
    fun NightStarts(progress: Float) {
        if (model.nightStar.isEmpty()){
            model.nightStar.clear()
            for (i in 0..10){
                model.nightStar.add(getRandomStart())
            }
        }

        val progressY = if (progress < mPerDistance){
            0f
        }else if (progress > (1f - mPerDistance)){
            1f
        }else{
            (progress - mPerDistance) / (1f - mPerDistance * 2f)
        }


        for (nightStar in model.nightStar){
            NightStart(nightStar, progressY)
        }
    }


    @Composable
    fun NightStart(nightStar: NightStar, progress: Float){

        if (nightStar.status.value == NightStarStatus.Start){
            nightStar.status.value = NightStarStatus.Lighting
            val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
            valueAnimator.duration = getRandom(3000, 6000)
            valueAnimator.repeatMode = ValueAnimator.REVERSE
            valueAnimator.repeatCount = 2
            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Float
                nightStar.alpha.value = value
            }
            valueAnimator.addListener {
                it.doOnEnd {
                    nightStar.status.value = NightStarStatus.End
                    model.nightStar.remove(nightStar)
                    if(model.nightStar.size < 10){
                        model.nightStar.add(getRandomStart())
                    }
                }
            }
            valueAnimator.start()
        }


            Canvas(
                modifier = Modifier
                    .width(mCanvasWidth)
                    .height(mCanvasHeight)
                    .offset(y = -mCanvasHeight + mCanvasHeight * progress)
                    .alpha(nightStar.alpha.value)
            ) {
                val temp = Pair((mCanvasHeight.toPx() - mStarRadius.toPx() * 2f) / 2f  +
                                (mCanvasWidth.toPx() / 2f - (mCanvasHeight.toPx() - mStarRadius.toPx() * 2f) / 2f ) * nightStar.x.value,
                                (mCanvasHeight.toPx() - mStarRadius.toPx() * 2f) / 2f  +
                                (mButtonHeight.toPx() - (mCanvasHeight.toPx() - mStarRadius.toPx() * 2f) / 2f ) * nightStar.y.value)
                // you can check the start position is not too nearly with other stars
                val x = temp.first
                val y = temp.second
                val radius = mCanvasHeight.toPx() / 30f + (mCanvasHeight.toPx() / 60f) * nightStar.radius.value
                val path = Path()
                path.moveTo(x, y + radius)
                path.lineTo(x + radius / 3f , y +  radius / 3f)
                path.lineTo(x + radius, y)
                path.lineTo(x + radius / 3f, y - radius / 3f)
                path.lineTo(x, y - radius)
                path.lineTo(x - radius / 3f, y - radius / 3f)
                path.lineTo(x - radius, y)
                path.lineTo(x - radius / 3f, y + radius / 3f)
                path.close()

                drawPath(
                    path = path,
                    color = Color.White,
                    style = Stroke(width = radius/ 2f)
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

        val offsetMoonDown by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 5000,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            )
        )

        val progressX  = if(reversal){
            0.dp
        }else {
                if(progress <= mPerDistance) {
                    mStarRadius * 2.5f
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
                    color = mMoonColor,
                    radius = mStarRadius.toPx() * 1.05f,
                    center = Offset(
                        size.width / 2f + mStarRadius.toPx() * 0.05f + mStarRadius.toPx() * 0.005f * offset
                                + progressX.toPx(),
                        size.height / 2f + mStarRadius.toPx() * 0.1f + mStarRadius.toPx() * 0.005f * offset
                    ),
                )
                drawCircle(
                    color = mMoonDownColor,
                    radius = mStarRadius.toPx() / 3f,
                    center = Offset(size.width / 2f - height / 4f + size.width * offsetMoonDown - size.width,
                        size.height / 5f * 3f),
                    blendMode = BlendMode.SrcIn

                )
                drawCircle(
                    color = mMoonDownColor,
                    radius = mStarRadius.toPx() / 3f,
                    center = Offset(size.width / 2f - height / 4f + size.width * offsetMoonDown,
                        size.height / 5f * 3f),
                    blendMode = BlendMode.SrcIn
                )
//
                drawCircle(
                    color = mMoonDownColor,
                    radius = mStarRadius.toPx() / 4f,
                    center = Offset(size.width / 2f + height / 6f + size.width * offsetMoonDown - size.width,
                        size.height / 4f * 1f),
                    blendMode = BlendMode.SrcIn
                )

                drawCircle(
                    color = mMoonDownColor,
                    radius = mStarRadius.toPx() / 4f,
                    center = Offset(size.width / 2f + height / 6f + size.width * offsetMoonDown,
                        size.height / 4f * 1f),
                    blendMode = BlendMode.SrcIn
                )

                drawCircle(
                    color = mMoonDownColor,
                    radius = mStarRadius.toPx() / 4f,
                    center = Offset(size.width / 2f + height / 8f + size.width * offsetMoonDown - size.width,
                        size.height / 4f * 3f),
                    blendMode = BlendMode.SrcIn
                )

                drawCircle(
                    color = mMoonDownColor,
                    radius = mStarRadius.toPx() / 4f,
                    center = Offset(size.width / 2f + height / 8f + size.width * offsetMoonDown,
                        size.height / 4f * 3f),
                    blendMode = BlendMode.SrcIn
                )



                drawCircle(
                    color = mMoonDownColor,
                    radius = mStarRadius.toPx() / 6f,
                    center = Offset( height / 8f + size.width * offsetMoonDown - size.width,
                        size.height / 5f * 1f),
                    blendMode = BlendMode.SrcIn
                )

                drawCircle(
                    color = mMoonDownColor,
                    radius = mStarRadius.toPx() / 6f,
                    center = Offset( height / 8f + size.width * offsetMoonDown,
                        size.height / 5f * 1f),
                    blendMode = BlendMode.SrcIn
                )


                restoreToCount(checkPoint)
            }


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

        val animationOffsetSun by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 5000,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )

        val progressX  = if(reversal){
            if(progress <= mPerDistance) {
                0.dp
            }else if (progress >= (1 - mPerDistance)){
                mStarRadius * 2.5f
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
                color = offsetColor(mSunColor, mSunColorDeep, animationOffsetSun),
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
        val infiniteTransition = rememberInfiniteTransition()
        val offset1 by infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = getRandom(3000, 5000).toInt(),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )
        val offset2 by infiniteTransition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = getRandom(5000, 7000).toInt(),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )
        val offset3 by infiniteTransition.animateFloat(
            initialValue = 0.85f,
            targetValue = 1.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = getRandom(1000, 10000).toInt(),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )

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
                    radius = (minRadius + (maxRadius - minRadius) / 7f * 4f)  * offset1,
                    center = Offset(offsetX.toPx(),
                        mCanvasHeight.toPx() / 2f)
                )

                drawCircle(
                    color = offsetColor(mLightBackgroundColor[2], mNightBackgroundColor[2], progress),
                    radius = (minRadius + (maxRadius - minRadius) / 7f * 2f)  * offset2,
                    center = Offset(offsetX.toPx(),
                         mCanvasHeight.toPx() / 2f)
                )

                drawCircle(
                    color = offsetColor(mLightBackgroundColor[3], mNightBackgroundColor[3], progress),
                    radius = minRadius * offset3,
                    center = Offset(offsetX.toPx(),
                        mCanvasHeight.toPx() / 2f)
                )
            }
        )
    }

    private fun getRandom(min: Float, max: Float): Float {
        return Random.nextFloat() * (max - min) + min
    }

    private fun getRandom(min: Int, max: Int): Long {
        return (Random.nextFloat() * (max - min) + min).toLong()
    }
}