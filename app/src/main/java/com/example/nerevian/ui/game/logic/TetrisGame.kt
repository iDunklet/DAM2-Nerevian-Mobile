package com.example.nerevian.ui.game.logic

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport

class TetrisGame : ApplicationAdapter(), InputProcessor {
    companion object {
        private const val BOARD_WIDTH = 10
        private const val BOARD_HEIGHT = 20
        private const val WORLD_W = 16f
        private const val WORLD_H = 26f
        private const val BOARD_X = 2f
        private const val BOARD_Y = 3.5f
    }

    private lateinit var viewport: FitViewport
    private lateinit var camera: OrthographicCamera
    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var spriteBatch: SpriteBatch
    private lateinit var font: BitmapFont
    private lateinit var layout: GlyphLayout

    private val grid = Array(BOARD_HEIGHT) { IntArray(BOARD_WIDTH) { 0 } }
    private var currentPiece: Tetromino? = null
    private var currentX = 0
    private var currentY = 0
    private var fallTimer = 0f
    private var fallDelay = 0.5f
    private var score = 0
    private var gameOver = false

    private val btnSize = 2.8f
    private val btnY = 0.35f
    private val leftBtn   = Rectangle(BOARD_X,                          btnY, btnSize, btnSize)
    private val rotateBtn = Rectangle(BOARD_X + BOARD_WIDTH/2f - btnSize/2f, btnY, btnSize, btnSize)
    private val rightBtn  = Rectangle(BOARD_X + BOARD_WIDTH - btnSize,  btnY, btnSize, btnSize)
    private val restartBtn = Rectangle(BOARD_X + BOARD_WIDTH + 0.3f, BOARD_Y + 2f, 3f, 1.4f)
    private val exitBtn = Rectangle(BOARD_X + BOARD_WIDTH + 0.4f, BOARD_Y + 1.2f, 3.4f, 1.2f)

    private val containerColors = mapOf(
        1 to Color.CYAN, 2 to Color.YELLOW, 3 to Color.MAGENTA,
        4 to Color.ORANGE, 5 to Color.BLUE, 6 to Color.GREEN, 7 to Color.RED
    )

    private var fontScale = 1f
    private var exitCallback: (() -> Unit)? = null

    fun setOnExitListener(callback: () -> Unit) {
        exitCallback = callback
    }

    override fun create() {
        camera = OrthographicCamera()
        viewport = FitViewport(WORLD_W, WORLD_H, camera)
        camera.position.set(WORLD_W / 2f, WORLD_H / 2f, 0f)
        camera.update()

        shapeRenderer = ShapeRenderer()
        spriteBatch = SpriteBatch()
        font = BitmapFont()
        layout = GlyphLayout()
        font.setUseIntegerPositions(false)

        Gdx.input.inputProcessor = this
        iniciarNuevoJuego()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        camera.position.set(WORLD_W / 2f, WORLD_H / 2f, 0f)
        camera.update()
        val ppu = height / WORLD_H
        fontScale = ppu / 15f
    }

    private fun iniciarNuevoJuego() {
        for (i in 0 until BOARD_HEIGHT) grid[i].fill(0)
        score = 0
        gameOver = false
        fallTimer = 0f
        generarNuevaPieza()
    }

    private fun generarNuevaPieza() {
        currentPiece = Tetromino.getRandom()
        currentX = BOARD_WIDTH / 2 - currentPiece!!.ancho / 2
        currentY = BOARD_HEIGHT - currentPiece!!.alto
        if (colision(currentX, currentY, currentPiece!!)) {
            gameOver = true
            currentPiece = null
        }
    }

    private fun colision(x: Int, y: Int, pieza: Tetromino): Boolean {
        for (i in 0 until pieza.alto) {
            for (j in 0 until pieza.ancho) {
                if (pieza.forma[i][j] != 0) {
                    val gx = x + j
                    val gy = y - i
                    if (gx < 0 || gx >= BOARD_WIDTH || gy < 0 || gy >= BOARD_HEIGHT) return true
                    if (grid[gy][gx] != 0) return true
                }
            }
        }
        return false
    }

    private fun fijarPieza() {
        if (currentPiece == null) return
        for (i in 0 until currentPiece!!.alto) {
            for (j in 0 until currentPiece!!.ancho) {
                if (currentPiece!!.forma[i][j] != 0) {
                    val gx = currentX + j
                    val gy = currentY - i
                    if (gy in 0 until BOARD_HEIGHT && gx in 0 until BOARD_WIDTH) {
                        grid[gy][gx] = currentPiece!!.id
                    }
                }
            }
        }

        var lineas = 0
        var row = 0
        while (row < BOARD_HEIGHT) {
            if (grid[row].all { it != 0 }) {
                for (r in row until BOARD_HEIGHT - 1) grid[r] = grid[r + 1].clone()
                grid[BOARD_HEIGHT - 1] = IntArray(BOARD_WIDTH)
                lineas++
            } else {
                row++
            }
        }
        score += when (lineas) {
            1 -> 100
            2 -> 300
            3 -> 500
            4 -> 800
            else -> 0
        }
        generarNuevaPieza()
    }

    private fun moverIzquierda() {
        if (!gameOver && currentPiece != null && !colision(currentX - 1, currentY, currentPiece!!)) currentX--
    }

    private fun moverDerecha() {
        if (!gameOver && currentPiece != null && !colision(currentX + 1, currentY, currentPiece!!)) currentX++
    }

    private fun rotarPieza() {
        if (!gameOver && currentPiece != null) {
            val r = currentPiece!!.rotar()
            if (!colision(currentX, currentY, r)) currentPiece = r
        }
    }

    private fun caerUnPaso() {
        if (gameOver || currentPiece == null) return
        if (!colision(currentX, currentY - 1, currentPiece!!)) {
            currentY--
        } else {
            fijarPieza()
        }
    }

    private fun calcularFantasmaY(): Int {
        var fy = currentY
        while (!colision(currentX, fy - 1, currentPiece!!)) fy--
        return fy
    }

    private fun dibujarBloque3D(x: Float, y: Float, base: Color, alpha: Float = 1f) {
        val s = 0.95f
        shapeRenderer.color = Color(base.r * 0.4f, base.g * 0.4f, base.b * 0.4f, alpha)
        shapeRenderer.rect(x, y, s, s)
        shapeRenderer.color = Color(minOf(base.r + 0.3f, 1f), minOf(base.g + 0.3f, 1f), minOf(base.b + 0.3f, 1f), alpha)
        shapeRenderer.rect(x + 0.05f, y + 0.05f, s - 0.1f, s - 0.1f)
        shapeRenderer.color = Color(base.r, base.g, base.b, alpha)
        shapeRenderer.rect(x + 0.15f, y + 0.15f, s - 0.3f, s - 0.3f)
    }

    private fun dibujarFlechaIzq(cx: Float, cy: Float, size: Float) {
        val hw = size * 0.35f
        shapeRenderer.triangle(cx - hw, cy, cx + hw, cy + hw, cx + hw, cy - hw)
        shapeRenderer.rect(cx, cy - size * 0.12f, hw * 0.8f, size * 0.24f)
    }

    private fun dibujarFlechaDer(cx: Float, cy: Float, size: Float) {
        val hw = size * 0.35f
        shapeRenderer.triangle(cx + hw, cy, cx - hw, cy + hw, cx - hw, cy - hw)
        shapeRenderer.rect(cx - hw * 1.8f, cy - size * 0.12f, hw * 0.8f, size * 0.24f)
    }

    private fun dibujarRotar(cx: Float, cy: Float, size: Float) {
        val r = size * 0.28f
        shapeRenderer.circle(cx, cy, r, 24)
        shapeRenderer.color = Color(0.15f, 0.2f, 0.3f, 1f)
        shapeRenderer.circle(cx, cy, r * 0.55f, 24)
    }

    private fun dibujarIconoRestart(cx: Float, cy: Float, size: Float) {
        val r = size * 0.25f
        shapeRenderer.circle(cx, cy, r, 24)
        shapeRenderer.color = Color(0.25f, 0.1f, 0.1f, 1f)
        shapeRenderer.circle(cx, cy, r * 0.5f, 24)
        shapeRenderer.color = Color.WHITE
        val tr = r * 0.55f
        shapeRenderer.triangle(cx - tr * 0.3f, cy + tr, cx - tr * 0.3f, cy - tr, cx + tr * 0.7f, cy)
    }

    private fun drawCentered(text: String, cx: Float, y: Float) {
        layout.setText(font, text)
        font.draw(spriteBatch, text, cx - layout.width / 2f, y)
    }

    override fun render() {
        val delta = Gdx.graphics.deltaTime
        if (!gameOver && currentPiece != null) {
            fallTimer += delta
            if (fallTimer >= fallDelay) {
                fallTimer = 0f
                caerUnPaso()
            }
        }

        Gdx.gl.glClearColor(0.08f, 0.09f, 0.12f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        viewport.apply()
        camera.update()
        shapeRenderer.projectionMatrix = camera.combined
        spriteBatch.projectionMatrix = camera.combined

        // 1. Fondo del tablero (relleno)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        try {
            shapeRenderer.color = Color(0.04f, 0.05f, 0.08f, 1f)
            shapeRenderer.rect(BOARD_X, BOARD_Y, BOARD_WIDTH.toFloat(), BOARD_HEIGHT.toFloat())
        } finally { shapeRenderer.end() }

        // 2. Líneas de cuadrícula
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        try {
            shapeRenderer.color = Color(1f, 1f, 1f, 0.06f)
            for (i in 0..BOARD_WIDTH) shapeRenderer.line(BOARD_X + i, BOARD_Y, BOARD_X + i, BOARD_Y + BOARD_HEIGHT)
            for (j in 0..BOARD_HEIGHT) shapeRenderer.line(BOARD_X, BOARD_Y + j, BOARD_X + BOARD_WIDTH, BOARD_Y + j)
            shapeRenderer.color = Color(0.35f, 0.5f, 0.7f, 0.6f)
            shapeRenderer.rect(BOARD_X, BOARD_Y, BOARD_WIDTH.toFloat(), BOARD_HEIGHT.toFloat())
        } finally { shapeRenderer.end() }

        // 3. Bloques fijos (relleno)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        try {
            for (row in 0 until BOARD_HEIGHT) {
                for (col in 0 until BOARD_WIDTH) {
                    val cell = grid[row][col]
                    if (cell != 0) {
                        dibujarBloque3D(BOARD_X + col, BOARD_Y + row, containerColors[cell] ?: Color.GRAY)
                    }
                }
            }
        } finally { shapeRenderer.end() }

        // 4. Pieza actual y fantasma (relleno)
        if (currentPiece != null && !gameOver) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            try {
                val fy = calcularFantasmaY()
                for (i in 0 until currentPiece!!.alto) {
                    for (j in 0 until currentPiece!!.ancho) {
                        if (currentPiece!!.forma[i][j] != 0) {
                            val bx = BOARD_X + (currentX + j)
                            // sombra
                            dibujarBloque3D(bx, BOARD_Y + (fy - i), currentPiece!!.color, 0.18f)
                            // pieza real
                            dibujarBloque3D(bx, BOARD_Y + (currentY - i), currentPiece!!.color, 1f)
                        }
                    }
                }
            } finally { shapeRenderer.end() }
        }

        // 5. Botones (relleno)
        val btnBase = Color(0.15f, 0.22f, 0.32f, 1f)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        try {
            for (btn in listOf(leftBtn, rotateBtn, rightBtn)) {
                shapeRenderer.color = btnBase
                shapeRenderer.rect(btn.x, btn.y, btn.width, btn.height)
            }
            shapeRenderer.color = Color(0.4f, 0.12f, 0.12f, 1f)
            shapeRenderer.rect(restartBtn.x, restartBtn.y, restartBtn.width, restartBtn.height)
            shapeRenderer.color = Color(0.3f, 0.2f, 0.2f, 0.9f)
            shapeRenderer.rect(exitBtn.x, exitBtn.y, exitBtn.width, exitBtn.height)
        } finally { shapeRenderer.end() }

        // 6. Iconos de los botones (relleno)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        try {
            shapeRenderer.color = Color(0.8f, 0.9f, 1f, 0.95f)
            dibujarFlechaIzq(leftBtn.x + leftBtn.width / 2f, leftBtn.y + leftBtn.height / 2f, btnSize)
            dibujarFlechaDer(rightBtn.x + rightBtn.width / 2f, rightBtn.y + rightBtn.height / 2f, btnSize)
            shapeRenderer.color = Color(0.6f, 0.85f, 1f, 0.95f)
            dibujarRotar(rotateBtn.x + rotateBtn.width / 2f, rotateBtn.y + rotateBtn.height / 2f, btnSize)
            dibujarIconoRestart(restartBtn.x + restartBtn.width / 2f, restartBtn.y + restartBtn.height / 2f, restartBtn.height)
        } finally { shapeRenderer.end() }

        // 7. Bordes de los botones (línea)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        try {
            val btnBorder = Color(0.35f, 0.5f, 0.65f, 1f)
            for (btn in listOf(leftBtn, rotateBtn, rightBtn)) {
                shapeRenderer.color = btnBorder
                shapeRenderer.rect(btn.x, btn.y, btn.width, btn.height)
            }
            shapeRenderer.color = Color(0.8f, 0.3f, 0.3f, 1f)
            shapeRenderer.rect(restartBtn.x, restartBtn.y, restartBtn.width, restartBtn.height)
            shapeRenderer.color = Color(0.8f, 0.4f, 0.4f, 1f)
            shapeRenderer.rect(exitBtn.x, exitBtn.y, exitBtn.width, exitBtn.height)
        } finally { shapeRenderer.end() }

        // 8. Panel lateral (score) - relleno
        val panelX = BOARD_X + BOARD_WIDTH + 0.4f
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        try {
            shapeRenderer.color = Color(0.1f, 0.12f, 0.18f, 0.85f)
            shapeRenderer.rect(panelX, BOARD_Y + 4.5f, 3.4f, 2.8f)
        } finally { shapeRenderer.end() }

        // 9. Borde del panel lateral
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        try {
            shapeRenderer.color = Color(0.35f, 0.5f, 0.65f, 0.5f)
            shapeRenderer.rect(panelX, BOARD_Y + 4.5f, 3.4f, 2.8f)
        } finally { shapeRenderer.end() }

        // 10. Textos (SpriteBatch)
        spriteBatch.begin()
        try {
            // SCORE
            font.data.setScale(fontScale * 0.7f)
            font.color = Color(0.6f, 0.75f, 0.9f, 1f)
            font.draw(spriteBatch, "SCORE", panelX + 0.15f, BOARD_Y + 6.8f)
            font.data.setScale(fontScale * 1.0f)
            font.color = Color.WHITE
            font.draw(spriteBatch, "$score", panelX + 0.15f, BOARD_Y + 6.1f)

            // Etiquetas de botones principales
            font.data.setScale(fontScale * 0.55f)
            font.color = Color(0.7f, 0.8f, 0.9f, 0.8f)
            drawCentered("MOVER", leftBtn.x + leftBtn.width / 2f, btnY - 0.1f)
            drawCentered("ROTAR", rotateBtn.x + rotateBtn.width / 2f, btnY - 0.1f)
            drawCentered("MOVER", rightBtn.x + rightBtn.width / 2f, btnY - 0.1f)

            // Texto botón salir
            font.color = Color(1f, 0.5f, 0.5f, 1f)
            drawCentered("SALIR", exitBtn.x + exitBtn.width / 2f, exitBtn.y + exitBtn.height / 2f + 0.2f)

            // Game over
            if (gameOver) {
                font.color = Color(1f, 0.3f, 0.3f, 1f)
                font.data.setScale(fontScale * 1.3f)
                drawCentered("GAME OVER", BOARD_X + BOARD_WIDTH / 2f, BOARD_Y + BOARD_HEIGHT / 2f + 1f)
                font.data.setScale(fontScale * 0.75f)
                font.color = Color(0.8f, 0.8f, 0.8f, 1f)
                drawCentered("Pulsa R para reiniciar", BOARD_X + BOARD_WIDTH / 2f, BOARD_Y + BOARD_HEIGHT / 2f - 0.5f)
            }
        } finally { spriteBatch.end() }
    }

    override fun dispose() {
        shapeRenderer.dispose()
        spriteBatch.dispose()
        font.dispose()
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val wp = viewport.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        when {
            leftBtn.contains(wp.x, wp.y)    -> moverIzquierda()
            rightBtn.contains(wp.x, wp.y)   -> moverDerecha()
            rotateBtn.contains(wp.x, wp.y)  -> rotarPieza()
            restartBtn.contains(wp.x, wp.y) -> iniciarNuevoJuego()
            exitBtn.contains(wp.x, wp.y)    -> exitCallback?.invoke()
        }
        return true
    }

    override fun touchUp(x: Int, y: Int, pointer: Int, button: Int) = false
    override fun touchDragged(x: Int, y: Int, pointer: Int) = false
    override fun mouseMoved(x: Int, y: Int) = false
    override fun scrolled(amountX: Float, amountY: Float) = false
    override fun keyDown(keycode: Int) = false
    override fun keyUp(keycode: Int) = false
    override fun keyTyped(character: Char) = false
    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int) = false
}