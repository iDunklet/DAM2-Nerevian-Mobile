package com.example.nerevian.ui.game.logic

import com.badlogic.gdx.graphics.Color

enum class Tetromino(val forma: Array<IntArray>, val color: Color) {
    I(
        arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(1, 1, 1, 1),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0)
        ),
        Color.CYAN
    ),
    O(
        arrayOf(
            intArrayOf(1, 1),
            intArrayOf(1, 1)
        ),
        Color.YELLOW
    ),
    T(
        arrayOf(
            intArrayOf(0, 1, 0),
            intArrayOf(1, 1, 1),
            intArrayOf(0, 0, 0)
        ),
        Color.MAGENTA
    ),
    L(
        arrayOf(
            intArrayOf(0, 0, 1),
            intArrayOf(1, 1, 1),
            intArrayOf(0, 0, 0)
        ),
        Color.ORANGE
    ),
    J(
        arrayOf(
            intArrayOf(1, 0, 0),
            intArrayOf(1, 1, 1),
            intArrayOf(0, 0, 0)
        ),
        Color.BLUE
    ),
    S(
        arrayOf(
            intArrayOf(0, 1, 1),
            intArrayOf(1, 1, 0),
            intArrayOf(0, 0, 0)
        ),
        Color.GREEN
    ),
    Z(
        arrayOf(
            intArrayOf(1, 1, 0),
            intArrayOf(0, 1, 1),
            intArrayOf(0, 0, 0)
        ),
        Color.RED
    );

    val ancho: Int get() = forma[0].size
    val alto: Int get() = forma.size

    fun rotar(): Tetromino {
        val nuevaForma = Array(ancho) { IntArray(alto) }
        for (i in 0 until alto) {
            for (j in 0 until ancho) {
                nuevaForma[j][alto - 1 - i] = forma[i][j]
            }
        }
        for (t in values()) {
            if (t.forma.contentDeepEquals(nuevaForma)) {
                return t
            }
        }
        return this
    }
}