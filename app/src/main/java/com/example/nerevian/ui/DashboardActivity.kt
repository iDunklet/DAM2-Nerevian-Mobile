package com.example.nerevian.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.example.nerevian.R
import com.example.nerevian.ui.game.GameFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val tvTopTitle = findViewById<TextView>(R.id.tvTopTitle)

        if (savedInstanceState == null) {
            replaceFragment(InicioFragment())
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
        if (fragment is GameFragment) {
            fragment.setOnExitListener(object : GameFragment.OnExitListener {
                override fun onExit() {
                    supportFragmentManager.popBackStack()
                }
            })
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun exit() {
        finish()
    }
}