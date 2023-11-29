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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.clwater.compose_canvas.ui.theme.AndroidComposeCanvasTheme
import kotlin.random.Random


enum class TreeType {
    TREE,
    FLOWER,
    FRUIT,
}

data class TreeNode(
    var level: Int = 0,
    var range: Float = 0f,
    var type: TreeType = TreeType.TREE,
    var child: List<TreeNode> = listOf(),
)


class TreeActivity : ComponentActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, TreeActivity::class.java))
        }

        val cloudColor_1 =  Color(0xFFF5F5F5)
        val cloudColor_2 =  Color(0xFFF5F5F5)
        val skyColor =  Color(0xFF9dbeb7)
        val landColor =  Color(0xFF2a574d)
    }

    private var mBaseCircle = 0.dp

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

    fun genNewTrees(seed: Int){

    }

    fun genNewTree(deep: Int) : TreeNode{
        val treeNode = TreeNode()

        var currentType = TreeType.TREE
        if (deep == 8){
            currentType  = TreeType.FLOWER
        }else if (deep > 5){
            if (Random(System.currentTimeMillis()).nextInt(9) > (9 - deep)){
                currentType = TreeType.FLOWER
            }
        }

        if (currentType == TreeType.FLOWER){
            if (Random(System.currentTimeMillis()).nextInt(10) > 8){
                currentType = TreeType.FRUIT
            }
        }



        treeNode.level = deep
        treeNode.type = currentType


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
                    seed = Random(System.currentTimeMillis()).nextInt(1000)
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
            Cloud_2()
            Cloud_1()
            Tree(seed)
            Text(text = "$seed")



        }

    }

    @Composable
    fun Tree(seed: Int){
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


        Canvas(modifier = Modifier
            .width(mBaseCircle)
            .height(mBaseCircle)
            .offset(
                x = mBaseCircle / 2f * offset,
                y = -mBaseCircle / 6f,
            )
            .alpha(0.8f)
            ,
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
                center = Offset(x = center.x - size.width / 20f, y = center.y +  size.height / 40f),
            )
            drawCircle(
                color = cloudColor_1,
                radius = size.minDimension / 8f,
                center = Offset(x = center.x +  size.width / 10f, y = center.y + size.height / 40f),
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


        Canvas(modifier = Modifier
            .width(mBaseCircle)
            .height(mBaseCircle)
            .offset(
                x = mBaseCircle / 2f * offset,
                y = -mBaseCircle / 3f,
            )
            .alpha(0.8f)
            ,
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
                center = Offset(x = center.x - size.width / 5f, y = center.y +  size.height / 40f),
            )
            drawCircle(
                color = cloudColor_1,
                radius = size.minDimension / 5f,
                center = Offset(x = center.x +  size.width / 10f, y = center.y + size.height / 40f),
            )
        }
    }

    @Composable
    fun TreeLand() {
        Canvas(
            modifier = Modifier
                .width(mBaseCircle)
                .height(mBaseCircle)
                .offset(y = mBaseCircle / 4f * 3)
            ,
        ) {
            drawCircle(
                color = landColor,
                radius = size.minDimension / 2f)
        }
    }

}