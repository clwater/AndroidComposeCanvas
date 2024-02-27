package com.clwater.compose_canvas.clap

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.AnticipateOvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.animation.addListener
import com.clwater.compose_canvas.R
import com.clwater.compose_canvas.ui.theme.AndroidComposeCanvasTheme
import java.lang.Exception
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClapActivity : ComponentActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ClapActivity::class.java))
        }
        const val mMaxClap = 50
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidComposeCanvasTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Clap()
                    }
                }
            }
        }
    }

    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalComposeUiApi::class
    )
    @Composable
    fun Clap(startClapCount: Int = 0) {
        var isInit by remember {
            mutableStateOf(false)
        }
        val snackbarHostState = SnackbarHostState()

        var showFill by remember {
            mutableStateOf(false)
        }

        var clapCount by remember {
            mutableStateOf(startClapCount)
        }

        var inTouch by remember {
            mutableStateOf(false)
        }

        var scale by remember {
            mutableStateOf(1f)
        }

        var inAnimator by remember {
            mutableStateOf(false)
        }

        val animator = ValueAnimator.ofFloat(1f, 0.95f, 1.1f, 1f).apply {
            duration = 700
            repeatCount = 0
            addUpdateListener {
                scale = it.animatedValue as Float
            }
            interpolator = AnticipateOvershootInterpolator()
        }
        animator.addListener(onEnd = {
            if (inTouch) {
                animator.start()
            } else {
                inAnimator = false
            }
        })

        LaunchedEffect(clapCount) {
            if (clapCount > 0) {
                showFill = true
            }
        }

        LaunchedEffect(inAnimator) {
            if (!inAnimator) {
                return@LaunchedEffect
            }
            if (!animator.isStarted && !animator.isRunning && inTouch) {
                animator.start()
            }
        }

        LaunchedEffect(inTouch) {
            if (!inTouch) {
                return@LaunchedEffect
            } else {
                clapCount++
            }
            while (true) {
                inAnimator = true
                delay(300)
                clapCount++
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(it),
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .align(
                            alignment = Alignment.CenterHorizontally
                        )
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .height(50.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        if (clapCount != startClapCount) {
                            TopTips(clapCount)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(100.dp)
                    ) {
                        if (isInit) {
                            ClapFlowers(clapCount - startClapCount + 3)
                        }
                        Image(
                            painter = if (showFill) {
                                painterResource(id = R.drawable.icon_hand_fill)
                            } else {
                                painterResource(id = R.drawable.icon_hand_outline)
                            },
                            contentDescription = "Hand",

                            modifier = Modifier
                                .scale(scale)
                                .size(100.dp)
                                .pointerInteropFilter {
                                    when (it.action) {
                                        MotionEvent.ACTION_DOWN -> {
                                            if (!isInit) {
                                                isInit = true
                                            }
                                            inTouch = true
                                        }

                                        MotionEvent.ACTION_UP -> {
                                            inTouch = false
                                        }

                                        else -> false
                                    }
                                    true
                                }

                        )
                    }
                }
            }
        }
    }

    @Composable
    fun TopTips(clapCount: Int) {
        var showTopTips by remember {
            mutableStateOf(true)
        }

        LaunchedEffect(clapCount) {
            showTopTips = true
            delay(1000)
            showTopTips = false
        }

        if (showTopTips) {
            Box(
                modifier = Modifier.background(
                    color = Color.Black,
                    shape = RoundedCornerShape(100.dp)
                )
            ) {
                val showCount =
                    if (clapCount > mMaxClap) {
                        "+$mMaxClap"
                    } else {
                        "+$clapCount"
                    }
                Text(
                    text = showCount,
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.Center),
                    color = Color.White
                )
            }
        }
    }

    @Composable
    fun ClapFlowers(clapCount: Int) {
        var flower_1 by remember {
            mutableStateOf(0)
        }
        var flower_2 by remember {
            mutableStateOf(0)
        }

        var flower_show_1 by remember {
            mutableStateOf(false)
        }
        var flower_show_2 by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(clapCount) {
            flower_1 = ((clapCount - 0) / 3f).toInt()
            flower_2 = ((clapCount - 1) / 3f).toInt()
        }

        LaunchedEffect(flower_1) {
            try {
                flower_show_1 = true
                delay(800)
                flower_show_1 = false
            } catch (e: Exception) {
                flower_show_1 = false
            }
        }
        LaunchedEffect(flower_2) {
            try {
                flower_show_2 = true
                delay(800)
                flower_show_2 = false
            } catch (e: Exception) {
                flower_show_2 = false
            }
        }

        if (flower_show_1) {
            ClapFlower()
        }
        if (flower_show_2) {
            ClapFlower()
        }
    }

    @Composable
    fun ClapFlower() {
        val offsetRotate = Random(System.currentTimeMillis()).nextInt(0, 72)
        val flowersWidth = 100.dp
        val flowersHeight = 100.dp

        val alpha = remember {
            Animatable(0f)
        }
        val distance = remember {
            Animatable(-1f)
        }

        LaunchedEffect(Unit) {
            launch {
                alpha.animateTo(1f, animationSpec = tween(300))
                distance.animateTo(1f, animationSpec = tween(200))
                delay(700)
                alpha.animateTo(0f, animationSpec = tween(300))
            }
        }

        for (i in 0..4) {
            val childRotate = i * 72.0 + offsetRotate
            val offsetX = flowersWidth / 2f * 1.1f * sin(Math.toRadians(childRotate)).toFloat()
            val offsetY = flowersHeight / 2f * 1.1f * cos(Math.toRadians(childRotate)).toFloat()
            Canvas(
                Modifier.offset(
                    flowersWidth / 2f + offsetX,
                    flowersHeight / 2f + offsetY
                ).rotate(-childRotate.toFloat())
                    .alpha(alpha.value)
            ) {
                drawCircle(
                    color = Color(0xFF59A5B3),
                    radius = 10f,
                    center = Offset(10f, 0f + flowersWidth.toPx() / 20f * distance.value)
                )
                val tripPath = Path()
                tripPath.moveTo(0f, 40f + flowersWidth.toPx() / 20f * distance.value)
                tripPath.lineTo(-10f, 5f + flowersWidth.toPx() / 20f * distance.value)
                tripPath.lineTo(-20f, 40f + flowersWidth.toPx() / 20f * distance.value)
                drawPath(
                    path = tripPath,
                    color = Color(0xFFF29394)
                )
            }
        }
    }
}