package com.clwater.compose_canvas.tree

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.clwater.compose_canvas.ui.theme.AndroidComposeCanvasTheme
import kotlinx.coroutines.delay
import java.util.ArrayDeque
import java.util.Queue
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random


// Tree Node Type
enum class TreeType {
    TREE,
    FLOWER,
    FRUIT,
}

enum class Season {
    Spring,
    Summer,
    Autumn,
    Winter,
}

// data Class TreeNode
data class TreeNode(
    var deep: Int = 0,
    var angle: Float = 0f,
    var type: TreeType = TreeType.TREE,
    var child: List<TreeNode> = listOf(),

    var length: Dp = 0.dp,

    // Increased in a loop rather than recursively
    var startOffset: Offset = Offset(0f, 0f)
)


class TreeActivity : ComponentActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, TreeActivity::class.java))
        }

        // const Color
        val cloudColor = Color(0xFFF5F5F5)
        val treeColor = Color(0xFF412e1f)
        val flowerColor = Color(0xFFFFFFFF)
        val fruitColor = Color(0xFFe66e4a)
        val fruitColorEnd = Color(0x1AE66E4A)
        val seasonSpring = Color(0xFF7FDF69)
        val seasonSummer = Color(0xFFEE4F4F)
        val seasonAutumn = Color(0xFFE6A23C)
        val seasonWinter = Color(0xFFB8CAC6)

        val skyColorSpring = Color(0xFF69ADA3)
        val landColorSpring = Color(0xFF59C255)
        val rainColor = Color(0x99CCD5CC)
        val skyColorSummer = Color(0xFF4D59AF)
        val landColorSummer = Color(0xFF1E1F44)
        val skyColorAutumn = Color(0xFFFAC164)
        val landColorAutumn = Color(0xFF612D1C)
        val skyColorWinter = Color(0xFF9dbeb7)
        val landColorWinter = Color(0xFFE7EEEC)
    }

    private lateinit var random: Random

    private var mBaseCircle = 0.dp
    private var flowerCount = 0
    private var minLength: Float = 0.0f


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

    // Generate new Mei Tree
    private fun genNewTrees(seed: Int): TreeNode {
        random = Random(seed)
        val treeNode = TreeNode()
        treeNode.angle = 0f
        treeNode.deep = 0
        treeNode.type = TreeType.TREE
        treeNode.length = mBaseCircle / 4f

        for (i in 0 until random.nextInt(3) + 1) {
            treeNode.child += genNewTree(1, treeNode.length)
        }
        return treeNode
    }

    // recursively new tree node
    private fun genNewTree(deep: Int, length: Dp): TreeNode {
        val treeNode = TreeNode()

        treeNode.deep = deep

        if (length < minLength.dp) {
            flowerCount++
            treeNode.type = if (flowerCount % 100 == 0) {
                TreeType.FRUIT
            } else {
                TreeType.FLOWER
            }
            return treeNode
        }

        treeNode.type = TreeType.TREE

        treeNode.length = length * (random.nextInt(2) / 10f + 0.6f)
        treeNode.angle =
            (if (random.nextFloat() > 0.5f) 1f else -1f) * (random.nextInt(20 + deep * 5) + 45)
        for (i in 0 until random.nextInt(3) + 1) {
            treeNode.child += genNewTree(deep + 1, treeNode.length)
        }

        return treeNode
    }

    @Composable
    fun TreeLayout() {
        with(LocalDensity.current) {
            mBaseCircle = resources.displayMetrics.widthPixels.toFloat().toDp() * 0.9f
        }

        var season by remember {
            mutableStateOf(Season.Spring)
        }

        var seed by remember {
            mutableStateOf(-1)
        }

        random = Random(seed)
        minLength = mBaseCircle.value / 40f

        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TreeCanvas(seed, season)
            }


            Column {
                Button(onClick = {
                    seed = random.nextInt(1000)
                }) {
                    Text(
                        text = "Generate New Tree",
                    )
                }
                Column(
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Row {
                        Button(
                            onClick = {
                                season = Season.Spring
                            },
                            modifier = Modifier
                                .weight(1f),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = if (season == Season.Spring) {
                                    seasonSpring
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Spring",
                            )
                        }

                        Button(
                            onClick = {
                                season = Season.Summer
                            },
                            modifier = Modifier
                                .weight(1f),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = if (season == Season.Summer) {
                                    seasonSummer
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Summer",
                            )
                        }
                    }


                    Row {
                        Button(
                            onClick = {
                                season = Season.Autumn
                            },
                            modifier = Modifier
                                .weight(1f),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = if (season == Season.Autumn) {
                                    seasonAutumn
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Autumn",
                            )
                        }

                        Button(
                            onClick = {
                                season = Season.Winter
                            },
                            modifier = Modifier
                                .weight(1f),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = if (season == Season.Winter) {
                                    seasonWinter
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Winter",
                            )
                        }
                    }


                }

            }

        }

    }

    @Composable
    fun TreeCanvas(seed: Int, season: Season) {
        Box(
            modifier = Modifier
                .width(mBaseCircle)
                .height(mBaseCircle)
                .clip(CircleShape)
                .background(
                    when (season) {
                        Season.Spring -> skyColorSpring
                        Season.Summer -> skyColorSummer
                        Season.Autumn -> skyColorAutumn
                        Season.Winter -> skyColorWinter
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            when(season){
                Season.Spring -> {
                    SpringRain()
                    Light()
                }
                Season.Autumn -> {
                    Cloud_1()
                    Cloud_2()
                }
                else -> {}
            }
            TreeLand(season)

//            Tree(seed, season)
        }

    }

    @Composable
    fun Light() {
        var showLight by remember {
            mutableStateOf(true)
        }
//        LaunchedEffect(Unit) {
//            while (true) {
//                delay(3000)
//                showLight = true
//                delay(1000)
//                showLight = false
//            }
//        }

        if (showLight) {
            Canvas(
                modifier = Modifier
                    .width(mBaseCircle)
                    .height(mBaseCircle)
                    .offset(mBaseCircle / 2f, mBaseCircle / 2f)

            ) {

                drawCircle(color = Color.Red,
                    radius = 10f,
                    center = Offset(x = 0f, y = 0f)
                )
            }
        }

    }

    @Composable
    private fun SpringRain() {
        val infiniteTransition = rememberInfiniteTransition()
        val offset by infiniteTransition.animateFloat(
            initialValue = -1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 4000,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Restart,
            ),
        )
        val maxRains = 100
        val rainOffset = mutableMapOf<Int, Offset>()

        for(index in 0 until maxRains){
            rainOffset[index] = Offset(
                x = - 2f * mBaseCircle.value  +  4f * random.nextInt(mBaseCircle.value.toInt()),
                y = - 1f * mBaseCircle.value  +  2f * random.nextInt(mBaseCircle.value.toInt())
            )
        }

        Canvas(
            modifier = Modifier
                .width(mBaseCircle)
                .height(mBaseCircle)
                .offset(mBaseCircle / 2f, mBaseCircle / 2f)
                .rotate(10f)
                .graphicsLayer {
//                    translationY  = mBaseCircle.toPx() / 2f * offset
                }
            ,

            ) {
            for(i in -2 .. 2){
                for (j in 0 until maxRains) {
                    drawRoundRect(
                        color = rainColor,
                        size = Size(mBaseCircle.toPx() / 400f, mBaseCircle.toPx() / 20f),
                        cornerRadius = CornerRadius(size.minDimension / 2f),
                        topLeft = Offset(x = rainOffset[j]!!.x ,
                            y = mBaseCircle.value * offset + i *  mBaseCircle.value + rainOffset[j]!!.y
                        ),
                    )
                }
            }
        }
    }

    @Composable
    fun Tree(seed: Int, season: Season) {

        val tree = genNewTrees(seed)
        val baseTreeLength = mBaseCircle / 4f
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

            // Increased in a loop rather than recursively
            while (treeQueue.isNotEmpty()) {
                val treeNode = treeQueue.poll() ?: break
                val angle = treeNode.angle
                val deep = treeNode.deep
                val type = treeNode.type
                val length = treeNode.length

                if (type == TreeType.TREE) {
                    var treeWidth = 15f
                    for (i in 0..deep) {
                        treeWidth *= 0.8f
                    }

                    // calculate the position for child node
                    val startOffset = treeNode.startOffset
                    val currentEnd = Offset(
                        x = startOffset.x + length.toPx() * sin(Math.toRadians(angle.toDouble()))
                            .toFloat(),
                        y = startOffset.y - length.toPx() * cos(Math.toRadians(angle.toDouble()))
                            .toFloat(),
                    )

                    drawLine(
                        color = treeColor,
                        start = startOffset,
                        end = currentEnd,
                        strokeWidth = treeWidth,
                    )
                    treeNode.child.forEach {
                        it.startOffset = currentEnd
                        treeQueue.offer(it)
                    }
                }

                // offer the flower/fruit child to queue
                if (type == TreeType.FLOWER) {
                    flowerQueue.offer(treeNode)
                } else if (type == TreeType.FRUIT) {
                    fruitQueue.offer(treeNode)
                }
            }

            // draw flowers
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
                color = cloudColor,
                size = Size(width = size.width / 7f * 4f, height = size.height / 4f),
                cornerRadius = CornerRadius(size.minDimension / 2f),
                topLeft = Offset(x = center.x - size.width / 4f, y = center.y),
            )
            drawCircle(
                color = cloudColor,
                radius = size.minDimension / 10f,
                center = Offset(x = center.x - size.width / 20f, y = center.y + size.height / 40f),
            )
            drawCircle(
                color = cloudColor,
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
                color = cloudColor,
                size = Size(width = size.width / 7f * 6f, height = size.height / 4f * 1.25f),
                cornerRadius = CornerRadius(size.minDimension / 2f),
                topLeft = Offset(x = center.x - size.width / 2f, y = center.y),
            )
            drawCircle(
                color = cloudColor,
                radius = size.minDimension / 6f,
                center = Offset(x = center.x - size.width / 5f, y = center.y + size.height / 40f),
            )
            drawCircle(
                color = cloudColor,
                radius = size.minDimension / 5f,
                center = Offset(x = center.x + size.width / 10f, y = center.y + size.height / 40f),
            )
        }
    }

    @Composable
    fun TreeLand(season: Season) {
        Canvas(
            modifier = Modifier
                .width(mBaseCircle)
                .height(mBaseCircle)
                .offset(y = mBaseCircle / 4f * 3),
        ) {
            drawCircle(
                color = when (season) {
                    Season.Spring -> landColorSpring
                    Season.Summer -> landColorSummer
                    Season.Autumn -> landColorAutumn
                    Season.Winter -> landColorWinter
                },
                radius = size.minDimension / 2f
            )
        }
    }

}