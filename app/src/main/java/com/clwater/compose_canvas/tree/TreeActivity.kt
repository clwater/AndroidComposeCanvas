package com.clwater.compose_canvas.tree

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RadialGradient
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.clwater.compose_canvas.ui.theme.AndroidComposeCanvasTheme
import java.util.ArrayDeque
import java.util.Queue
import kotlin.random.Random


enum class TreeType {
    TREE,
    FLOWER,
    FRUIT,
}

data class TreeNode(
    var deep: Int = 0,
    var angle: Float = 0f,
    var type: TreeType = TreeType.TREE,
    var child: List<TreeNode> = listOf(),
    var startOffset: Offset = Offset(0f, 0f)
)


class TreeActivity : ComponentActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, TreeActivity::class.java))
        }

        val cloudColor_1 = Color(0xFFF5F5F5)
        val cloudColor_2 = Color(0xFFF5F5F5)
        val skyColor = Color(0xFF9dbeb7)
        val landColor = Color(0xFF2a574d)
        val treeColor = Color(0xFF412e1f)
        val flowerColor = Color(0xFFFFFFFF)
        val fruitColor = Color(0xFFe66e4a)
        val fruitColorEnd = Color(0x1AE66E4A)
        const val maxDeep = 8

        val deepLengthPer = mutableMapOf(
            1 to 7f / 9f,
            2 to 7f / 18f,
            3 to 1f / 3f,
            4 to 1f / 3f,
            5 to 1f / 3f,
            6 to 1f / 3f,
            7 to 1f / 3f,
            8 to 1f / 3f,

            )
    }

    private var mBaseCircle = 0.dp
    private lateinit var random: Random


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidComposeCanvasTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TreeLayout()
                }
            }
        }
    }

    fun genNewTrees(seed: Int): TreeNode {
        random = Random(seed)
        val treeNode = TreeNode()
        treeNode.angle = 0f
        treeNode.deep = 0
        treeNode.type = TreeType.TREE
        treeNode.child += genNewTree(1)
        return treeNode
    }

    private fun genNewTree(deep: Int): TreeNode {
        val treeNode = TreeNode()

        var currentType = TreeType.TREE
        if (deep == maxDeep - 1) {
            currentType = TreeType.FLOWER
        } else if (deep > maxDeep - 3) {
            if (random.nextInt(maxDeep) > (maxDeep - deep)) {
                currentType = TreeType.FLOWER
            }
        }

        if (currentType == TreeType.FLOWER) {
            if (random.nextInt(maxDeep * maxDeep * maxDeep) < maxDeep) {
                currentType = TreeType.FRUIT
            }
        }

        treeNode.deep = deep
        treeNode.type = currentType

        val angleRange = 90 + deep * (180 / maxDeep)
        treeNode.angle = -angleRange / 2f + random.nextInt(angleRange)
        if (currentType == TreeType.TREE) {
            var childCount = random.nextInt(deep + 1) + 1
            if (childCount > 3) {
                childCount = 3
            }
            for (i in 0 until childCount) {
                treeNode.child += genNewTree(deep + 1)
            }
        }


        return treeNode
    }

    @Composable
    fun TreeLayout() {
        with(LocalDensity.current) {
            mBaseCircle = resources.displayMetrics.widthPixels.toFloat().toDp() * 0.9f
        }

        var seed by remember {
            mutableStateOf(-1)
        }

        random = Random(seed)

        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TreeCanvas(seed)
            }

            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = {
                    seed = random.nextInt(1000)
                }) {
                    Text(
                        text = "tree",
                    )
                }
            }


        }


    }

    @Composable
    fun TreeCanvas(seed: Int) {
        Log.d("gzb", "mScreenWidth: $mBaseCircle")
        Box(
            modifier = Modifier
                .width(mBaseCircle)
                .height(mBaseCircle)
                .clip(CircleShape)
                .background(skyColor),
            contentAlignment = Alignment.Center
        ) {
            TreeLand()
//            Cloud_2()
//            Cloud_1()
            Tree(seed)
            Text(text = "$seed")


        }

    }

    @Composable
    fun Tree(seed: Int) {
        val infiniteTransition = rememberInfiniteTransition()
        val offset by infiniteTransition.animateFloat(
            initialValue = -0.8f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 5007,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
        )

        val tree = genNewTrees(seed)


        val baseTreeLength = mBaseCircle / 4.5f

        Canvas(
            modifier = Modifier
                .width(mBaseCircle)
                .height(mBaseCircle)
                .offset(mBaseCircle / 2f, mBaseCircle),

            ) {

            drawLine(
                color = treeColor,
                start = Offset(x = 0f, y = -mBaseCircle.toPx() / 20f),
                end = Offset(0f, -baseTreeLength.toPx() - mBaseCircle.toPx() / 20f),
                strokeWidth = 10f,
            )
            val treeQueue: Queue<TreeNode> = ArrayDeque()
            val flowerQueue: Queue<TreeNode> = ArrayDeque()
            val fruitQueue: Queue<TreeNode> = ArrayDeque()



            for (treeNode in tree.child) {
                treeNode.startOffset = Offset(0f, -baseTreeLength.toPx() - mBaseCircle.toPx() / 20f)
                treeQueue.offer(treeNode)
            }

            while (treeQueue.isNotEmpty()) {
                val treeNode = treeQueue.poll() ?: break
                val angle = treeNode.angle
                val deep = treeNode.deep
                val type = treeNode.type


                if (type == TreeType.TREE) {

                    Log.d("gzb", "deep: $deep, angle: $angle")


                    val _deepLengthPer = deepLengthPer[deep]
                    var currentLength =
                        baseTreeLength * _deepLengthPer!! + -baseTreeLength * _deepLengthPer / 4f + random.nextInt(
                            (baseTreeLength * _deepLengthPer / 2f).toPx().toInt()
                        ).toDp()
                    if (type != TreeType.TREE) {
                        currentLength = currentLength / 3f * 2
                    }
                    val treeWidth = 10 - deep

                    val startOffset = treeNode.startOffset
                    val currentEnd = Offset(
                        x = startOffset.x + currentLength.toPx() * Math.sin(Math.toRadians(angle.toDouble()))
                            .toFloat(),
                        y = startOffset.y - currentLength.toPx() * Math.cos(Math.toRadians(angle.toDouble()))
                            .toFloat(),
                    )

                    drawLine(
                        color = treeColor,
                        start = startOffset,
                        end = currentEnd,
                        strokeWidth = treeWidth.toFloat(),
                    )
                    for (treeNode: TreeNode in treeNode.child) {
                        treeNode.startOffset = currentEnd
                        treeQueue.offer(treeNode)
                    }
                }


                if (type == TreeType.FLOWER) {
                    flowerQueue.offer(treeNode)
                } else if (type == TreeType.FRUIT) {
                    fruitQueue.offer(treeNode)
                }
            }

            while (flowerQueue.isNotEmpty()) {
                val treeNode = flowerQueue.poll() ?: break
                drawCircle(
                    color = flowerColor,
                    radius = 10f,
                    center = treeNode.startOffset,
                )
            }

            while (fruitQueue.isNotEmpty()) {
                val treeNode = fruitQueue.poll() ?: break
                drawCircle(
                    brush = Brush.radialGradient(
                        0.0f to fruitColor,
                        0.5f to fruitColor,
                        1f to fruitColorEnd,
                        center = treeNode.startOffset,
                        radius = 20f

                    ),
                    center = treeNode.startOffset,
                    radius = 20f
                )

            }

        }

    }

    @Composable
    fun CanvasTree() {

    }

    @Composable
    fun Cloud_1() {
        val infiniteTransition = rememberInfiniteTransition()
        val offset by infiniteTransition.animateFloat(
            initialValue = -0.7f,
            targetValue = 0.7f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 4003,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
        )


        Canvas(
            modifier = Modifier
                .width(mBaseCircle)
                .height(mBaseCircle)
                .offset(
                    x = mBaseCircle / 2f * offset,
                    y = -mBaseCircle / 6f,
                )
                .alpha(0.8f),
        )
        {
            drawRoundRect(
                color = cloudColor_1,
                size = Size(width = size.width / 7f * 4f, height = size.height / 4f),
                cornerRadius = CornerRadius(size.minDimension / 2f),
                topLeft = Offset(x = center.x - size.width / 4f, y = center.y),
            )
            drawCircle(
                color = cloudColor_1,
                radius = size.minDimension / 10f,
                center = Offset(x = center.x - size.width / 20f, y = center.y + size.height / 40f),
            )
            drawCircle(
                color = cloudColor_1,
                radius = size.minDimension / 8f,
                center = Offset(x = center.x + size.width / 10f, y = center.y + size.height / 40f),
            )
        }
    }

    @Composable
    fun Cloud_2() {
        val infiniteTransition = rememberInfiniteTransition()
        val offset by infiniteTransition.animateFloat(
            initialValue = -0.8f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 5007,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
        )


        Canvas(
            modifier = Modifier
                .width(mBaseCircle)
                .height(mBaseCircle)
                .offset(
                    x = mBaseCircle / 2f * offset,
                    y = -mBaseCircle / 3f,
                )
                .alpha(0.8f),
        )
        {
            drawRoundRect(
                color = cloudColor_1,
                size = Size(width = size.width / 7f * 6f, height = size.height / 4f * 1.25f),
                cornerRadius = CornerRadius(size.minDimension / 2f),
                topLeft = Offset(x = center.x - size.width / 2f, y = center.y),
            )
            drawCircle(
                color = cloudColor_1,
                radius = size.minDimension / 6f,
                center = Offset(x = center.x - size.width / 5f, y = center.y + size.height / 40f),
            )
            drawCircle(
                color = cloudColor_1,
                radius = size.minDimension / 5f,
                center = Offset(x = center.x + size.width / 10f, y = center.y + size.height / 40f),
            )
        }
    }

    @Composable
    fun TreeLand() {
        Canvas(
            modifier = Modifier
                .width(mBaseCircle)
                .height(mBaseCircle)
                .offset(y = mBaseCircle / 4f * 3),
        ) {
            drawCircle(
                color = landColor,
                radius = size.minDimension / 2f
            )
        }
    }

}