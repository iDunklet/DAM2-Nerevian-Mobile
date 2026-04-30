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
    enum class State { RUNNING, PAUSED, GAME_OVER }

    companion object {
        private const val BOARD_WIDTH = 10
        private const val BOARD_HEIGHT = 20
        private const val WORLD_W = 18f
        private const val WORLD_H = 26f
        private const val BOARD_X = 1f
        private const val BOARD_Y = 3.5f
    }

    private lateinit var viewport: FitViewport
    private lateinit var camera: OrthographicCamera
    private lateinit var shapeRenderer: ShapeRenderer
    private lateinit var spriteBatch: SpriteBatch
    private lateinit var font: BitmapFont
    private lateinit var layout: GlyphLayout

    // fondo y 7 colores de cajas
    private lateinit var txBackground: Texture
    private lateinit var txPieceI: Texture
    private lateinit var txPieceO: Texture
    private lateinit var txPieceT: Texture
    private lateinit var txPieceL: Texture
    private lateinit var txPieceJ: Texture
    private lateinit var txPieceS: Texture
    private lateinit var txPieceZ: Texture

    private var gameState = State.RUNNING
    private val grid = Array(BOARD_HEIGHT) { IntArray(BOARD_WIDTH) }
    private var currentPiece: Tetromino? = null
    private var nextPiece: Tetromino? = null

    private var currentX = 0
    private var currentY = 0
    private var fallTimer = 0f
    private var fallDelay = 0.6f
    private var score = 0
    private var linesClearedTotal = 0

    // botones
    private val btnSize = 2.5f
    private val btnY = 0.5f
    private val leftBtn   = Rectangle(BOARD_X, btnY, btnSize, btnSize)
    private val rotateBtn = Rectangle(BOARD_X + 3.2f, btnY, btnSize, btnSize)
    private val rightBtn  = Rectangle(BOARD_X + 6.4f, btnY, btnSize, btnSize)
    private val dropBtn   = Rectangle(BOARD_X + 9.6f, btnY, btnSize, btnSize)

    //
    private val pauseBtn = Rectangle(12f, 18f, 5f, 2f)
    private val restartBtn = Rectangle(12f, 15.5f, 5f, 2f)
    private val exitBtn = Rectangle(12f, 13f, 5f, 2f)

    private val pieceTextures by lazy {
        mapOf(
            1 to txPieceI, 2 to txPieceO, 3 to txPieceT,
            4 to txPieceL, 5 to txPieceJ, 6 to txPieceS, 7 to txPieceZ
        )
    }

    private var exitCallback: (() -> Unit)? = null
    fun setOnExitListener(callback: () -> Unit) { exitCallback = callback }

    override fun create() {
        camera = OrthographicCamera()
        viewport = FitViewport(WORLD_W, WORLD_H, camera)
        shapeRenderer = ShapeRenderer()
        spriteBatch = SpriteBatch()
        font = BitmapFont()
        layout = GlyphLayout()
        font.setUseIntegerPositions(false)

        try {
            txBackground = Texture("cielo.jpg")
            txPieceI = Texture("piece_i.png")
            txPieceO = Texture("piece_o.png")
            txPieceT = Texture("piece_t.png")
            txPieceL = Texture("piece_l.png")
            txPieceJ = Texture("piece_j.png")
            txPieceS = Texture("piece_s.png")
            txPieceZ = Texture("piece_z.png")
        } catch (e: Exception) {
            Gdx.app.error("ERROR", "Missing piece textures in assets!")
        }

        Gdx.input.inputProcessor = this
        iniciarNuevoJuego()
    }

    private fun iniciarNuevoJuego() {
        //determinar si llena toda pantalla
        for (i in 0 until BOARD_HEIGHT) grid[i].fill(0)
        score = 0
        linesClearedTotal = 0
        //velocidad
        fallDelay = 0.6f
        gameState = State.RUNNING
        nextPiece = Tetromino.getRandom()
        generarNuevaPieza()
    }

    private fun generarNuevaPieza() {
        currentPiece = nextPiece
        nextPiece = Tetromino.getRandom()
        currentX = BOARD_WIDTH / 2 - (currentPiece?.ancho ?: 0) / 2
        currentY = BOARD_HEIGHT - 1
        if (currentPiece != null && colision(currentX, currentY, currentPiece!!)) {
            gameState = State.GAME_OVER
        }
    }

    private fun colision(x: Int, y: Int, pieza: Tetromino): Boolean {
        for (i in 0 until pieza.alto) {
            for (j in 0 until pieza.ancho) {
                if (pieza.forma[i][j] != 0) {
                    val gx = x + j
                    val gy = y - i
                    if (gx !in 0 until BOARD_WIDTH || gy < 0) return true
                    if (gy < BOARD_HEIGHT && grid[gy][gx] != 0) return true
                }
            }
        }
        return false
    }

    private fun hardDrop() {
        if (gameState != State.RUNNING || currentPiece == null) return
        while (!colision(currentX, currentY - 1, currentPiece!!)) currentY--
        fijarPieza()
    }

    private fun fijarPieza() {
        currentPiece?.let { pieza ->
            for (i in 0 until pieza.alto) {
                for (j in 0 until pieza.ancho) {
                    if (pieza.forma[i][j] != 0) {
                        val gx = currentX + j
                        val gy = currentY - i
                        if (gy in 0 until BOARD_HEIGHT && gx in 0 until BOARD_WIDTH) {
                            grid[gy][gx] = pieza.id
                        }
                    }
                }
            }
        }
        eliminarLineas()
        generarNuevaPieza()
    }

    private fun eliminarLineas() {
        var lineas = 0
        var row = 0
        while (row < BOARD_HEIGHT) {
            if (grid[row].all { it != 0 }) {
                lineas++
                for (r in row until BOARD_HEIGHT - 1) grid[r] = grid[r + 1].copyOf()
                grid[BOARD_HEIGHT - 1] = IntArray(BOARD_WIDTH)
            } else row++
        }
        if (lineas > 0) {
            score += when (lineas) {
                1 -> 100; 2 -> 300; 3 -> 500; 4 -> 800; else -> 0
            }
            linesClearedTotal += lineas
            fallDelay = Math.max(0.1f, 0.6f - (linesClearedTotal / 10) * 0.05f)
        }
    }

    private fun calcularFantasmaY(): Int {
        var fy = currentY
        while (currentPiece != null && !colision(currentX, fy - 1, currentPiece!!)) fy--
        return fy
    }

    //motor principal
    override fun render() {
        if (gameState == State.RUNNING && currentPiece != null) {
            fallTimer += Gdx.graphics.deltaTime
            if (fallTimer >= fallDelay) {
                fallTimer = 0f
                if (!colision(currentX, currentY - 1, currentPiece!!)) currentY-- else fijarPieza()
            }
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        spriteBatch.projectionMatrix.setToOrtho2D(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        spriteBatch.begin()
        if (::txBackground.isInitialized) {
            spriteBatch.draw(txBackground, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        }
        spriteBatch.end()
        viewport.apply()
        spriteBatch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        drawGameGrid()
        drawUI()

        if (gameState == State.PAUSED) {
            Gdx.gl.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

            shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled)
            shapeRenderer.color = com.badlogic.gdx.graphics.Color(0f, 0f, 0f, 0.7f)
            shapeRenderer.rect(0f, 0f, WORLD_W, WORLD_H)
            shapeRenderer.end()

            spriteBatch.begin()
            font.color = com.badlogic.gdx.graphics.Color.WHITE
            font.data.setScale(0.14f)
            drawCentered("PAUSED", WORLD_W / 2f, WORLD_H / 2f + 0.5f)
            font.data.setScale(1f)
            spriteBatch.end()
        }
    }

    private fun drawGameGrid() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0f, 0f, 0f, 0.5f)
        shapeRenderer.rect(BOARD_X, BOARD_Y, BOARD_WIDTH.toFloat(), BOARD_HEIGHT.toFloat())
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color(1f, 1f, 1f, 0.15f)
        for (i in 0..BOARD_WIDTH) shapeRenderer.line(BOARD_X + i, BOARD_Y, BOARD_X + i, BOARD_Y + BOARD_HEIGHT)
        for (j in 0..BOARD_HEIGHT) shapeRenderer.line(BOARD_X, BOARD_Y + j, BOARD_X + BOARD_WIDTH, BOARD_Y + j)
        shapeRenderer.end()

        spriteBatch.begin()

        for (row in 0 until BOARD_HEIGHT) {
            for (col in 0 until BOARD_WIDTH) {
                val id = grid[row][col]
                if (id != 0) {
                    pieceTextures[id]?.let { spriteBatch.draw(it, BOARD_X + col, BOARD_Y + row, 1f, 1f) }
                }
            }
        }

        currentPiece?.let { piece ->
            val texture = pieceTextures[piece.id]
            if (texture != null) {

                val gy = calcularFantasmaY()

                for (i in 0 until piece.alto) {
                    for (j in 0 until piece.ancho) {
                        if (piece.forma[i][j] != 0) {

                            spriteBatch.color = Color(1f, 1f, 1f, 0.3f)
                            spriteBatch.draw(texture, BOARD_X + currentX + j, BOARD_Y + gy - i, 1f, 1f)

                            spriteBatch.color = Color.WHITE
                            spriteBatch.draw(texture, BOARD_X + currentX + j, BOARD_Y + currentY - i, 1f, 1f)
                        }
                    }
                }
            }
        }
        spriteBatch.end()
    }

    private fun dibujarPiezaTexture(texture: Texture, x: Int, y: Int, pieza: Tetromino, alpha: Float) {
        spriteBatch.color = Color(1f, 1f, 1f, alpha)
        val drawX = BOARD_X + x
        val drawY = BOARD_Y + y - (pieza.alto - 1)
        spriteBatch.draw(texture, drawX, drawY, pieza.ancho.toFloat(), pieza.alto.toFloat())
        spriteBatch.color = Color.WHITE
    }

    private fun drawUI() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color(0.2f, 0.2f, 0.2f, 0.8f)
        // fondo boton
        for (btn in listOf(leftBtn, rotateBtn, rightBtn, dropBtn)) shapeRenderer.rect(btn.x, btn.y, btn.width, btn.height)

        for (btn in listOf(pauseBtn, restartBtn, exitBtn)) shapeRenderer.rect(btn.x, btn.y, btn.width, btn.height)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.WHITE
        dibujarIconosControl()
        shapeRenderer.end()

        spriteBatch.begin()
        font.color = Color.WHITE
        font.data.setScale(0.045f)

        font.draw(spriteBatch, "NEXT BOX", 12.3f, 24.5f)
        nextPiece?.let { next ->
            val texture = pieceTextures[next.id]
            if (texture != null) {

                for (i in 0 until next.alto) {
                    for (j in 0 until next.ancho) {
                        if (next.forma[i][j] != 0) {
                            spriteBatch.draw(
                                texture,
                                12.8f + j * 0.8f,
                                23.0f - i * 0.8f,
                                0.8f, 0.8f
                            )
                        }
                    }
                }
            }
        }

        font.draw(spriteBatch, if(gameState == State.PAUSED) "RESUME" else "PAUSE", 12.3f, 19.3f)
        font.draw(spriteBatch, "RESTART", 12.3f, 16.8f)
        font.draw(spriteBatch, "EXIT", 13.0f, 14.3f)

        font.draw(spriteBatch, "REVENUE", 12.2f, 9.5f)
        font.draw(spriteBatch, "$ $score", 12.5f, 8.7f)

        if (gameState == State.GAME_OVER) {
            font.color = Color.RED
            font.data.setScale(0.08f)
            drawCentered("GAME OVER", BOARD_X + BOARD_WIDTH/2f, BOARD_Y + BOARD_HEIGHT/2f)
        }
        spriteBatch.end()
    }

    private fun dibujarIconosControl() {
        // flecha izquierda
        shapeRenderer.line(leftBtn.x + 1.8f, leftBtn.y + 0.5f, leftBtn.x + 0.7f, leftBtn.y + 1.25f)
        shapeRenderer.line(leftBtn.x + 0.7f, leftBtn.y + 1.25f, leftBtn.x + 1.8f, leftBtn.y + 2f)
        // flecha derecha
        shapeRenderer.line(rightBtn.x + 0.7f, rightBtn.y + 0.5f, rightBtn.x + 1.8f, rightBtn.y + 1.25f)
        shapeRenderer.line(rightBtn.x + 1.8f, rightBtn.y + 1.25f, rightBtn.x + 0.7f, rightBtn.y + 2f)
        // cambiar forma
        shapeRenderer.circle(rotateBtn.x + 1.25f, rotateBtn.y + 1.25f, 0.7f, 20)
        // bajar directa
        shapeRenderer.line(dropBtn.x + 1.25f, dropBtn.y + 2f, dropBtn.x + 1.25f, dropBtn.y + 0.5f)
        shapeRenderer.line(dropBtn.x + 0.7f, dropBtn.y + 1f, dropBtn.x + 1.25f, dropBtn.y + 0.5f)
        shapeRenderer.line(dropBtn.x + 1.8f, dropBtn.y + 1f, dropBtn.x + 1.25f, dropBtn.y + 0.5f)
    }

    private fun drawCentered(text: String, cx: Float, y: Float) {
        layout.setText(font, text)
        font.draw(spriteBatch, text, cx - layout.width / 2f, y)
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val wp = viewport.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        if (pauseBtn.contains(wp.x, wp.y)) { gameState = if (gameState == State.RUNNING) State.PAUSED else State.RUNNING; return true }
        if (restartBtn.contains(wp.x, wp.y)) { iniciarNuevoJuego(); return true }
        if (exitBtn.contains(wp.x, wp.y)) { exitCallback?.invoke(); return true }

        if (gameState == State.RUNNING) {
            when {
                leftBtn.contains(wp.x, wp.y)    -> if (currentPiece != null && !colision(currentX - 1, currentY, currentPiece!!)) currentX--
                rightBtn.contains(wp.x, wp.y)   -> if (currentPiece != null && !colision(currentX + 1, currentY, currentPiece!!)) currentX++
                rotateBtn.contains(wp.x, wp.y)  -> {
                    val r = currentPiece?.rotar()
                    if (r != null && !colision(currentX, currentY, r)) currentPiece = r
                }
                dropBtn.contains(wp.x, wp.y)    -> hardDrop()
            }
        }
        return true
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height, true)
    override fun dispose() {
        shapeRenderer.dispose(); spriteBatch.dispose(); font.dispose()
        if (::txBackground.isInitialized) txBackground.dispose()
        listOf(txPieceI, txPieceO, txPieceT, txPieceL, txPieceJ, txPieceS, txPieceZ).forEach { if (::txPieceI.isInitialized) it.dispose() }
    }
    override fun touchUp(x: Int, y: Int, p: Int, b: Int) = false
    override fun touchDragged(x: Int, y: Int, p: Int) = false
    override fun mouseMoved(x: Int, y: Int) = false
    override fun scrolled(aX: Float, aY: Float) = false
    override fun keyDown(k: Int) = false
    override fun keyUp(k: Int) = false
    override fun keyTyped(c: Char) = false
    override fun touchCancelled(x: Int, y: Int, p: Int, b: Int) = false
}
