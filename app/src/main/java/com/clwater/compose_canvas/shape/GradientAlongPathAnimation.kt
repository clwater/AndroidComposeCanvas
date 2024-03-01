package com.clwater.compose_canvas.shape

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.flatten
import kotlin.math.floor


/*
* Copyright 2022 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/**
 * Inspired by William Candillon https://youtu.be/7SCzL-XnfUU,
 * this uses Jetpack Compose to draw a gradient along a path
 * https://github.com/wcandillon/can-it-be-done-in-react-native/tree/master/bonuses/skia-examples/src/PathGradient
 */
@Composable
@Preview
fun GradientAlongPathAnimation() {
    val path = remember {
        HelloPath.test.toPath()
    }
    val bounds = path.getBounds()

    val totalLength = remember {
        val pathMeasure = PathMeasure()
        pathMeasure.setPath(path, false)
        pathMeasure.length
    }
    val lines = remember {
        path.asAndroidPath().flatten(0.5f)
    }
    val progress = remember {
        Animatable(0f)
    }
    LaunchedEffect(Unit) {
        progress.animateTo(
            1f,
            animationSpec = infiniteRepeatable(tween(3000))
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier
            .padding(top = 32.dp, start = 64.dp, bottom = 32.dp, end = 32.dp)
            .aspectRatio(bounds.width / bounds.height)
            .size(400.dp)
            .align(Alignment.Center),
            onDraw = {
                val currentLength = totalLength * progress.value
                lines.forEach { line ->
                    if (line.startFraction * totalLength < currentLength) {
                        val startColor = interpolateColors(line.startFraction, colors)
                        val endColor = interpolateColors(line.endFraction, colors)
                        drawLine(
                            brush = Brush.linearGradient(listOf(startColor, endColor)),
                            start = Offset(line.start.x, line.start.y),
                            end = Offset(line.end.x, line.end.y),
                            strokeWidth = 30f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            })
    }
}

private val colors = listOf(
    Color(0xFF3FCEBC),
    Color(0xFF3CBCEB),
    Color(0xFF5F96E7),
    Color(0xFF816FE3),
    Color(0xFF9F5EE2),
    Color(0xFFBD4CE0),
    Color(0xFFDE589F),
    Color(0xFFFF645E),
    Color(0xFFFDA859),
    Color(0xFFFAEC54),
    Color(0xFF9EE671),
    Color(0xFF67E282),
    Color(0xFF3FCEBC)
)

private object HelloPath {
    val test = PathParser().parsePathString("M289.2,572.1c-33,19.2 -73.3,28.8 -120.7,28.8h-57.6v-28.8H82.1c-3.9,0 -7.3,-3.5 -10.1,-10.6c-2.9,-7.1 -4.3,-15.5 -4.3,-25.4c0,-7.2 1.1,-14.6 3.2,-22.1c-17.4,-0.6 -31.9,-2.2 -43.5,-4.7C15.8,506.7 10,503.6 10,500s5.8,-6.7 17.3,-9.2c11.6,-2.6 26,-4.1 43.5,-4.7c-2.1,-7.5 -3.2,-14.9 -3.2,-22.1c0,-9.9 1.4,-18.4 4.3,-25.4c2.9,-7.1 6.2,-10.6 10.1,-10.6h28.8v-28.8h57.6c47.4,0 87.7,9.6 120.7,28.8h501.3c12.6,2.1 28.6,4.8 48,8.1c19.4,3.3 31.5,5.4 36.3,6.3c26.7,4.5 49.2,10.6 67.6,18.2c18.3,7.7 30.8,14.8 37.6,21.4c6.8,6.6 10.1,12.6 10.1,18s-3.4,11.4 -10.1,18c-6.8,6.6 -19.3,13.7 -37.6,21.4c-18.3,7.7 -40.8,13.7 -67.6,18.2c-4.8,0.9 -16.9,3 -36.3,6.3c-19.4,3.3 -35.4,6 -48,8.1H289.2L289.2,572.1zM793.2,458.6c15.9,10.8 23.9,24.6 23.9,41.4s-8,30.6 -23.9,41.4l36.5,13.5c20.4,-14.4 30.6,-32.7 30.6,-54.9s-10.2,-40.5 -30.6,-54.9L793.2,458.6zM291.5,579.3h457.1c-65.2,11.4 -133.6,23.4 -205.4,36c-17.1,0 -34.1,3.6 -50.9,10.8c-16.8,7.2 -29.3,14.4 -37.4,21.6l-12.6,10.8L312.6,788.2c-7.8,7.8 -18.4,14.6 -31.8,20.3c-13.4,5.7 -26.8,8.6 -40.3,8.6h-43.2l-41.9,-209h13.1C215.7,608.1 256.7,598.5 291.5,579.3L291.5,579.3zM168.5,391.9h-13.1l41.9,-209h43.2c13.8,0 27.3,2.9 40.5,8.6c13.2,5.7 23.7,12.5 31.5,20.3l129.7,129.7c1.2,1.2 2.9,2.8 5,4.7c2.1,2 6.7,5.4 13.7,10.4c7.1,5 14.3,9.3 21.8,13.1c7.5,3.8 16.7,7.2 27.7,10.4s21.8,4.7 32.7,4.7l205.4,36H291.5C256.7,401.5 215.7,391.9 168.5,391.9L168.5,391.9z")
    val path = PathParser().parsePathString(
        "M13.63 248.31C13.63 248.31 51.84 206.67 84.21 169.31C140" +
                ".84 103.97 202.79 27.66 150.14 14.88C131.01 10.23 116.36 29.88 107.26 45.33C69.7 108.92 58.03 214.33 57.54 302.57C67.75 271.83 104.43 190.85 140.18 193.08C181.47 195.65 145.26 257.57 154.53 284.39C168.85 322.18 208.22 292.83 229.98 277.45C265.92 252.03 288.98 231.22 288.98 200.45C288.98 161.55 235.29 174.02 223.3 205.14C213.93 229.44 214.3 265.89 229.3 284.14C247.49 306.28 287.67 309.93 312.18 288.46C337 266.71 354.66 234.56 368.68 213.03C403.92 158.87 464.36 86.15 449.06 30.03C446.98 22.4 440.36 16.57 432.46 16.26C393.62 14.75 381.84 99.18 375.35 129.31C368.78 159.83 345.17 261.31 373.11 293.06C404.43 328.58 446.29 262.4 464.66 231.67C468.66 225.31 472.59 218.43 476.08 213.07C511.33 158.91 571.77 86.19 556.46 30.07C554.39 22.44 547.77 16.61 539.87 16.3C501.03 14.79 489.25 99.22 482.76 129.35C476.18 159.87 452.58 261.35 480.52 293.1C511.83 328.62 562.4 265.53 572.64 232.86C587.34 185.92 620.94 171.58 660.91 180.29C616 166.66 580.86 199.67 572.64 233.16C566.81 256.93 573.52 282.16 599.25 295.77C668.54 332.41 742.8 211.69 660.91 180.29C643.67 181.89 636.15 204.77 643.29 227.78C654.29 263.97 704.29 268.27 733.08 256"
    )
    val riggarooPath = PathParser().parsePathString(
        """	
        M320 448.5C308.333 483 288.5 563.4 302.5 609C320 666 417.981 379.805 438.5 448.5C461.5 525.5 424 552.5 471 552.5C508.6 552.5 539.333 478.167 550 443.5C536 504.667 500 642.112 550 632C594.5 623 636.5 600.5 648.5 552.5C654.5 506.5 691.5 468.5 751.5 463.5C699.051 488 658 497 648.5 580.5C639 664 740.701 626.877 756.5 592C783 533.5 778.5 502.5 783 463.5C773.5 567.5 756.5 740 709 817C676.667 869.413 629 862.5 620 834C611 805.5 648.5 726.5 737.5 669.5C778.403 643.304 836.5 592 873.5 580.5C891 491 922.5 479.5 983.5 463.5C935.5 484.5 888.5 516 881 580.5C873.5 645 935 648 969 606C996.2 572.4 1010 497 1013.5 463.5C1003.67 564.333 976.6 776.2 947 817C910 868 842.766 870.039 846 834C853 756 924.5 683.5 977 660.5C1029.5 637.5 1086.5 580 1108.5 567.5C1130.5 555 1111.5 463.5 1206.5 463.5C1206.5 469.5 1140.5 456 1122 567.5C1106.57 660.5 1194.5 632 1206.5 600C1220.19 563.506 1248.5 455.5 1248.5 455.5C1248.5 455.5 1232.5 583.5 1248.5 621C1264.5 658.5 1349.5 603.5 1354 567.5C1358.5 531.5 1370.5 455.5 1370.5 455.5C1370.5 455.5 1333.5 633 1360 628C1386.5 623 1432.5 416.5 1480 469.5C1527.5 522.5 1474.5 584 1525 552C1582.05 515.851 1581 438 1660 442.5C1626.5 457.5 1564.08 465.399 1588 578C1608.5 674.5 1713 627.126 1713 547C1713 482.5 1696.5 470.5 1679 442.5C1679 442.5 1776.31 558.119 1798 525C1821.58 489 1832.5 435.5 1905 448.5M1905 448.5C1877.5 447 1817.9 456.7 1809.5 529.5C1799 620.5 1828.5 644.5 1872 630.5C1915.5 616.5 1936.5 569.5 1938 535C1939.5 500.5 1926 455.5 1905 448.5Z	
    """.trimIndent()
    )
}

private fun interpolateColors(
    progress: Float,
    colorsInput: List<Color>,
): Color {
    if (progress == 1f) return colorsInput.last()

    val scaledProgress = progress * (colorsInput.size - 1)
    val oldColor = colorsInput[scaledProgress.toInt()]
    val newColor = colorsInput[(scaledProgress + 1f).toInt()]
    val newScaledAnimationValue = scaledProgress - floor(scaledProgress)
    return lerp(start = oldColor, stop = newColor, fraction = newScaledAnimationValue)
}