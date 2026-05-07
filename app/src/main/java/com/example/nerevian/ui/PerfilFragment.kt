package com.example.nerevian.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.nerevian.R
import com.example.nerevian.data.crypto.AESUtils
import com.example.nerevian.data.network.RetrofitInstance
import com.example.nerevian.data.network.SocketClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Fragmento de perfil: gestiona datos públicos y DNI (subida/descarga cifrada)
 */
class PerfilFragment : Fragment() {

    private val TAG = "PerfilFragment_DEBUG"
    private var selectedImageUri: Uri? = null

    // ==================== VISTAS ====================
    private lateinit var ivDniPreview: ImageView
    private lateinit var btnSelectDni: MaterialButton
    private lateinit var btnUploadDni: MaterialButton
    private lateinit var btnRecoverDni: MaterialButton
    private lateinit var etPersonaContacto: TextInputEditText
    private lateinit var etEmailEmpresa: TextInputEditText
    private lateinit var etTelefonoEmpresa: TextInputEditText
    private lateinit var btnCerrarSesion: MaterialButton

    // Lanzador para seleccionar imagen de galería
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            ivDniPreview.setImageURI(it)
            ivDniPreview.visibility = View.VISIBLE
            btnUploadDni.isEnabled = true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)

        val sharedPrefs = requireContext().getSharedPreferences("NerevianPrefs", Context.MODE_PRIVATE)
        val roleId = sharedPrefs.getInt("role_id", 5)

        btnCerrarSesion.setOnClickListener { cerrarSesion() }

        if (roleId == 4) {
            bloquearSeccionDni()
        } else {
            initDniListeners()
        }
        loadUserProfile()
    }

    // ==================== INICIALIZACIÓN ====================

    private fun initViews(view: View) {
        etPersonaContacto = view.findViewById(R.id.etPersonaContacto)
        etEmailEmpresa = view.findViewById(R.id.etEmailEmpresa)
        etTelefonoEmpresa = view.findViewById(R.id.etTelefonoEmpresa)
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion)
        ivDniPreview = view.findViewById(R.id.ivPreviewDni)
        btnSelectDni = view.findViewById(R.id.btnSeleccionarDni)
        btnUploadDni = view.findViewById(R.id.btnSubirDni)
        btnRecoverDni = view.findViewById(R.id.btnRecuperarDni)
        btnUploadDni.isEnabled = false
    }

    private fun initDniListeners() {
        btnSelectDni.setOnClickListener { pickImageLauncher.launch("image/*") }
        btnUploadDni.setOnClickListener { processImageUpload() }
        btnRecoverDni.setOnClickListener { processImageRecovery() }
    }

    private fun bloquearSeccionDni() {
        btnSelectDni.visibility = View.GONE
        btnUploadDni.visibility = View.GONE
        btnRecoverDni.visibility = View.GONE
        ivDniPreview.visibility = View.GONE
        Log.d(TAG, "DNI bloqueado: rol Agente")
    }

    // ==================== SUBIDA DE DNI ====================

    private fun processImageUpload() {
        val uri = selectedImageUri ?: return
        val userId = obtenerUserId() ?: return

        btnUploadDni.isEnabled = false
        btnUploadDni.text = "Enviando..."

        lifecycleScope.launch {
            val success = withContext(Dispatchers.IO) {
                runCatching {
                    // Leer imagen → cifrar con AES → enviar por socket
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val rawBytes = inputStream?.readBytes() ?: return@runCatching false
                    inputStream.close()
                    val encryptedBytes = AESUtils.getEncryptCipher().doFinal(rawBytes)
                    SocketClient.uploadDni(userId, encryptedBytes)
                }.getOrElse { e ->
                    Log.e(TAG, "Error subida: ${e.message}")
                    false
                }
            }
            restaurarBotonUpload(success)
        }
    }

    private fun restaurarBotonUpload(exito: Boolean) {
        btnUploadDni.isEnabled = true
        btnUploadDni.text = "Subir DNI Encriptado"
        val mensaje = if (exito) "✅ DNI encriptado y guardado" else "❌ Error al subir el DNI"
        showMessage(mensaje)
    }

    // ==================== RECUPERACIÓN DE DNI ====================

    private fun processImageRecovery() {
        val userId = obtenerUserId() ?: return  // Salida temprana si no hay usuario (válido por precondición)

        btnRecoverDni.isEnabled = false
        showMessage("Recuperando datos...")

        lifecycleScope.launch {
            // Resultado de la operación
            var resultado: Bitmap? = null
            var mensajeError: String? = null

            // 1. Obtener bytes cifrados desde el socket (operación de red)
            val encryptedBytes = runCatching {
                SocketClient.downloadDni(userId)
            }.getOrElse { e ->
                Log.e(TAG, "Error de red: ${e.message}")
                mensajeError = "Error de conexión: ${e.message}"
                null
            }

            // 2. Si hay datos, descifrar y decodificar en hilo de CPU
            if (encryptedBytes != null) {
                withContext(Dispatchers.Default) {
                    runCatching {
                        val decrypted = AESUtils.getDecryptCipher().doFinal(encryptedBytes)
                        BitmapFactory.decodeByteArray(decrypted, 0, decrypted.size)
                    }.getOrNull()
                }.let { bitmap ->
                    if (bitmap != null) {
                        resultado = bitmap
                    } else {
                        mensajeError = "Datos corruptos o descifrado falló (clave incorrecta)"
                    }
                }
            } else if (mensajeError == null) {
                mensajeError = "No se encontró DNI en el servidor"
            }

            // 3. Actualizar UI según el resultado (único punto de salida)
            when {
                resultado != null -> {
                    ivDniPreview.setImageBitmap(resultado)
                    ivDniPreview.visibility = View.VISIBLE
                    showMessage("✅ DNI recuperado correctamente")
                }
                else -> {
                    showMessage(mensajeError ?: "Error desconocido")
                }
            }

            // 4. Restaurar botón siempre
            btnRecoverDni.isEnabled = true
        }
    }

    // ==================== PERFIL PÚBLICO ====================

    private fun loadUserProfile() {
        val userId = obtenerUserId() ?: return
        lifecycleScope.launch {
            runCatching {
                val response = RetrofitInstance.api.getUserProfile(userId)
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        etPersonaContacto.setText("${user.name} ${user.surname}")
                        etEmailEmpresa.setText(user.email)
                        etTelefonoEmpresa.setText(user.phoneNumber ?: "Sin teléfono")
                    }
                }
            }.onFailure { e -> Log.e(TAG, "Error cargando perfil: ${e.message}") }
        }
    }

    // ==================== UTILIDADES ====================

    /** Devuelve userId de SharedPreferences, o null si no existe */
    private fun obtenerUserId(): Int? {
        val userId = requireContext().getSharedPreferences("NerevianPrefs", Context.MODE_PRIVATE)
            .getInt("user_id", -1)
        return if (userId == -1) null else userId
    }

    private fun cerrarSesion() {
        val sharedPrefs = requireContext().getSharedPreferences("NerevianPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showMessage(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}