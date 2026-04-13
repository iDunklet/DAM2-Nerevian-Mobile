package com.example.nerevian.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.example.nerevian.ui.game.logic.LogisticSimulator

class GameFragment : AndroidFragmentApplication() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val config = com.badlogic.gdx.backends.android.AndroidApplicationConfiguration()
        config.useAccelerometer = false
        config.useCompass = false

        return initializeForView(LogisticSimulator(), config)
    }
}