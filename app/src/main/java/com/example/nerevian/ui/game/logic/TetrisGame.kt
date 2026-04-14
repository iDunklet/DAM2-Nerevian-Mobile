package com.example.nerevian.ui.game.logic

import android.util.Log
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import kotlin.random.Random

class TetrisGame : ApplicationAdapter(), InputProcessor {
    private lateinit var shapeRenderer: ShapeRenderer

    override fun create() {
        shapeRenderer = ShapeRenderer()
        Gdx.input.inputProcessor = this
        Log.d("TetrisGame", "create() mínimo")
    }

    override fun render() {
        Log.d("TetrisGame", "render() mínimo")
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f) // Rojo
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.GREEN
        shapeRenderer.rect(2f, 2f, 5f, 5f) // Cuadrado verde
        shapeRenderer.end()
    }

    override fun dispose() { shapeRenderer.dispose() }

    // Implementaciones vacías (devuelven false)
    override fun touchDown(x: Int, y: Int, pointer: Int, button: Int) = false
    override fun touchUp(x: Int, y: Int, pointer: Int, button: Int) = false
    override fun touchDragged(x: Int, y: Int, pointer: Int) = false
    override fun mouseMoved(x: Int, y: Int) = false
    override fun scrolled(amountX: Float, amountY: Float) = false
    override fun keyDown(keycode: Int) = false
    override fun keyUp(keycode: Int) = false
    override fun keyTyped(character: Char) = false
    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int) = false
}