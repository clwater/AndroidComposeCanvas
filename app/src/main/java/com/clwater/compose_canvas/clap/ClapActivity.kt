package com.clwater.compose_canvas.clap

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.animation.AnticipateOvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.animation.addListener
import com.clwater.compose_canvas.R
import com.clwater.compose_canvas.ui.theme.AndroidComposeCanvasTheme
import kotlinx.coroutines.delay

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
                    Clap(3)
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
                    modifier = Modifier.padding(10.dp).align(Alignment.Center),
                    color = Color.White
                )
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalComposeUiApi::class
    )
    @Composable
    fun Clap(startClapCount: Int = 0) {
        val snackbarHostState = SnackbarHostState()
        val scope = rememberCoroutineScope()

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
            Log.d("gzb", "animator is END")
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
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.align(
                        alignment = Alignment.CenterHorizontally
                    )
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.height(50.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        if (clapCount != startClapCount) {
                            TopTips(clapCount)
                        }
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
                            .align(Alignment.CenterHorizontally)
                            .pointerInteropFilter {
                                when (it.action) {
                                    MotionEvent.ACTION_DOWN -> {
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

    fun TimeInterpolator.toEasing() = Easing {
            x ->
        getInterpolation(x)
    }
}