package com.clwater.compose_canvas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.clwater.compose_canvas.bezier.BezierActivity
import com.clwater.compose_canvas.clap.ClapActivity
import com.clwater.compose_canvas.shape.ShapeActivity
import com.clwater.compose_canvas.sun_moon.Canvas1Activity
import com.clwater.compose_canvas.tree.TreeActivity
import com.clwater.compose_canvas.ui.theme.AndroidComposeCanvasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ShapeActivity.start(this@MainActivity)
        super.onCreate(savedInstanceState)
        setContent {
            AndroidComposeCanvasTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = { Canvas1Activity.start(this@MainActivity) }) {
                            Text(text = "Sun Moon")
                        }

                        Button(onClick = { BezierActivity.start(this@MainActivity) }) {
                            Text(text = "Bezier")
                        }
                        Button(onClick = { ClapActivity.start(this@MainActivity) }) {
                            Text(text = "Clap")
                        }
                        Button(onClick = { TreeActivity.start(this@MainActivity) }) {
                            Text(text = "Tree")
                        }
                        Button(onClick = { ShapeActivity.start(this@MainActivity) }) {
                            Text(text ="Shape")
                        }
                    }
                }
            }
        }
    }
}
