package com.clwater.compose_canvas.bezier

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clwater.compose_canvas.ui.theme.AndroidComposeCanvasTheme
import kotlin.math.roundToInt

class BezierActivity : ComponentActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, BezierActivity::class.java))
        }
    }

    private val mPointRadius = 15.dp
    private val mLineWidth = 10.dp
    private val mTextSize = 16.sp

    private val model by viewModels<BezierViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidComposeCanvasTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DefaultView()
                }
            }
        }
    }

    @Composable
    fun DefaultView() {
        Column(modifier = Modifier.padding(12.dp)) {
            ControlView()
            BezierView()
        }
    }

    @Composable
    fun BezierView() {
        model.clear()
        Canvas(
            modifier = Modifier
                .border(width = 1.dp, color = Color.Black, shape = RectangleShape)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            model.pointDragStart(it)
                        },
                        onDragEnd = {
                            model.pointDragEnd()
                        }
                    ) { _, dragAmount ->
                        model.pointDragProgress(dragAmount)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures {
                        model.addPoint(it.x, it.y)
                    }
                }
        ) {
            lateinit var preBezierPoint: BezierPoint
            val paint = Paint()
            paint.textSize = mTextSize.toPx()

            for (pointList in model.mBezierDrawPoints) {
                if (pointList == model.mBezierDrawPoints.first() ||
                    (model.mInAuxiliary.value && !model.mInChange.value)
                ) {
                    for (point in pointList) {
                        if (point != pointList.first()) {
                            drawLine(
                                color = Color(point.color),
                                start = Offset(point.x.value, point.y.value),
                                end = Offset(preBezierPoint.x.value, preBezierPoint.y.value),
                                strokeWidth = mLineWidth.value
                            )
                        }
                        preBezierPoint = point

                        drawCircle(
                            color = Color(point.color),
                            radius = mPointRadius.value,
                            center = Offset(point.x.value, point.y.value)
                        )
                        paint.color = Color(point.color).toArgb()
                        drawIntoCanvas {
                            it.nativeCanvas.drawText(
                                point.name,
                                point.x.value - mPointRadius.value,
                                point.y.value - mPointRadius.value * 1.5f,
                                paint
                            )
                        }
                    }
                }
            }

            for (linePoint in model.mBezierLinePoints.toList()) {
                if (linePoint.first <= model.mProgress.value) {
                    drawCircle(
                        color = Color.Red,
                        radius = mPointRadius.value / 2f,
                        center = Offset(linePoint.second.first, linePoint.second.second)
                    )
                }
            }
        }
    }

    @Composable
    fun ControlView() {
        Column() {
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { model.start() },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f),
                    enabled = !model.mInChange.value
                ) {
                    Text(text = "Start")
                }
                Button(
                    onClick = { model.clear() },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f),
                    enabled = !model.mInChange.value
                ) {
                    Text(text = "Clear")
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { model.changeMovePoint() },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f)
                ) {
                    Text(text = "Change Point ${if (model.mInChange.value){"On"}else {"Off"}}")
                }
                Button(
                    onClick = { model.changeAuxiliary() },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f),
                    enabled = !model.mInChange.value
                ) {
                    Text(text = "${if (model.mInAuxiliary.value){"Close"}else {"Open"}} Auxiliary")
                }
                Button(
                    onClick = { model.changeMore() },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(1f),
                    enabled = !model.mInChange.value
                ) {
                    Text(text = "More Point ${if (model.mInMore.value){"On"}else {"Off"}}")
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "index(${model.mIndexRange.first}~${model.mIndexRange.second}): " +
                        "${model.mIndex.value}",
                    modifier = Modifier.weight(1f)
                )
                Slider(
                    modifier = Modifier.weight(2f),
                    enabled = !model.mInChange.value,
                    value = model.mIndex.value.toFloat(),
                    onValueChange = {
                        model.clear()
                        model.mIndex.value = it.roundToInt()
                    },
                    steps = (model.mIndexRange.second - model.mIndexRange.first - 1),
                    valueRange = model.mIndexRange.first.toFloat()..model.mIndexRange.second.toFloat() // ktlint-disable max-line-length
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "index(${model.mTimeRange.first}~${model.mTimeRange.second}): " +
                        "${model.mTime.value}",
                    modifier = Modifier.weight(1f)
                )
                Slider(
                    modifier = Modifier.weight(2f),
                    enabled = !model.mInChange.value,
                    value = model.mTime.value.toFloat(),
                    onValueChange = {
                        model.mTime.value = it.roundToInt()
                    },
                    steps = (model.mTimeRange.second - model.mTimeRange.first - 1),
                    valueRange = model.mTimeRange.first.toFloat()..model.mTimeRange.second.toFloat()
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "progress: ${(model.mProgress.value * 100).roundToInt() / 100f}",
                    modifier = Modifier.weight(1f)
                )
                Slider(
                    modifier = Modifier.weight(2f),
                    enabled = !model.mInChange.value,
                    value = model.mProgress.value,
                    onValueChange = {
                        model.mProgress.value = it
                        model.calculate()
                    },
                    steps = 100
                )
            }
        }
    }
}