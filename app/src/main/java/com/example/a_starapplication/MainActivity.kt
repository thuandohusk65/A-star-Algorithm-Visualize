package com.example.a_starapplication

import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a_starapplication.a_star.*

import com.example.a_starapplication.ui.theme.AstarApplicationTheme
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    private var screenHeight: Int = 0
    private var screenWidth: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            screenHeight = Constant.HEIGHT_SCREEN
            screenWidth = Constant.WIDTH_SCREEN
            var hashNode by remember { mutableStateOf(HashMap<Int, Node>()) }
            var isRunning by remember { mutableStateOf(false) }
            var countUpdate by remember { mutableStateOf(0) }
            val graph = Graph()
            graph.addNode(Node(caculateWight(0.0), caculateHeight(0.0)))
            graph.addNode(Node(caculateWight(3.0), caculateHeight(-1.73)))
            graph.addNode(Node(caculateWight(4.0), caculateHeight(0.0)))

            graph.addEdge(0, 1, 5.0)
            graph.addEdge(1, 2, 4.0)
            graph.addEdge(0, 2, 12.0)

            LaunchedEffect(key1 = true) {
                val copy = hashMapOf<Int, Node>()
                copy.putAll(graph.nodes)
                hashNode = copy
            }
            AstarApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {


                    Box(modifier = Modifier.fillMaxSize()) {
                        GraphSection(hashNode, countUpdate)
                        ShowCoordinateAxis(
                            heightScreen = screenHeight.toFloat(),
                            widthScreen = screenWidth.toFloat()
                        )
                        if (isRunning) {
                            graph.aStar(startNodeId = 0, targetNodeId = 2)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                        ) {
                            var x by remember { mutableStateOf("") }
                            var y by remember { mutableStateOf("") }
                            var start by remember { mutableStateOf("") }
                            var end by remember { mutableStateOf("") }
                            var distance by remember { mutableStateOf("") }
                            Button(onClick = {
                                val node = Node(
                                    caculateWight(x.toDouble()),
                                    caculateHeight(y.toDouble())
                                )
                                isRunning = false
                                graph.addNode(node)
                                val copy = hashMapOf<Int, Node>()
                                copy.putAll(graph.nodes)
                                hashNode = copy
                            }) {
                            }

                            TextField(
                                value = x, onValueChange = {
                                    x = it
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .width(50.dp)
                            )
                            TextField(
                                value = y, onValueChange = {
                                    y = it
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .padding(end = 10.dp)
                                    .width(50.dp)
                            )

                            Button(onClick = {
                                graph.addEdge(
                                    start.toInt(),
                                    end.toInt(),
                                    distance = distance.toDouble()
                                )
//                                listEdge = listOf()
                                isRunning = false
//                                for ((_, node) in graph.nodes) {
//                                    for ((neighbor, distance) in node.neighbors) {
//                                        listEdge += Edge(node, neighbor, distance)
//                                    }
//                                }
                                countUpdate += 1
                            }) {
                                Text(text = "add Egde")
                            }
                            TextField(
                                value = start, onValueChange = {
                                    start = it
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .width(50.dp)
                            )
                            TextField(
                                value = end,
                                onValueChange = {
                                    end = it
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.width(50.dp)
                            )
                            TextField(
                                value =
                                distance, onValueChange = {
                                    distance = it
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .width(50.dp)
                            )

                            Button(onClick = { isRunning = true }) {
                                Text(text = "Run")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun caculateWight(x: Double) = screenHeight / 2 + x * (screenHeight / 30)
    private fun caculateHeight(y: Double) = screenWidth / 2 - y * (screenWidth / 20)
}

@Composable
fun TurnOnNode(node: Node, delay: Int = 300) {
    var colorState by remember {
        mutableStateOf(
//            Color(
//                node.color.red.toFloat() + 50f,
//                node.color.green.toFloat() + 50f,
//                node.color.blue.toFloat() + 50f,
//                1f
//            )
        Color.Transparent
        )
    }

    var color = animateColorAsState(
        targetValue = colorState,
        animationSpec = tween(
            durationMillis = if(colorState == Color.Yellow) 0 else 1500
        )
    )

    LaunchedEffect(key1 = true) {
        delay(delay.toLong())
        colorState = Color.Yellow
        colorState = Color.Transparent
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = color.value,
            center = Offset(node.x.toFloat(), node.y.toFloat()),
            radius = 55f
        )
    }
}

@Composable
fun EdgeAnimation(start: Node, end: Node, delay: Int = 300) {
    var animationPlayed by remember { mutableStateOf(false) }
    val x = animateFloatAsState(
        targetValue = if (animationPlayed) (end.x - start.x).toFloat() else 0f,
        animationSpec = tween(
            durationMillis = 3000,
            delayMillis = delay
        )
    )
    val y = animateFloatAsState(
        targetValue = if (animationPlayed) (end.y - start.y).toFloat() else 0f,
        animationSpec = tween(
            durationMillis = 3000,
            delayMillis = delay
        )
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Canvas(modifier = Modifier.fillMaxSize()) {

        drawLine(
            color = Color.Yellow,
            start = Offset(start.x.toFloat(), start.y.toFloat()),
            end = Offset(start.x.toFloat() + x.value, start.y.toFloat() + y.value),
            strokeWidth = 1.dp.toPx()
        )
    }
}

@Composable
fun GraphSection(hashNode: HashMap<Int, Node>, count: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        for ((_, node) in hashNode) {
            drawCircle(
                color = Color(node.color),
                center = Offset(node.x.toFloat(), node.y.toFloat()),
                radius = 50f
            )

            for ((neighbor, _) in node.neighbors) {
                drawLine(
                    color = Color.Black,
                    start = Offset(node.x.toFloat(), node.y.toFloat()),
                    end = Offset(neighbor.x.toFloat(), neighbor.y.toFloat()),
                    strokeWidth = 1.dp.toPx()
                )

            }
        }

//        for (edge in listEdge) {
//            drawLine(
//                color = Color.Black,
//                start = Offset(
//                    edge.start.x.toFloat(),
//                    edge.start.y.toFloat(),
//                ),
//                end = Offset(
//                    edge.start.x.toFloat(),
//                    edge.start.y.toFloat(),
//                ),
//                strokeWidth = 1.dp.toPx()
//            )
//        }
    }

}

@Composable
fun ShowCoordinateAxis(
    heightScreen: Float,
    widthScreen: Float,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {

        //Oy
        drawLine(
            color = Color.Black,
            start = Offset(heightScreen / 2, 0f),
            end = Offset(heightScreen / 2, widthScreen),
            strokeWidth = 1.dp.toPx()
        )

        //Ox
        drawLine(
            color = Color.Black,
            start = Offset(0f, widthScreen / 2),
            end = Offset(heightScreen, widthScreen / 2),
            strokeWidth = 1.dp.toPx()
        )
        val unitHeight = heightScreen / 30f
        val unitWidth = widthScreen / 20f
        for (i in 0..20) {

            drawLine(
                color = if (i != 10) Color.LightGray else Color.Transparent,
                start = Offset(heightScreen / 2 - 20f, i * unitWidth),
                end = Offset(heightScreen / 2 + 20f, i * unitWidth),
                strokeWidth = 1.dp.toPx()
            )

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    " ${-i + 10}",
                    heightScreen / 2 + 40f,
                    i * unitWidth,
                    Paint().apply {
                        textSize = 10.dp.toPx()
//                        textAlign = Paint.Align.CENTER
                    }
                )
            }
        }
        for (i in 0..30) {

            drawLine(
                color = if (i != 15) Color.LightGray else Color.Transparent,
                start = Offset(i * unitHeight, widthScreen / 2 + 20f),
                end = Offset(i * unitHeight, widthScreen / 2 - 20f),
                strokeWidth = 1.dp.toPx()
            )

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    " ${i - 15}",
                    i * unitHeight,
                    widthScreen / 2 + 40f,
                    Paint().apply {
                        textSize = 10.dp.toPx()
//                        textAlign = Paint.Align.CENTER
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AstarApplicationTheme {
    }
}