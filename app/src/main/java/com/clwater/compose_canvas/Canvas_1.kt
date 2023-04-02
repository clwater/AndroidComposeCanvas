package com.clwater.compose_canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun Canvas_1() {
    Column(
        modifier = Modifier.fillMaxSize()
    ){
        Text(text = "11111111")
        Canvas(
            modifier = Modifier
                .width(500.dp)
                .height(100.dp)
                .border(10.dp, Color.Red)
                .background(Color.Blue)
                .clip(shape = RoundedCornerShape(10.dp))

            ,
            onDraw = {
                drawLine(
                    color = Color.Blue,
                    start = Offset.Zero,
                    end = Offset(size.width, size.height),
                    strokeWidth = 10f
                )
                drawCircle(
                    color = Color.Red,
                    radius = 50f,
                    center = Offset(size.width / 2, size.height / 2)
                )

            }
        )
        Text(text = "22222")


    }

}