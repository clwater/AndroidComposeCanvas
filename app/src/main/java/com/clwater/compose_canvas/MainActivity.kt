package com.clwater.compose_canvas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.clwater.compose_canvas.bezier.BezierActivity
import com.clwater.compose_canvas.clap.ClapActivity
import com.clwater.compose_canvas.shape.ShapeActivity
import com.clwater.compose_canvas.sun_moon.Canvas1Activity
import com.clwater.compose_canvas.tree.TreeActivity
import com.clwater.compose_canvas.ui.theme.AndroidComposeCanvasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//        ShapeActivity.start(this@MainActivity)
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
                    ) {
                        Row(modifier = Modifier.height(100.dp)) {
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically),
                                onClick = { Canvas1Activity.start(this@MainActivity) }) {
                                Text(text = "Sun Moon")
                            }
                            Image(
                                modifier = Modifier.weight(2f),
                                painter = painterResource(id = R.drawable.sun_moon),
                                contentDescription = ""
                            )
                        }

                        Spacer(
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth()
                                .background(Color.Gray)
                        )

                        Row(modifier = Modifier.height(100.dp)) {
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically),
                                onClick = { BezierActivity.start(this@MainActivity) }) {
                                Text(text = "Bezier")
                            }
                            Image(
                                modifier = Modifier.weight(2f),
                                painter = painterResource(id = R.drawable.bezier),
                                contentDescription = ""
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth()
                                .background(Color.Gray)
                        )

                        Row(modifier = Modifier.height(100.dp)) {
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically),
                                onClick = { ClapActivity.start(this@MainActivity) }) {
                                Text(text = "Clap")
                            }
                            Image(
                                modifier = Modifier
                                    .weight(2f)
                                    .align(Alignment.CenterVertically),
                                painter = painterResource(id = R.drawable.icon_hand_fill),
                                contentDescription = ""
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth()
                                .background(Color.Gray)
                        )

                        Row(modifier = Modifier.height(100.dp)) {
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically),
                                onClick = { TreeActivity.start(this@MainActivity) }) {
                                Text(text = "Tree")
                            }
                            Image(
                                modifier = Modifier.weight(2f),
                                painter = painterResource(id = R.drawable.tree),
                                contentDescription = ""
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth()
                                .background(Color.Gray)
                        )

                        Row(modifier = Modifier.height(100.dp)) {
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically),
                                onClick = { ShapeActivity.start(this@MainActivity) }) {
                                Text(text = "Shape")
                            }
                            Image(
                                modifier = Modifier.weight(2f),
                                painter = painterResource(id = R.drawable.shape),
                                contentDescription = ""
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth()
                                .background(Color.Gray)
                        )
                    }
                }
            }
        }
    }
}
