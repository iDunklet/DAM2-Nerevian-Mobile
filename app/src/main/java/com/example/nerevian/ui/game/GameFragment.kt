package com.example.nerevian.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.example.nerevian.ui.game.logic.TetrisGame

class GameFragment : AndroidFragmentApplication() {

    interface OnExitListener {
        fun onExit()
    }

    private var exitListener: OnExitListener? = null

    fun setOnExitListener(listener: OnExitListener) {
        this.exitListener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val config = com.badlogic.gdx.backends.android.AndroidApplicationConfiguration()
        config.useAccelerometer = false
        config.useCompass = false

        val game = TetrisGame()
        game.setOnExitListener {
            exitListener?.onExit()
        }

        return initializeForView(game, config)
    }
}