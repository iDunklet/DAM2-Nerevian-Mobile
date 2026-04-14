package com.example.nerevian.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.example.nerevian.R
import com.example.nerevian.ui.game.GameFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

// ✅ Implementa AndroidFragmentApplication.Callbacks
class DashboardActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val tvTopTitle = findViewById<TextView>(R.id.tvTopTitle)

        if (savedInstanceState == null) {
            replaceFragment(InicioFragment()) // Ahora funciona
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    replaceFragment(GameFragment())
                    tvTopTitle.text = "NEREVIAN - TETRIS"
                    true
                }
                R.id.nav_presupuestos -> {
                    replaceFragment(PresupuestoFragment())
                    tvTopTitle.text = "Mis Presupuestos"
                    true
                }
                R.id.nav_tracking -> {
                    replaceFragment(TrackingFragment())
                    tvTopTitle.text = "Seguimiento"
                    true
                }
                R.id.nav_docs -> {
                    replaceFragment(DocsFragment())
                    tvTopTitle.text = "Documentos"
                    true
                }
                R.id.nav_perfil -> {
                    replaceFragment(PerfilFragment())
                    tvTopTitle.text = "Mi Perfil"
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // ✅ Método obligatorio de la interfaz
    override fun exit() {
        // Se llama cuando el juego quiere cerrarse (por ejemplo, con un botón de salida)
        finish() // Cierra la actividad
    }
}