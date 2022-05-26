package com.example.a_starapplication.a_star

import android.graphics.Color
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.pow
import kotlin.math.sqrt

class Node(var x: Double, var y: Double) : Comparable<Node> {
    private val convertX = (this.x - Constant.HEIGHT_SCREEN / 2) / (Constant.HEIGHT_SCREEN / 30)
    private val convertY = (this.y - Constant.WIDTH_SCREEN / 2) / (Constant.WIDTH_SCREEN / 20)
    var f = Double.MAX_VALUE
    var g = Double.MAX_VALUE
    var parent: Node?
    var id: Int
    var neighbors: HashMap<Node, Double>
    var color: Int =
        Color.argb(255, Random().nextInt(127), Random().nextInt(127), Random().nextInt(127))

    companion object {
        private var idAutoIncreasing = 0
    }

    init {
        this.id = idAutoIncreasing++
        parent = null
        neighbors = HashMap()
    }

    fun addEdge(target: Node, distance: Double) {
        if (distance > 0) {
            neighbors[target] = distance
        }
    }

    fun removeEdge(target: Node) {
        neighbors.remove(target)
    }

    fun calculateHeuristic(target: Node): Double {
        return sqrt((convertX - target.convertX).pow(2) + (convertY - target.convertY).pow(2))
    }

    override fun compareTo(other: Node): Int {
        return (this.f - other.f).toInt()
    }
}