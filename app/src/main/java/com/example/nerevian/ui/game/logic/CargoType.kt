package com.example.nerevian.ui.game.logic

enum class CargoType {
    EMPTY, FRAGILE, STANDARD, HEAVY, SPECIAL
}

data class Cell(var type: CargoType = CargoType.EMPTY)

// Aquí definimos los bloques (Tetrominós clásicos)
object CargoBlueprints {
    val SHAPES = listOf(
        arrayOf(intArrayOf(1, 1, 1, 1)), // Palo (I)
        arrayOf(intArrayOf(1, 1), intArrayOf(1, 1)), // Cuadrado (O)
        arrayOf(intArrayOf(0, 1, 0), intArrayOf(1, 1, 1)), // T
        arrayOf(intArrayOf(1, 0, 0), intArrayOf(1, 1, 1)), // L
        arrayOf(intArrayOf(0, 0, 1), intArrayOf(1, 1, 1)), // J
        arrayOf(intArrayOf(0, 1, 1), intArrayOf(1, 1, 0)), // S
        arrayOf(intArrayOf(1, 1, 0), intArrayOf(0, 1, 1))  // Z
    )
}

class ActiveCargo(var shape: Array<IntArray>, val type: CargoType) {
    var x = 3  // Posición inicial en X
    var y = 19 // Posición inicial en Y (Arriba del todo)

    // Función matemática para rotar la matriz 90 grados a la derecha
    fun getRotatedShape(): Array<IntArray> {
        val rows = shape.size
        val cols = shape[0].size
        val newShape = Array(cols) { IntArray(rows) }
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                newShape[c][rows - 1 - r] = shape[r][c]
            }
        }
        return newShape
    }
}