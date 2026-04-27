package com.example.nerevian.ui.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nerevian.R
import com.example.nerevian.ui.game.GameFragment

class InicioFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnJugarTetris = view.findViewById<View>(R.id.mobile)
        btnJugarTetris.setOnClickListener {
            val fragment = GameFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)  // Para poder volver atrás
                .commit()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = InicioFragment()
    }
}