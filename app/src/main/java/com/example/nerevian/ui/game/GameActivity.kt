package com.example.nerevian.ui.game

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.example.nerevian.ui.game.logic.TetrisGame

class GameActivity : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = AndroidApplicationConfiguration()
        config.useAccelerometer = false
        config.useCompass = false

        val game = TetrisGame()

        game.setOnExitListener {
            runOnUiThread {
                finish()
            }
        }

        initialize(game, config)
    }
}