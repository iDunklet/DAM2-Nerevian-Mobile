package com.example.nerevian.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nerevian.R
import com.example.nerevian.data.network.RetrofitInstance
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val btnForgotPassword = findViewById<MaterialButton>(R.id.btnForgotPassword)
        testapi();
        btnForgotPassword?.setOnClickListener {
            Toast.makeText(this, "Contacta con el administrador", Toast.LENGTH_SHORT).show()
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email == "cliente@nerevian.com" && password == "123456") {
                Toast.makeText(this, "¡Bienvenido Cliente!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()

            } else if (email == "agente@nerevian.com" && password == "123456") {
                Toast.makeText(this, "¡Bienvenido Agente Comercial!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DashboardAgenteActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun testapi() {
        checkTrackStatus(1)
    }

    private fun changeTrackStatus(id: Int, newStatusId: Int) {
        // Usamos lifecycleScope igual que en el GET
        lifecycleScope.launch {
            try {
                Log.d("NEREVIAN_DEBUG", "Cambiando estado de $id a $newStatusId...")

                // Llamamos a la API a través de la instancia de Retrofit
                val response = RetrofitInstance.api.changeTrackStatus(id, newStatusId)

                if (response.isSuccessful) {
                    val nuevoId = response.body()
                    Log.d("NEREVIAN_DEBUG", "¡ÉXITO al cambiar! Nuevo estado: $nuevoId")
                    Toast.makeText(this@MainActivity, "Actualizado al estado $nuevoId", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("NEREVIAN_DEBUG", "Error servidor al cambiar: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("NEREVIAN_DEBUG", "FALLO en PUT: ${e.localizedMessage}")
            }
        }
    }

    private fun checkTrackStatus(id: Int) {
        lifecycleScope.launch {
            try {
                Log.d("NEREVIAN_DEBUG", "Pidiendo estado del track $id...")
                val response = RetrofitInstance.api.getTrackStatus(id)

                if (response.isSuccessful) {
                    val data = response.body()
                    Log.d("NEREVIAN_DEBUG", "¡ÉXITO! Status: ${data?.status}")
                    Toast.makeText(this@MainActivity, "Estado: ${data?.status}", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("NEREVIAN_DEBUG", "Error servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("NEREVIAN_DEBUG", "FALLO: ${e.localizedMessage}")
            }
        }
    }
}