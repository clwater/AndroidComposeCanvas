package com.clwater.compose_canvas

import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint


val mCanvasWidth = 150.dp
val mCanvasHeight = 50.dp
val mCanvasRadius = mCanvasHeight / 2f
val mShadowWidth = 3.dp
val mButtonWidth = mCanvasWidth - mShadowWidth * 2
val mButtonHeight = mCanvasHeight - mShadowWidth * 2
val mRadius = mCanvasRadius - mShadowWidth
val mLightBackgroundColor = listOf(
    Color(0xFF1565C0),
    Color(0xFF1E88E5),
    Color(0xFF2196F3),
    Color(0xFF42A5F5),
)

val mSunColor = Color(0xFFFFB300)
val mSunTopShadowColor = Color(0xFFFFECB3)
val mSunRadius = mRadius * 0.9f

val mCommonBackgroundColor = Color.Gray

@Preview
@Composable
fun Canvas_1() {

    Column(
        modifier = Modifier
            .padding(top = 100.dp, start = 10.dp)
            .fillMaxSize()
    ){
        Canvas(
            modifier = Modifier
                .width(mCanvasWidth)
                .height(mCanvasHeight)
                .clip(RoundedCornerShape(mCanvasRadius))
                .clipToBounds()
            ,
            onDraw = {
                drawBackground()
                drawSun()
            }
        )
    }
}

fun getBitmapCircle(radius: Int, color: Color) : Bitmap {
    val bitmap = Bitmap.createBitmap(
        radius * 2,
        radius * 2,
        Bitmap.Config.ARGB_8888
    )
    val canvas = AndroidCanvas(bitmap)
    val paint = AndroidPaint()
    paint.color = color.toArgb()
    canvas.drawCircle(radius.toFloat(), radius.toFloat(),  radius.toFloat(), paint)

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
    val centerX = mSunRadius + mCanvasHeight -  mSunRadius * 2f
    val centerY =  mCanvasHeight / 2f


}

fun DrawScope.drawSun() {
    val centerX = mSunRadius + mCanvasHeight -  mSunRadius * 2f
    val centerY =  mCanvasHeight / 2f

    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)

        drawCircle(
            color = mSunTopShadowColor,
            radius = mSunRadius.toPx(),
            center = Offset(centerX.toPx(), centerY.toPx())
        )

        drawCircle(
            color = mSunColor,
            radius = mSunRadius.toPx() * 1.1f,
            center = Offset(centerX.toPx() + mSunRadius.toPx() * 0.15f,
                mCanvasHeight.toPx() / 2f + mSunRadius.toPx() * 0.15f),
            blendMode = BlendMode.SrcIn
        )

    }



}


fun DrawScope.drawBackground() {
    val maxRadius = mCanvasWidth.toPx() - mCanvasRadius.toPx() * 1.5f
    val minRadius =  maxRadius * 0.45f

    drawCircle(
        color = mLightBackgroundColor[0],
        radius = maxRadius * 1.1f,
        center = Offset(mRadius.toPx() * 1.5f, mCanvasHeight.toPx() / 2f)
    )

    drawCircle(
        color = mLightBackgroundColor[1],
        radius = minRadius + (maxRadius - minRadius ) / 7f * 4f,
        center = Offset(mRadius.toPx() * 1.5f, mCanvasHeight.toPx() / 2f)
    )

    drawCircle(
        color = mLightBackgroundColor[2],
        radius = minRadius + (maxRadius - minRadius ) / 7f * 2f,
        center = Offset(mRadius.toPx() * 1.5f, mCanvasHeight.toPx() / 2f)
    )

    drawCircle(
        color = mLightBackgroundColor[3],
        radius = minRadius,
        center = Offset(mRadius.toPx() * 1.5f, mCanvasHeight.toPx() / 2f)
    )
}


