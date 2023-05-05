package com.clwater.compose_canvas.bezier

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel

class BezierViewModel : ViewModel() {
    var mIndex = mutableStateOf(3)
    var mTime = mutableStateOf(3)
    var mBezierPoints = mutableStateListOf<BezierPoint>()
    var mBezierDrawPoints = mutableStateListOf(mutableListOf<BezierPoint>())
    var mProgress = mutableStateOf(0f)
    var mBezierLinePoints = mutableStateMapOf(Pair(0f, Pair(0f, 0f)))
    var mInChange = mutableStateOf(false)
    var mInAuxiliary = mutableStateOf(true)
    var mInMore = mutableStateOf(false)

    val mIndexRange = Pair(2, 15)
    val mTimeRange = Pair(1, 10)

    // 点层级字符集
    val mCharSequence = listOf(
        "P",
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"
    )

    // 辅助线颜色集
    val mColorSequence = listOf(
        0xff000000,
        0xff1BFFF8,
        0xff17FF89,
        0xff25FF2F,
        0xffA7FF05,
        0xffFFE61E,
        0xffFF9B0C,
        0xffFF089F,
        0xffE524FF,
        0xff842BFF,
        0xff090BFF,
        0xff0982FF,
        0xffF8A1FF,
        0xffF7FFA7,
        0xffAAFFEC
    )

    fun addPoint(x: Float, y: Float) {
        val pointSize = mBezierPoints.size
        if (pointSize < mIndex.value || mInMore.value) {
            mBezierPoints.add(
                BezierPoint(
                    mutableStateOf(x),
                    mutableStateOf(y),
                    0,
                    "${mCharSequence[0]}$pointSize",
                    mColorSequence[0]
                )
            )
        }
        mBezierDrawPoints.clear()
        mBezierDrawPoints.add(mBezierPoints)
    }

    fun clear() {
        mBezierPoints.clear()
        mBezierDrawPoints.clear()
        mBezierLinePoints.clear()
        mProgress.value = 0f
    }

    fun start() {
        val process = ValueAnimator.ofFloat(0f, 1f)
        process.addUpdateListener {
            mProgress.value = it.animatedValue as Float
            calculate()
        }

        val set = AnimatorSet()
        set.play(process)
        set.duration = mTime.value * 1000L
        set.interpolator = LinearInterpolator()
        set.start()
    }

    fun calculate() {
        mBezierDrawPoints.clear()
        mBezierDrawPoints.add(mBezierPoints)
        calculateBezierPoint(0, mBezierPoints.toList())
    }

    private fun calculateBezierPoint(deep: Int, parentList: List<BezierPoint>) {
        if (parentList.size > 1) {
            val childList = mutableListOf<BezierPoint>()
            for (i in 0 until parentList.size - 1) {
                val point1 = parentList[i]
                val point2 = parentList[i + 1]
                val x = point1.x.value + (point2.x.value - point1.x.value) * mProgress.value
                val y = point1.y.value + (point2.y.value - point1.y.value) * mProgress.value
                if (parentList.size == 2) {
                    mBezierLinePoints[mProgress.value] = Pair(x, y)
                    return
                } else {
                    val point = BezierPoint(
                        mutableStateOf(x),
                        mutableStateOf(y),
                        deep + 1,
                        "${mCharSequence.getOrElse(deep + 1){"Z"}}$i",
                        mColorSequence.getOrElse(deep + 1) { 0xff000000 }
                    )
                    childList.add(point)
                }
            }
            mBezierDrawPoints.add(childList)
            calculateBezierPoint(deep + 1, childList)
        } else {
            return
        }
    }

    var bezierPoint: BezierPoint? = null
    fun pointDragStart(position: Offset) {
        if (!mInChange.value) {
            return
        }
        if (mBezierPoints.isEmpty()) {
            return
        }
        mBezierPoints.firstOrNull() {
            position.x > it.x.value - 50 && position.x < it.x.value + 50 &&
                position.y > it.y.value - 50 && position.y < it.y.value + 50
        }.let {
            bezierPoint = it
        }
    }

    fun pointDragEnd() {
        bezierPoint = null
    }

    fun pointDragProgress(drag: Offset) {
        if (!mInChange.value || bezierPoint == null) {
            return
        } else {
            bezierPoint!!.x.value += drag.x
            bezierPoint!!.y.value += drag.y
            calculate()
        }
    }

    fun changeMovePoint() {
        mInChange.value = !mInChange.value
        if (mInChange.value) {
            clearWithOutBasePoint()
            mBezierDrawPoints.add(mBezierPoints)
        }
    }

    private fun clearWithOutBasePoint() {
        mBezierDrawPoints.clear()
        mBezierLinePoints.clear()
        mProgress.value = 0f
    }

    fun changeMore() {
        clear()
        mInMore.value = !mInMore.value
        if (mInMore.value) {
            mInAuxiliary.value = false
        }
    }

    fun changeAuxiliary() {
        mInAuxiliary.value = !mInAuxiliary.value
    }
}