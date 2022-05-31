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
    lateinit var name: String

    companion object {
        private var idAutoIncreasing = 0
    }

    init {
        this.id = idAutoIncreasing++
        name = convertIdToName()
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

    private fun convertIdToName(): String{
        var num = this.id + 1
        var result = ""
        while (num > 0) {
            var surplus = num % 26
            num--
            num /= 26
            result = toChar(surplus) + result
        }
        return result
    }

    fun convertToTitle(columnNumber: Int): String {
        var num = columnNumber
        var result = ""
        while (num > 0) {
            var surplus = num % 26
            num--
            num /= 26
            result = toChar(surplus) + result
        }
        return result
    }

    private fun toChar(num: Int): String {
        if (num in 0..25) {
            return when (num) {
                1 -> "A";
                2 -> "B";
                3 -> "C";
                4 -> "D";
                5 -> "E";
                6 -> "F";
                7 -> "G";
                8 -> "H";
                9 -> "I";
                10 -> "J";
                11 -> "K";
                12 -> "L";
                13 -> "M";
                14 -> "N";
                15 -> "O";
                16 -> "P";
                17 -> "Q";
                18 -> "R";
                19 -> "S";
                20 -> "T";
                21 -> "U";
                22 -> "V";
                23 -> "W";
                24 -> "X";
                25 -> "Y";
                else -> "Z";
            }
        }
        return ""
    }
}