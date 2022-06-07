package com.example.a_starapplication.a_star

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class Graph {
    var close: PriorityQueue<Node> = PriorityQueue()
    var open: PriorityQueue<Node> = PriorityQueue()
    var nodes: HashMap<Int, Node> = HashMap()

    fun addNode(node: Node) {
        nodes[node.id] = node
    }

    fun addEdge(nodeId1: Int, nodeId2: Int, distance: Double) {
        if (nodes.containsKey(nodeId1) && nodes.containsKey(nodeId2)) {
            nodes[nodeId1]!!.addEdge(nodes[nodeId2]!!, distance)
            nodes[nodeId2]!!.addEdge(nodes[nodeId1]!!, distance)
        }
    }

    fun removeEdge(nodeId1: Int, nodeId2: Int) {
        if (nodes.containsKey(nodeId1) && nodes.containsKey(nodeId2)) {
            nodes[nodeId1]!!.removeEdge(nodes[nodeId2]!!)
            nodes[nodeId2]!!.removeEdge(nodes[nodeId1]!!)
        }
    }

    fun removeNode(nodeId: Int) {
        if (nodes.containsKey(nodeId)) {
            val temp = nodes[nodeId]
            for ((_, node) in nodes) {
                node.removeEdge(temp!!)
            }
        }
        nodes.remove(nodeId)
    }

    @Composable
    fun aStar(startNodeId: Int, targetNodeId: Int, speed: Double) {
        close.clear()
        open.clear()
        for ((_, node) in nodes) {
            node.g = 0.0
            node.f = 0.0
            node.parent = null
        }
        if (nodes.containsKey(startNodeId) && nodes.containsKey(targetNodeId)) {
            val startNode = nodes[startNodeId]
            val targetNode = nodes[targetNodeId]
            startNode!!.g = 0.0
            startNode.f = startNode.calculateHeuristic(targetNode!!)
            open.add(startNode)
//            openRemember = open
            var currentNode = startNode
            var countDelay = 0
            val tempOpen = PriorityQueue<Node>()
            tempOpen.add(currentNode)
            CloseAndOpenQueueSection(
                open = tempOpen,
                close = PriorityQueue(),
                (countDelay + 500 * speed).toInt(),
                (1000 * speed).toInt()
            )
            countDelay += (1000 * speed).toInt()
            while (!open.isEmpty()) {
                currentNode = open.peek()
                if (currentNode != startNode) {
                    EdgeAnimation(
                        start = currentNode!!.parent!!,
                        end = currentNode,
                        delay = countDelay
                    )
                    val copyOpen = PriorityQueue<Node>()
                    copyOpen.addAll(open)
                    val copyClose = PriorityQueue<Node>()
                    copyClose.addAll(close)
                    CloseAndOpenQueueSection(
                        open = copyOpen,
                        close = copyClose,
                        (countDelay + 500 * speed).toInt(),
                        (3300 * speed).toInt()
                    )
                    countDelay += (3300 * speed).toInt()
                }
//                TurnOnNode(node = currentNode, delay = countDelay)
//                countDelay+=1500
                if (currentNode == targetNode) {
                    CloseAndOpenQueueSection(open = open, close = close, (countDelay + 500 * speed).toInt())
                    break
                }
                for ((neighbor, distance) in currentNode!!.neighbors) {
                    TurnOnNode(node = neighbor, delay = countDelay)
                    val totalWeight = currentNode.g + distance
                    if (!open.contains(neighbor) && !close.contains(neighbor)) {
                        neighbor.g = totalWeight
                        neighbor.f = totalWeight + neighbor.calculateHeuristic(targetNode)
                        neighbor.parent = currentNode
                        open.add(neighbor)
                    } else {
                        if (totalWeight < neighbor.g) {
                            neighbor.g = totalWeight
                            neighbor.f = totalWeight + neighbor.calculateHeuristic(targetNode)
                            if (close.contains(neighbor)) {
                                open.add(neighbor)
                                close.remove(neighbor)
                            }
                            neighbor.parent = currentNode
                        }
                    }
                    val copyOpen2 = PriorityQueue<Node>()
                    copyOpen2.addAll(open)
                    val copyClose2 = PriorityQueue<Node>()
                    copyClose2.addAll(close)
                    CloseAndOpenQueueSection(
                        open = copyOpen2,
                        close = copyClose2,
                        delay = (countDelay + 500 * speed).toInt(),
                        visibleTime = (1500 * speed).toInt()
                    )
                    countDelay += (1500 * speed).toInt()
                }
                open.remove(currentNode)
                close.add(currentNode)
            }
            PrintPath(targetNodeId, countDelay)
        }
    }

    @Composable
    fun PrintPath(targetId: Int, delay: Int) {
        val listStep = mutableListOf<Node>()
        var point = nodes[targetId]
        while (point != null) {
            listStep.add(point)
            point = point.parent
        }
        listStep.reverse()
        if (listStep.size >= 2) {
            var countDelay = delay + 1000
            for (i in 1 until listStep.size) {
                EdgeAnimation(
                    start = listStep[i - 1],
                    end = listStep[i],
                    delay = countDelay,
                    timeDuration = 1500,
                    color = Color.Cyan,
                    strokeWidth = 3.dp
                )
                countDelay += 1600
            }
        }
    }
}

@Composable
fun TurnOnNode(node: Node, delay: Int = 300) {
    var colorState by remember {
        mutableStateOf(
            Color.Transparent
        )
    }

    var color = animateColorAsState(
        targetValue = colorState,
        animationSpec = tween(
            durationMillis = 1500
        )
    )

    LaunchedEffect(key1 = true) {
        delay(delay.toLong())
        colorState = Color.Yellow
        delay(1500)
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
fun EdgeAnimation(
    start: Node,
    end: Node,
    delay: Int = 300,
    timeDuration: Int = 3000,
    color: Color = Color.Yellow,
    strokeWidth: Dp = 1.dp
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val x = animateFloatAsState(
        targetValue = if (animationPlayed) (end.x - start.x).toFloat() else 0f,
        animationSpec = tween(
            durationMillis = timeDuration,
            delayMillis = delay
        )
    )
    val y = animateFloatAsState(
        targetValue = if (animationPlayed) (end.y - start.y).toFloat() else 0f,
        animationSpec = tween(
            durationMillis = timeDuration,
            delayMillis = delay
        )
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Canvas(modifier = Modifier.fillMaxSize()) {

        drawLine(
            color = color,
            start = Offset(start.x.toFloat(), start.y.toFloat()),
            end = Offset(start.x.toFloat() + x.value, start.y.toFloat() + y.value),
            strokeWidth = strokeWidth.toPx()
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

            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    node.name,
                    node.x.toFloat(),
                    node.y.toFloat() + 10f,
                    Paint().apply {
                        textSize = 14.dp.toPx()
                        color = Color.White.toArgb()
                        textAlign = Paint.Align.CENTER
                    }
                )
            }

            for ((neighbor, _) in node.neighbors) {
                drawLine(
                    color = Color.Black,
                    start = Offset(node.x.toFloat(), node.y.toFloat()),
                    end = Offset(neighbor.x.toFloat(), neighbor.y.toFloat()),
                    strokeWidth = 1.dp.toPx()
                )

                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                         "${node.neighbors[neighbor]}",
                        ((node.x + neighbor.x)/2).toFloat(),
                        ((node.y + neighbor.y)/2).toFloat() + 30f,
                        Paint().apply {
                            textSize = 14.dp.toPx()
                            typeface = Typeface.DEFAULT_BOLD
                            color = Color.Red.toArgb()
                            textAlign = Paint.Align.CENTER
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CloseAndOpenQueueSection(
    open: PriorityQueue<Node>,
    close: PriorityQueue<Node>,
    delay: Int,
    visibleTime: Int? = null
) {
    Box(modifier = Modifier.fillMaxSize()) {
        var isVisible by remember { mutableStateOf(false) }
        LaunchedEffect(key1 = true) {
            delay(delay.toLong())
            isVisible = true
            if (visibleTime != null) {
                delay(visibleTime.toLong())
                isVisible = false
            }
        }
        if (isVisible) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.2f)
                    .padding(end = 5.dp)
                    .align(Alignment.TopEnd)
            ) {
                //close
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Open")
                    for (node in open) {
                        QueueItem(node = node)
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Close")
                    for (node in close) {
                        QueueItem(node = node)
                    }
                }
            }
        }
    }
}

@Composable
fun QueueItem(node: Node) {
    Box(
        modifier = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(node.color))
            .padding(5.dp)
    ) {
        Text(
            text = "${node.name} g: ${(node.g * 10.0).roundToInt() / 10.0} f: ${(node.f * 10.0).roundToInt() / 10.0}",
            color = Color.White
        )
    }
}

//fun main() {
//    val graph = Graph()
//    graph.addNode(Node(0.0, 0.0))
//    graph.addNode(Node(5.0, 4.9))
//    graph.addNode(Node(3.0, -Math.sqrt(3.0)))
//    graph.addNode(Node(4.0, 0.0))
//    graph.addEdge(0, 1, 2.0)
//    graph.addEdge(0, 2, 5.0)
//    graph.addEdge(1, 3, 1.0)
//    graph.addEdge(2, 3, 4.0)
//    graph.removeNode(2)
////    println(graph.aStar(0, 3))
//    println(graph.printPath(3))
//}