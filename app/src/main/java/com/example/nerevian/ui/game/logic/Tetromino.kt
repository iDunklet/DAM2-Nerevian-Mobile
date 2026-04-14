package com.example.nerevian.ui.game.logic

import com.badlogic.gdx.graphics.Color

class Tetromino(var forma: Array<IntArray>, val color: Color, val id: Int, val name: String = "") {

    val ancho: Int get() = forma[0].size
    val alto: Int get() = forma.size

    fun rotar(): Tetromino {
        val nuevaForma = Array(ancho) { IntArray(alto) }
        for (i in 0 until alto) {
            for (j in 0 until ancho) {
                nuevaForma[j][alto - 1 - i] = forma[i][j]
            }
        }
        return Tetromino(nuevaForma, color, id, name)
    }

    companion object {
        fun I() = Tetromino(arrayOf(intArrayOf(1, 1, 1, 1)), Color.CYAN, 1, "I")
        fun O() = Tetromino(arrayOf(intArrayOf(1, 1), intArrayOf(1, 1)), Color.YELLOW, 2, "O")
        fun T() = Tetromino(arrayOf(intArrayOf(0, 1, 0), intArrayOf(1, 1, 1)), Color.MAGENTA, 3, "T")
        fun L() = Tetromino(arrayOf(intArrayOf(0, 0, 1), intArrayOf(1, 1, 1)), Color.ORANGE, 4, "L")
        fun J() = Tetromino(arrayOf(intArrayOf(1, 0, 0), intArrayOf(1, 1, 1)), Color.BLUE, 5, "J")
        fun S() = Tetromino(arrayOf(intArrayOf(0, 1, 1), intArrayOf(1, 1, 0)), Color.GREEN, 6, "S")
        fun Z() = Tetromino(arrayOf(intArrayOf(1, 1, 0), intArrayOf(0, 1, 1)), Color.RED, 7, "Z")

        fun getRandom(): Tetromino {
            val piezas = listOf(I(), O(), T(), L(), J(), S(), Z())
            return piezas.random()
        }
    }
}