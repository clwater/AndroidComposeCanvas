package com.clwater.compose_canvas

import android.graphics.Paint
import android.graphics.Paint.Style
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val mButtonWidth = 200.dp
val mButtonHeight = 50.dp
val mRadius = mButtonHeight / 2f
val mCommonBackgroundColor = Color.Gray

@Preview(showBackground = true)
@Composable
fun Canvas_1() {

    Column(
        modifier = Modifier.fillMaxSize()
    ){
        Canvas(
            modifier = Modifier
                .width(mButtonWidth)
                .height(mButtonHeight)
            ,
        ){
            drawBackground()
        }
    }
}

fun DrawScope.drawBackground() {
//    drawCircle(
//        color = mCommonBackgroundColor,
//        center = Offset(mRadius.toPx(), mRadius.toPx()),
//        radius = mRadius.toPx()
//    )
//
//    drawCircle(
//        color = mCommonBackgroundColor,
//        center = Offset(mButtonWidth.toPx() - mRadius.toPx(), mRadius.toPx()),
//        radius = mRadius.toPx()
//    )
//
//    drawRect(
//        color = mCommonBackgroundColor,
//        topLeft = Offset(mRadius.toPx(), 0f),
//        size = Size(mButtonWidth.toPx() - mRadius.toPx() * 2, mButtonHeight.toPx())
//    )


    val linearColor = LinearGradientShader(
        from = Offset(0f, 0f),
        to = Offset(mButtonWidth.toPx(), mButtonHeight.toPx()),
        colors = listOf(
            Color.Red,
            Color.Blue,
            Color.Green
        )
    )

    val rediaPoint_1 = RadialGradientShader(
        center = Offset(mRadius.toPx() * 1.5f, mRadius.toPx()),
        radius = mRadius.toPx() * 2,
        colors = listOf(
            Color.Black,
            Color.Gray,
        )
    )

    drawIntoCanvas {
        it.drawRoundRect(
            left = 0f,
            top = 0f,
            right = mButtonWidth.toPx(),
            bottom = mButtonHeight.toPx(),
            radiusX = mRadius.toPx(),
            radiusY = mRadius.toPx(),
            paint = androidx.compose.ui.graphics.Paint().apply {
//                color = mCommonBackgroundColor
                shader = rediaPoint_1
                style  = PaintingStyle.Stroke
                strokeWidth = 3.dp.toPx()
            }
        )

        it.drawRoundRect(
            left = 0f,
            top = 0f,
            right = mButtonWidth.toPx(),
            bottom = mButtonHeight.toPx(),
            radiusX = mRadius.toPx(),
            radiusY = mRadius.toPx(),
            paint = androidx.compose.ui.graphics.Paint().apply {
                color = mCommonBackgroundColor
//                shader = rediaPoint_1
            }
        )







    }
}


