package com.example.nerevian.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.nerevian.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardAgenteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboardagente)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationAgente)
        val tvTopTitleAgente = findViewById<TextView>(R.id.tvTopTitleAgente)


        if (savedInstanceState == null) {
            replaceFragment(PedidosFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_pedidos -> {
                    replaceFragment(PedidosFragment())
                    tvTopTitleAgente.text = "Pedidos de Clientes"
                    true
                }

                R.id.nav_perfil -> {
                    replaceFragment(PerfilAgenteFragment())
                    tvTopTitleAgente.text = "Mi Perfil"
                    true
                }

                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_containerAgente, fragment)
            .commit()
    }
}
