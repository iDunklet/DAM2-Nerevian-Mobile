package com.example.nerevian.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nerevian.R
import com.example.nerevian.core.model.others.LoginRequest
import com.example.nerevian.data.network.RetrofitInstance
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // Declaracion de los componentes de la interfaz de usuario
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnForgotPassword: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializacion y configuracion estructurada
        initViews()
        setupListeners()
        runApiTests()
    }

    // Vincula las variables con los elementos del XML
    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnForgotPassword = findViewById(R.id.btnForgotPassword)
    }

    // Configura todos los eventos de interaccion del usuario
    private fun setupListeners() {
        btnForgotPassword.setOnClickListener {
            Toast.makeText(this, "Contacta con el administrador", Toast.LENGTH_SHORT).show()
        }

        btnLogin.setOnClickListener {
            handleLoginAttempt()
        }
    }

    // Valida los datos introducidos antes de hacer la peticion
    private fun handleLoginAttempt() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        performLogin(email, password)
    }

    // Ejecuta la logica de autenticacion con la API
    // Ejecuta la logica de autenticacion con la API
    // Ejecuta la logica de autenticacion con la API
    private fun performLogin(email: String, password: String) {
        setLoginButtonState(isLoading = true)

        lifecycleScope.launch {
            try {
                // 1. PRIMERA LLAMADA: LOGIN
                val request = LoginRequest(correu = email, contrasenya = password)
                val loginResponse = RetrofitInstance.authApi.login(request)

                if (loginResponse.isSuccessful && loginResponse.body() != null) {
                    val token = loginResponse.body()!!.token
                    val userId = loginResponse.body()!!.user.id // Aquí sacamos el ID (ej: 23)

                    // 2. SEGUNDA LLAMADA: OBTENER PERFIL CON ESE ID
                    fetchUserProfile(userId, token)

                } else {
                    val errorBody = loginResponse.errorBody()?.string()
                    Log.e("NEREVIAN_DEBUG", "CÓDIGO: ${loginResponse.code()} | ERROR LOGIN: $errorBody")
                    Toast.makeText(this@MainActivity, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    setLoginButtonState(isLoading = false)
                }
            } catch (e: Exception) {
                Log.e("NEREVIAN_DEBUG", "Error de conexión en login: ${e.localizedMessage}")
                Toast.makeText(this@MainActivity, "Error de red. Revisa tu conexión.", Toast.LENGTH_SHORT).show()
                setLoginButtonState(isLoading = false)
            }
        }
    }

    // Nueva función para pedir el perfil antes de entrar al Dashboard
    private suspend fun fetchUserProfile(userId: Int, token: String) {
        try {
            // Nota: Usa RetrofitInstance.api o authApi según dónde hayas puesto el @GET
            val profileResponse = RetrofitInstance.api.getUserProfile(userId)

            if (profileResponse.isSuccessful && profileResponse.body() != null) {
                val usuarioReal = profileResponse.body()!!

                val rolDelUsuario = usuarioReal.roleId
                val nombreUsuario = usuarioReal.name

                Log.d("NEREVIAN_LOGIN", "¡Perfil obtenido! Nombre: $nombreUsuario, Rol: $rolDelUsuario")

                // 3. GUARDAR DATOS Y NAVEGAR
                guardarToken(token, userId, rolDelUsuario)
                navigateToDashboard(nombreUsuario)

            } else {
                Log.e("NEREVIAN_DEBUG", "Error al obtener perfil: ${profileResponse.code()}")
                Toast.makeText(this@MainActivity, "Error al cargar tu perfil", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("NEREVIAN_DEBUG", "Error de red al pedir perfil: ${e.localizedMessage}")
            Toast.makeText(this@MainActivity, "Error de red al cargar el perfil.", Toast.LENGTH_SHORT).show()
        } finally {
            setLoginButtonState(isLoading = false)
        }
    }

    // Almacena el token JWT de forma persistente en el dispositivo


    // Controla el estado visual del boton de login para evitar multiples clicks
    private fun setLoginButtonState(isLoading: Boolean) {
        btnLogin.isEnabled = !isLoading
        btnLogin.text = if (isLoading) "Iniciando..." else "Iniciar Sesión"
    }

    // Gestiona la transicion a la pantalla principal de la aplicacion
    private fun navigateToDashboard(userName: String) {
        Toast.makeText(this, "¡Bienvenido $userName!", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Almacena el token JWT de forma persistente en el dispositivo
    private fun guardarToken(token: String, userId: Int, roleId: Int) {
        val sharedPreferences = getSharedPreferences("NerevianPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("auth_token", token)
            .putInt("user_id", userId)
            .putInt("role_id", roleId)
            .apply()

        // ---> LOGS AÑADIDOS AQUÍ PARA CONFIRMAR QUÉ SE GUARDÓ <---
        Log.d("NEREVIAN_LOGIN", "========== GUARDADO EN SHAREDPREFERENCES ==========")
        Log.d("NEREVIAN_LOGIN", "User ID guardado: ${sharedPreferences.getInt("user_id", -1)}")
        Log.d("NEREVIAN_LOGIN", "Role ID guardado: ${sharedPreferences.getInt("role_id", -1)}")
        Log.d("NEREVIAN_LOGIN", "===================================================")
    }

    // --- METODOS DE TESTEO DE LA API DE TRACKING ---

    // Agrupa las llamadas de prueba a la API
    private fun runApiTests() {
        checkTrackStatus(1)
    }

    // Modifica el estado de un track especifico
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

    // Consulta el estado actual de un track
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