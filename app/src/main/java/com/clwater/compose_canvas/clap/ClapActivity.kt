package com.clwater.compose_canvas.clap

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
                    Clap(45)
                }
            }
        }
    }

    @Composable
    fun TopTips(clapCount: Int) {
        if (clapCount == 0) {
            return
        }

        var showTopTips by remember {
            mutableStateOf(true)
        }

        LaunchedEffect(clapCount) {
            showTopTips = true
            delay(1000)
            showTopTips = false
        }

        AnimatedVisibility(visible = showTopTips) {
            if (clapCount > mMaxClap) {
                Text(text = "clapCount: $mMaxClap")
            } else {
                Text(text = "clapCount: $clapCount")
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

        LaunchedEffect(clapCount) {
            if (clapCount > 0) {
                showFill = true
            }
        }

        LaunchedEffect(inTouch) {
            if (false) {
                return@LaunchedEffect
            }
            delay(100)
            Log.d("clwater", "inTouch")
            inTouch = false
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
                ) {
                    TopTips(clapCount)
                    Image(
                        painter = if (showFill) {
                            painterResource(id = R.drawable.icon_hand_fill)
                        } else {
                            painterResource(id = R.drawable.icon_hand_outline)
                        },
                        contentDescription = "Hand",

                        modifier = Modifier
                            .size(100.dp)
                            .pointerInteropFilter {
                                when (it.action) {
                                    MotionEvent.ACTION_DOWN -> {
                                        clapCount++
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