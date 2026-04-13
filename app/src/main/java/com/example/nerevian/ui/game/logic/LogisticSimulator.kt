package com.example.nerevian.ui.game.logic

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import kotlin.math.abs

class LogisticSimulator : ApplicationAdapter(), GestureDetector.GestureListener {
    private lateinit var engine: LogisticEngine
    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: Viewport

    private val cellSize = 60f
    private var boardWidth = 0f
    private var boardHeight = 0f

    // Variables de tiempo para la gravedad
    private var timeSinceLastDrop = 0f
    private val dropInterval = 0.6f // La pieza cae cada 0.6 segundos

    override fun create() {
        shapeRenderer = ShapeRenderer()
        engine = LogisticEngine(10, 20)

        boardWidth = engine.columns * cellSize
        boardHeight = engine.rows * cellSize

        camera = OrthographicCamera()
        viewport = FitViewport(boardWidth + 150f, boardHeight + 150f, camera)

        // Configurar los controles táctiles
        Gdx.input.inputProcessor = GestureDetector(this)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime

        // --- 1. LÓGICA DE JUEGO (GRAVEDAD) ---
        if (!engine.isGameOver) {
            timeSinceLastDrop += delta
            if (timeSinceLastDrop >= dropInterval) {
                engine.tick()
                timeSinceLastDrop -= dropInterval
            }
        }

        // --- 2. DIBUJADO ---
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        val startX = (viewport.worldWidth - boardWidth) / 2f
        val startY = (viewport.worldHeight - boardHeight) / 2f

        // Fondo negro del tablero
        shapeRenderer.color = Color(0.05f, 0.05f, 0.05f, 1f)
        shapeRenderer.rect(startX, startY, boardWidth, boardHeight)

        // Dibujar cajas apiladas (La cuadrícula)
        for (y in 0 until engine.rows) {
            for (x in 0 until engine.columns) {
                val cargo = engine.grid[y][x]
                drawBlock(startX, startY, x, y, cargo.type)
            }
        }

        // Dibujar la pieza activa (La que está cayendo)
        engine.activePiece?.let { piece ->
            for (r in piece.shape.indices) {
                for (c in piece.shape[r].indices) {
                    if (piece.shape[r][c] != 0) {
                        val boardX = piece.x + c
                        val boardY = piece.y - r
                        drawBlock(startX, startY, boardX, boardY, piece.type)
                    }
                }
            }
        }

        shapeRenderer.end()
    }

    private fun drawBlock(startX: Float, startY: Float, boardX: Int, boardY: Int, type: CargoType) {
        // No pintar piezas que están por encima del techo visible
        if (boardY >= engine.rows) return

        shapeRenderer.color = when(type) {
            CargoType.EMPTY -> Color(0.15f, 0.15f, 0.15f, 1f)
            CargoType.FRAGILE -> Color.SKY
            CargoType.STANDARD -> Color.ORANGE
            CargoType.HEAVY -> Color.SLATE
            CargoType.SPECIAL -> Color.PURPLE
        }

        shapeRenderer.rect(
            startX + (boardX * cellSize) + 1f,
            startY + (boardY * cellSize) + 1f,
            cellSize - 2f,
            cellSize - 2f
        )
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }

    // --- CONTROLES TÁCTILES ---
    override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean {
        engine.rotatePiece() // Tocar la pantalla para rotar
        return true
    }

    override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
        // Deslizar (Swipe) para mover
        if (abs(velocityX) > abs(velocityY)) {
            // Movimiento horizontal
            if (velocityX > 0) engine.moveRight() else engine.moveLeft()
        } else {
            // Movimiento vertical
            if (velocityY > 0) engine.hardDrop() // Deslizar hacia abajo tira la pieza
        }
        return true
    }

    // Métodos obligatorios de la interfaz GestureDetector (los dejamos vacíos si no se usan)
    override fun touchDown(x: Float, y: Float, pointer: Int, button: Int) = false
    override fun longPress(x: Float, y: Float) = false
    override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float) = false
    override fun panStop(x: Float, y: Float, pointer: Int, button: Int) = false
    override fun zoom(initialDistance: Float, distance: Float) = false
    override fun pinch(initialPointer1: Vector2, initialPointer2: Vector2, pointer1: Vector2, pointer2: Vector2) = false
    override fun pinchStop() {}
}