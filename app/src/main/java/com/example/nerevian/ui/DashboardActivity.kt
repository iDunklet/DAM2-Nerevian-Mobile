package com.example.nerevian.ui

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.example.nerevian.R
import com.example.nerevian.ui.agent.OrderListFragment
import com.example.nerevian.ui.client.DocsFragment
import com.example.nerevian.ui.client.InicioFragment
import com.example.nerevian.ui.client.PresupuestoFragment
import com.example.nerevian.ui.client.TrackStatusFragment
import com.example.nerevian.ui.game.GameFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Actividad principal con BottomNavigation que adapta el menú según el rol del usuario (Agent vs resto).
 * Modularización: extraer lógica de roles a un ViewModel o manager, separar la navegación en un componente,
 * y mover los títulos a recursos de strings. Considerar usar Navigation Component.
 */
class DashboardActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val tvTopTitle = findViewById<TextView>(R.id.tvTopTitle)

        // Obtiene el rol desde SharedPreferences (por defecto 5 = Client)
        val sharedPreferences = getSharedPreferences("NerevianPrefs", Context.MODE_PRIVATE)
        val roleId = sharedPreferences.getInt("role_id", 5)
        val isAgent = (roleId == 4)   // Solo rol 4 (Agent) tiene menú especial

        val fabChat = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabChat)
        fabChat.setOnClickListener {
            replaceFragment(ChatBotFragment())
            tvTopTitle.text = "Asistente Nerevian"

            // Opcional: Deseleccionar los ítems del bottomNav para que no parezca
            // que estás en otra sección principal
            bottomNav.menu.setGroupCheckable(0, true, false)
            for (i in 0 until bottomNav.menu.size()) {
                bottomNav.menu.getItem(i).isChecked = false
            }
            bottomNav.menu.setGroupCheckable(0, true, true)
        }
        // Configura el menú según el rol
        bottomNav.menu.clear()
        if (isAgent) {
            bottomNav.inflateMenu(R.menu.bottom_nav_agent)
            if (savedInstanceState == null) {
                replaceFragment(OrderListFragment())
                tvTopTitle.text = "Pedidos de Clientes"
            }
        } else {
            bottomNav.inflateMenu(R.menu.bottom_nav_menu)
            if (savedInstanceState == null) {
                replaceFragment(InicioFragment())
                tvTopTitle.text = "NEREVIAN - TETRIS"
            }
        }

        // Maneja los clics del menú inferior
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // Menú normal
                R.id.nav_inicio -> {
                    replaceFragment(InicioFragment())
                    tvTopTitle.text = "NEREVIAN - TETRIS"
                    true
                }
                R.id.nav_presupuestos -> {
                    replaceFragment(PresupuestoFragment())
                    tvTopTitle.text = "Mis Presupuestos"
                    true
                }
                R.id.nav_tracking -> {
                    replaceFragment(TrackStatusFragment())
                    tvTopTitle.text = "Seguimiento"
                    true
                }
                R.id.nav_docs -> {
                    replaceFragment(DocsFragment())
                    tvTopTitle.text = "Documentos"
                    true
                }
                // Menú agente
                R.id.nav_pedidos -> {
                    replaceFragment(OrderListFragment())
                    tvTopTitle.text = "Pedidos de Clientes"
                    true
                }
                // Compartido
                R.id.nav_perfil -> {
                    replaceFragment(PerfilFragment())
                    tvTopTitle.text = "Mi Perfil"
                    true
                }
                else -> false
            }
        }
    }

    /** Reemplaza el fragmento en el contenedor y lo añade al back stack */
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

    /** Callback requerido por AndroidFragmentApplication para salir del juego LibGDX */
    override fun exit() {
        finish()
    }
}