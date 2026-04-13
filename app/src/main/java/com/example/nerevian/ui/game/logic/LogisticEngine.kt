package com.example.nerevian.ui.game.logic

import kotlin.random.Random

class LogisticEngine(val columns: Int, val rows: Int) {
    val grid = Array(rows) { Array(columns) { Cell() } }

    var activePiece: ActiveCargo? = null
    var efficiencyScore: Int = 0
    var isGameOver: Boolean = false

    init {
        spawnNewPiece()
    }

    // Se llama cada X milisegundos para que la pieza caiga por gravedad
    fun tick() {
        if (isGameOver) return

        if (canMoveTo(activePiece!!.shape, activePiece!!.x, activePiece!!.y - 1)) {
            activePiece!!.y -= 1
        } else {
            lockPiece()
        }
    }

    fun moveLeft() {
        if (!isGameOver && canMoveTo(activePiece!!.shape, activePiece!!.x - 1, activePiece!!.y)) {
            activePiece!!.x -= 1
        }
    }

    fun moveRight() {
        if (!isGameOver && canMoveTo(activePiece!!.shape, activePiece!!.x + 1, activePiece!!.y)) {
            activePiece!!.x += 1
        }
    }

    fun rotatePiece() {
        if (isGameOver) return
        val rotated = activePiece!!.getRotatedShape()
        if (canMoveTo(rotated, activePiece!!.x, activePiece!!.y)) {
            activePiece!!.shape = rotated
        }
    }

    fun hardDrop() {
        if (isGameOver) return
        while (canMoveTo(activePiece!!.shape, activePiece!!.x, activePiece!!.y - 1)) {
            activePiece!!.y -= 1
        }
        lockPiece()
    }

    // --- FÍSICAS Y COLISIONES ---

    private fun canMoveTo(shape: Array<IntArray>, targetX: Int, targetY: Int): Boolean {
        for (r in shape.indices) {
            for (c in shape[r].indices) {
                if (shape[r][c] != 0) {
                    val boardX = targetX + c
                    val boardY = targetY - r

                    // Si sale de las paredes o del suelo
                    if (boardX < 0 || boardX >= columns || boardY < 0) return false

                    // Si choca con otra caja ya bloqueada (ignoramos si sale por el techo momentáneamente)
                    if (boardY < rows && grid[boardY][boardX].type != CargoType.EMPTY) return false
                }
            }
        }
        return true
    }

    private fun lockPiece() {
        val piece = activePiece!!
        for (r in piece.shape.indices) {
            for (c in piece.shape[r].indices) {
                if (piece.shape[r][c] != 0) {
                    val boardY = piece.y - r
                    val boardX = piece.x + c
                    if (boardY in 0 until rows) {
                        grid[boardY][boardX].type = piece.type
                    }
                }
            }
        }
        checkCompletedRows()
        spawnNewPiece()
    }

    private fun spawnNewPiece() {
        val randomShape = CargoBlueprints.SHAPES.random()
        // Elegimos un tipo de caja al azar (saltándonos el EMPTY)
        val randomType = CargoType.values()[Random.nextInt(1, CargoType.values().size)]

        activePiece = ActiveCargo(randomShape, randomType)

        // Si la nueva pieza choca nada más nacer, fin del juego
        if (!canMoveTo(activePiece!!.shape, activePiece!!.x, activePiece!!.y)) {
            isGameOver = true
        }
    }

    private fun checkCompletedRows() {
        var linesCleared = 0
        var y = 0
        while (y < rows) {
            if (grid[y].all { it.type != CargoType.EMPTY }) {
                clearRow(y)
                linesCleared++
                // No incrementamos 'y' porque la fila de arriba ha caído a esta posición
            } else {
                y++
            }
        }
        if (linesCleared > 0) {
            efficiencyScore += linesCleared * 100
        }
    }

    private fun clearRow(rowToClear: Int) {
        for (y in rowToClear until rows - 1) {
            for (x in 0 until columns) {
                grid[y][x].type = grid[y + 1][x].type
            }
        }
        for (x in 0 until columns) {
            grid[rows - 1][x].type = CargoType.EMPTY
        }
    }
}