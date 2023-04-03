package com.clwater.compose_canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val mButtonWidth = 200.dp
val mButtonHeight = 50.dp
val mRadius = mButtonHeight / 2f
val mCommonBackgroundColor = Color.Gray
val mShadowWidth = 3.dp

@Preview(showBackground = true)
@Composable
fun Canvas_1() {

    Column(
        modifier = Modifier.padding(top =  100.dp, start = 10.dp).fillMaxSize()
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
        to = Offset(mButtonWidth.toPx() + mRadius.toPx(), mButtonHeight.toPx() + mRadius.toPx()),
        colors = listOf(
            Color(0x80000000),
            Color(0x80000000),
            Color(0x80F0F0F0),
        )
    )

    drawIntoCanvas {


        it.drawRoundRect(
            left = mShadowWidth.toPx() / 2,
            top = mShadowWidth.toPx() / 2,
            right = mButtonWidth.toPx() - mShadowWidth.toPx() / 2,
            bottom = mButtonHeight.toPx() - mShadowWidth.toPx() / 2,
            radiusX = mRadius.toPx(),
            radiusY = mRadius.toPx(),
            paint = Paint().apply {
                color = mCommonBackgroundColor
                style  = PaintingStyle.Fill
                strokeWidth = mShadowWidth.toPx()
            }
        )

        it.drawRoundRect(
            left = 0f,
            top = 0f,
            right = mButtonWidth.toPx(),
            bottom = mButtonHeight.toPx(),
            radiusX = mRadius.toPx(),
            radiusY = mRadius.toPx(),
            paint = Paint().apply {
                shader = linearColor
                style  = PaintingStyle.Stroke
                strokeWidth = mShadowWidth.toPx()
            }
        )


    }
}


