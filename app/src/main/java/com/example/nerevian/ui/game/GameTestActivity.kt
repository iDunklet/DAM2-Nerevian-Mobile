package com.example.nerevian.ui.game

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.badlogic.gdx.backends.android.AndroidFragmentApplication

// ¡ESTA LÍNEA ES CLAVE! Sin el ", AndroidFragmentApplication.Callbacks" crashea siempre
class GameTestActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = FrameLayout(this)
        container.id = View.generateViewId()
        setContentView(container)

        supportFragmentManager.beginTransaction()
            .replace(container.id, GameFragment())
            .commit()
    }

    // Método obligatorio de la interfaz
    override fun exit() {
        finish()
    }
}