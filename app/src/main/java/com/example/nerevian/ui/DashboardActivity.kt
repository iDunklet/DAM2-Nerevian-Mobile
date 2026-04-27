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
import com.example.nerevian.ui.client.TrackStatusFragment
import com.example.nerevian.ui.game.GameFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val tvTopTitle = findViewById<TextView>(R.id.tvTopTitle)

        // 1. Obtenemos el Rol desde SharedPreferences
        val sharedPreferences = getSharedPreferences("NerevianPrefs", Context.MODE_PRIVATE)

        // Obtenemos el ID. Si por algo no existiera, ponemos 5 (Client) por defecto.
        val roleId = sharedPreferences.getInt("role_id", 5)

        // 2. Condición: SOLO si es el ID 4 (Agent) verá el menú de agente.
        // Cualquier otro ID (1, 2, 3 o 5) entrará en el "else" (Menú Normal).
        val isAgent = (roleId == 4)

        bottomNav.menu.clear()

        if (isAgent) {
            // VISTA PARA AGENTE
            bottomNav.inflateMenu(R.menu.bottom_nav_agent)
            if (savedInstanceState == null) {
                replaceFragment(OrderListFragment())
                tvTopTitle.text = "Pedidos de Clientes"
            }
        } else {
            // VISTA NORMAL (Para Admin, User, Operator y Client)
            bottomNav.inflateMenu(R.menu.bottom_nav_menu)
            if (savedInstanceState == null) {
                replaceFragment(InicioFragment())
                tvTopTitle.text = "NEREVIAN - TETRIS"
            }
        }

        // 3. Listener de clics (Mantenemos todos los IDs por si acaso)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // IDs del Menú Normal
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

                // ID del Menú Agente
                R.id.nav_pedidos -> {
                    replaceFragment(OrderListFragment())
                    tvTopTitle.text = "Pedidos de Clientes"
                    true
                }

                // ID compartido
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
            // Solo añadimos a la pila si queremos que el botón "Atrás" de Android devuelva al fragmento anterior
            // Usualmente en la navegación base (BottomNav) no se suele hacer addToBackStack,
            // pero lo mantengo igual que en tu código original para no romper tu flujo.
            .addToBackStack(null)
            .commit()
    }

    override fun exit() {
        finish()
    }
}