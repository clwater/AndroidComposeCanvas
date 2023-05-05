package com.clwater.compose_canvas.bezier

import androidx.compose.runtime.MutableState

data class BezierPoint(var x: MutableState<Float>, var y: MutableState<Float>, var deep: Int, var name: String, var color: Long = 0x7F000000)
