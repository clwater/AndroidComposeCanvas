package com.clwater.compose_canvas

import android.graphics.Bitmap
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint


val mCanvasWidth = 120.dp
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

val mSunColor = Color(0xFFFFD54F)
val mSunTopShadowColor = Color(0xFFFFF9C4)
val mSunBottomShadowColor = Color(0xFF827717)
val mSunRadius = mRadius * 0.9f

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
        Sun()
    }
}

@Composable
fun SunCloud() {
    val cloudOffsetX = (mCanvasWidth - mSunRadius * 1.1f) / 7f
    val cloudOffsetY = mCanvasHeight / 2f / 10f
    val baseOffsetX = - mRadius / 5f
    val baseOffsetY = mCanvasHeight / 8f
    val cloudShadowOffsetY = - mCanvasHeight / 8f

    val cloudColor: Color = Color(0xFFFFFFFF)
    val cloudColorShadow: Color = Color(0xFFFFFFFF)

    val offsetRadius = listOf(1f, 0.8f, 0.6f, 0.4f, 0.6f, 0.8f, 0.6f)
    val offsetX = listOf(0, 2, 4, 6, 7, 8, 8)
    val shadowOffsetY = listOf(1f, 2f, 2f, 2f, 1f, 1f, 1f)
    val shadowOffsetX = listOf(0f, 0f, 0f, 0f, 0f, 0f, -0.8f)


    Canvas(
        modifier = Modifier
            .width(mCanvasWidth)
            .height(mCanvasHeight)
            .alpha(0.5f)
            .clip(RoundedCornerShape(mCanvasRadius))
        ,
    ){

        for (i in 0..6) {
            drawCircle(
                color = cloudColorShadow,
                radius = mRadius.toPx() * offsetRadius[i],
                center = Offset(size.width - cloudOffsetX.toPx() * i + baseOffsetX.toPx() - baseOffsetX.toPx() * shadowOffsetX[i],
                    size.height / 2f + cloudOffsetY.toPx() * offsetX[i] + baseOffsetY.toPx() + cloudShadowOffsetY.toPx() * shadowOffsetY[i])
            )
        }
    }


    Canvas(
        modifier = Modifier
            .width(mCanvasWidth)
            .height(mCanvasHeight)
            .clip(RoundedCornerShape(mCanvasRadius))
        ,
    ){
        for (i in 0..6) {
            drawCircle(
                color = cloudColor,
                radius = mRadius.toPx() * offsetRadius[i],
                center = Offset(size.width - cloudOffsetX.toPx() * i + baseOffsetX.toPx(),
                    size.height / 2f + cloudOffsetY.toPx() * offsetX[i] + baseOffsetY.toPx())
            )
        }
    }
}

@Composable
fun Sun(){
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
            .width(mSunRadius * 2f)
            .height(mSunRadius * 2f)
            .offset(x = (mCanvasHeight -  mSunRadius * 2f) / 2f,
                    y = (mCanvasHeight -  mSunRadius * 2f) / 2f)
            .clip(RoundedCornerShape(mCanvasRadius))
            .clipToBounds()
        ,
    ){
        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)
            drawCircle(
                color = mSunTopShadowColor,
                radius = mSunRadius.toPx() + mSunRadius.toPx() * 0.1f,
                center = Offset(size.width / 2f, size.height / 2f)
            )
            drawCircle(
                color = mSunColor,
                radius = mSunRadius.toPx() * 1.05f,
                center = Offset(size.width / 2f + mSunRadius.toPx() * 0.05f + mSunRadius.toPx() * 0.005f * offset,
                    size.height / 2f + mSunRadius.toPx() * 0.1f + mSunRadius.toPx() * 0.005f * offset),
                blendMode = BlendMode.SrcIn
            )
            restoreToCount(checkPoint)
        }

        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)
            drawCircle(
                color = mSunBottomShadowColor,
                radius = mSunRadius.toPx() + mSunRadius.toPx() * 0.1f,
                center = Offset(size.width / 2f, size.height / 2f)
            )
            drawCircle(
                color = Color.Transparent,
                radius = mSunRadius.toPx(),
                center = Offset(size.width / 2f - mSunRadius.toPx() * 0.05f + mSunRadius.toPx() * 0.005f * offset,
                    size.height / 2f - mSunRadius.toPx() * 0.1f + mSunRadius.toPx() * 0.005f * offset),
                blendMode = BlendMode.SrcIn
            )
            restoreToCount(checkPoint)
        }


    }
}

@Composable
fun Background(){

    Canvas(
        modifier = Modifier
            .width(mCanvasWidth)
            .height(mCanvasHeight)
            .clip(RoundedCornerShape(mCanvasRadius))
            .clipToBounds()
        ,
        onDraw = {
            val maxRadius = mCanvasWidth.toPx() - mCanvasRadius.toPx() * 1.5f
            val minRadius =  maxRadius * 0.3f

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
    )
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
