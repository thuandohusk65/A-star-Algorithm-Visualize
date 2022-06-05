package com.example.a_starapplication

import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a_starapplication.a_star.*

import com.example.a_starapplication.ui.theme.AstarApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private var screenHeight: Int = 0
    private var screenWidth: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            screenHeight = Constant.HEIGHT_SCREEN
            screenWidth = Constant.WIDTH_SCREEN
            var startId = -1
            var targetId = -1
            var speed by remember { mutableStateOf(1.0) }
            var hashNode by remember { mutableStateOf(HashMap<Int, Node>()) }
            var isRunning by remember { mutableStateOf(false) }
            var countUpdate by remember { mutableStateOf(0) }
            var graph = Graph()
            graph.addNode(Node(caculateWight(0.0), caculateHeight(0.0)))
            graph.addNode(Node(caculateWight(5.0), caculateHeight(4.9)))
            graph.addNode(Node(caculateWight(3.0), caculateHeight(-1.73)))
            graph.addNode(Node(caculateWight(4.0), caculateHeight(0.0)))

            graph.addEdge(0, 1, 2.0)
            graph.addEdge(0, 2, 5.0)
            graph.addEdge(2, 3, 4.0)

            LaunchedEffect(key1 = true) {
                val copy = hashMapOf<Int, Node>()
                copy.putAll(graph.nodes)
                hashNode = copy
            }
            AstarApplicationTheme {
                // A surface container using the 'background' color from the theme
                val scope = rememberCoroutineScope()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        GraphSection(hashNode, countUpdate)
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                                .size(24.dp)
                                .clickable {
                                    graph = Graph()
                                    val copy = hashMapOf<Int, Node>()
                                    copy.putAll(graph.nodes)
                                    hashNode = copy
                                }
                        )
                        ShowCoordinateAxis(
                            heightScreen = screenHeight.toFloat(),
                            widthScreen = screenWidth.toFloat()
                        )
                        if (isRunning) {
                            graph.aStar(
                                startNodeId = startId,
                                targetNodeId = targetId,
                                speed = 1 / speed
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                        ) {
                            var x by remember { mutableStateOf("") }
                            var y by remember { mutableStateOf("") }
                            var start by remember { mutableStateOf("") }
                            var end by remember { mutableStateOf("") }
                            var distance by remember { mutableStateOf("") }
                            var startNameRe by remember { mutableStateOf("") }
                            var targetNameRe by remember { mutableStateOf("") }
                            Button(
                                modifier = Modifier.padding(start = 10.dp),
                                onClick = {
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
                                Text("add Node")
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
                                    nameToId(start.trim().uppercase()),
                                    nameToId(end.trim().uppercase()),
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
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .width(50.dp)
                            )
                            TextField(
                                value = end,
                                onValueChange = {
                                    end = it
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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

                            Button(onClick = {
                                if (isRunning == true) {
                                    isRunning = false
                                    scope.launch {
                                        delay(100)
                                        isRunning = true
                                    }
                                } else {
                                    isRunning = true
                                }
                            }) {
                                Text(text = "Run")
                            }

                            TextField(
                                value = startNameRe, onValueChange = {
                                    startNameRe = it
                                    startId = nameToId(it.trim().uppercase())
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .width(50.dp)
                            )
                            TextField(
                                value = targetNameRe,
                                onValueChange = {
                                    targetNameRe = it
                                    targetId = nameToId(it.trim().uppercase())
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.width(50.dp)
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Column(modifier = Modifier.padding(end = 10.dp)) {
                                Image(
                                    Icons.Default.KeyboardArrowUp, contentDescription = null,
                                    modifier = Modifier
                                        .padding(bottom = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (speed < 2) {
                                                speed += 0.25
                                            } else if (speed >= 2 && speed <= 4) {
                                                speed += 1
                                            }
                                        }
                                        .background(Color.LightGray)
                                        .size(30.dp)
                                )
                                Text(text = "${speed}", modifier = Modifier.padding(bottom = 4.dp))
                                Image(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.LightGray)
                                        .clickable {
                                            if (speed > 2) {
                                                speed -= 1
                                            } else if (speed <= 2 && speed >= 0.5) {
                                                speed -= 0.25
                                            }
                                        }
                                        .size(30.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun caculateWight(x: Double) = screenHeight / 2 + x * (screenHeight / 30)
    private fun caculateHeight(y: Double) = screenWidth / 2 - y * (screenWidth / 20)

    private fun nameToId(columnTitle: String): Int {
        var point = 0
        var id = 0
        while (point < columnTitle.length) {
            val lastCharacter = columnTitle.substring(point, point + 1).first()
            val charId = lastCharacter.toInt()
            val charToColumn = if (charId == 90) 26 else charId - 64
            id += charToColumn * Math.pow(26.0, (columnTitle.length - point - 1).toDouble()).toInt()
            point++
        }
        return id - 1
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