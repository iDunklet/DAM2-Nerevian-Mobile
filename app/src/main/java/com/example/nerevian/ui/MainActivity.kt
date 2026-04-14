package com.example.nerevian.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nerevian.R
import com.example.nerevian.data.network.LoginRequest
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

        testapi()

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

            // Desactivamos el botón mientras carga
            btnLogin.isEnabled = false
            btnLogin.text = "Iniciando..."

            // 1. ABRIMOS LA CORRUTINA PARA LA LLAMADA DE RED
            lifecycleScope.launch {
                try {
                    // 2. CREAMOS LA PETICIÓN Y LLAMAMOS A LA API
                    val request = LoginRequest(correu = email, contrasenya = password)
                    val response = RetrofitInstance.authApi.login(request)

                    // 3. COMPROBAMOS LA RESPUESTA
                    if (response.isSuccessful && response.body() != null) {
                        val token = response.body()!!.token
                        val usuarioLogueado = response.body()!!.user // Aquí usamos tu modelo User

                        guardarToken(token)

                        Toast.makeText(this@MainActivity, "¡Bienvenido ${usuarioLogueado.name}!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("NEREVIAN_DEBUG", "CÓDIGO: ${response.code()} | ERROR DEL SERVER: $errorBody")
                        Toast.makeText(this@MainActivity, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("NEREVIAN_DEBUG", "Error de conexión: ${e.localizedMessage}")
                    Toast.makeText(this@MainActivity, "Error de red. Revisa tu conexión.", Toast.LENGTH_SHORT).show()
                } finally {
                    // Volvemos a activar el botón pase lo que pase
                    btnLogin.isEnabled = true
                    btnLogin.text = "Iniciar Sesión"
                }
            }
        }
    }

    // Esta función guarda el token de forma segura en el móvil
    private fun guardarToken(token: String) {
        val sharedPreferences = getSharedPreferences("NerevianPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    // --- TUS MÉTODOS DE TESTEO QUE YA TENÍAS ---
    private fun testapi() {
        checkTrackStatus(1)
    }

    private fun changeTrackStatus(id: Int, newStatusId: Int) {
        lifecycleScope.launch {
            try {
                Log.d("NEREVIAN_DEBUG", "Cambiando estado de $id a $newStatusId...")
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