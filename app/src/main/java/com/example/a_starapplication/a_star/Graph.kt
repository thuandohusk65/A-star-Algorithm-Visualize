package com.example.a_starapplication.a_star

import android.os.Handler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.a_starapplication.EdgeAnimation
import com.example.a_starapplication.TurnOnNode
import kotlinx.coroutines.delay
import java.util.*

class Graph {
    var close: PriorityQueue<Node?> = PriorityQueue()
    var open: PriorityQueue<Node?> = PriorityQueue()
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
    fun aStar(startNodeId: Int, targetNodeId: Int) {
        if (nodes.containsKey(startNodeId) && nodes.containsKey(targetNodeId)) {
            val startNode = nodes[startNodeId]
            val targetNode = nodes[targetNodeId]
            startNode!!.g = 0.0
            startNode.f = startNode.calculateHeuristic(targetNode!!)
            open.add(startNode)
            var currentNode = startNode
            var countDelay = 0
            while (!open.isEmpty()) {
                currentNode = open.peek()
                if (currentNode != startNode) {
                    EdgeAnimation(currentNode!!.parent!!, currentNode,  countDelay)
                    countDelay+= 3300
                }
                TurnOnNode(node = currentNode, delay = countDelay)
                countDelay+=1500
                if (currentNode == targetNode) {
                    return
                }
                for ((neighbor, distance) in currentNode!!.neighbors) {
                    TurnOnNode(node = neighbor,delay = countDelay)
                    countDelay+= 1500
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
                }
                open.remove(currentNode)
                close.add(currentNode)
            }
        }
    }

    fun printPath(targetId: Int): String? {
        val pathStr = StringBuilder("")
        var point = nodes!![targetId]
        while (point != null) {
            pathStr.append(" " + point.id)
            point = point.parent
        }
        pathStr.reverse()
        return pathStr.toString()
    }

    override fun equals(other: Any?): Boolean {
        return false
    }
}

fun main() {
    val graph = Graph()
    graph.addNode(Node(0.0, 0.0))
    graph.addNode(Node(5.0, 4.9))
    graph.addNode(Node(3.0, -Math.sqrt(3.0)))
    graph.addNode(Node(4.0, 0.0))
    graph.addEdge(0, 1, 2.0)
    graph.addEdge(0, 2, 5.0)
    graph.addEdge(1, 3, 1.0)
    graph.addEdge(2, 3, 4.0)
    graph.removeNode(2)
//    println(graph.aStar(0, 3))
    println(graph.printPath(3))
}