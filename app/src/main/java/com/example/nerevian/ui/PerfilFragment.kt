package com.example.nerevian.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Fragmento de perfil de usuario. Muestra datos personales y permite subir/recuperar DNI encriptado.
 * El rol Agente (roleId=4) no puede gestionar DNI.
 * Modularización: mover lógica de encriptación/Socket a un repositorio dedicado.
 */
class PerfilFragment : Fragment() {

    private val TAG = "PerfilFragment_DEBUG"
    private var selectedImageUri: Uri? = null

    // Sección DNI (encriptación)
    private lateinit var ivDniPreview: ImageView
    private lateinit var btnSelectDni: MaterialButton
    private lateinit var btnUploadDni: MaterialButton
    private lateinit var btnRecoverDni: MaterialButton

    // Sección perfil público
    private lateinit var etPersonaContacto: TextInputEditText
    private lateinit var etEmailEmpresa: TextInputEditText
    private lateinit var etTelefonoEmpresa: TextInputEditText
    private lateinit var btnCerrarSesion: MaterialButton

    // Lanzador para seleccionar imagen desde galería
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
            bloquearSeccionDni()          // Agente no accede a DNI
        } else {
            initDniListeners()             // Usuarios normales pueden subir/recuperar
        }

        loadUserProfile()
    }

    /** Inicializa las referencias a las vistas */
    private fun initViews(view: View) {
        etPersonaContacto = view.findViewById(R.id.etPersonaContacto)
        etEmailEmpresa = view.findViewById(R.id.etEmailEmpresa)
        etTelefonoEmpresa = view.findViewById(R.id.etTelefonoEmpresa)
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion)

        ivDniPreview = view.findViewById(R.id.ivPreviewDni)
        btnSelectDni = view.findViewById(R.id.btnSeleccionarDni)
        btnUploadDni = view.findViewById(R.id.btnSubirDni)
        btnRecoverDni = view.findViewById(R.id.btnRecuperarDni)
    }

    /** Configura los botones de la sección DNI */
    private fun initDniListeners() {
        btnSelectDni.setOnClickListener { pickImageLauncher.launch("image/*") }
        btnUploadDni.setOnClickListener { processImageUpload() }
        btnRecoverDni.setOnClickListener { processImageRecovery() }
    }

    /** Oculta la gestión de DNI para el rol Agente */
    private fun bloquearSeccionDni() {
        btnSelectDni.visibility = View.GONE
        btnUploadDni.visibility = View.GONE
        btnRecoverDni.visibility = View.GONE
        ivDniPreview.visibility = View.GONE
        Log.d(TAG, "DNI bloqueado para rol Agente")
    }

    /** Limpia datos de sesión y redirige a login */
    private fun cerrarSesion() {
        val sharedPrefs = requireContext().getSharedPreferences("NerevianPrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()
        Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    /** Encripta la imagen seleccionada y la envía mediante Socket */
    private fun processImageUpload() {
        val uri = selectedImageUri ?: return
        val userId = requireContext().getSharedPreferences("NerevianPrefs", Context.MODE_PRIVATE).getInt("user_id", -1)
        if (userId == -1) return

        btnUploadDni.isEnabled = false
        btnUploadDni.text = "Subiendo..."

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    val bytes = requireContext().contentResolver.openInputStream(uri)?.readBytes() ?: return@runCatching false
                    val encrypted = AESUtils.getEncryptCipher().doFinal(bytes)
                    val base64 = Base64.encodeToString(encrypted, Base64.NO_WRAP)
                    RetrofitInstance.uploadDataViaSocket("$userId|$base64")
                }.getOrDefault(false)
            }

            btnUploadDni.isEnabled = true
            btnUploadDni.text = "Subir DNI Encriptado"
            showMessage(if (result) "Guardado correctamente" else "Error al subir")
        }
    }

    /** Recupera el DNI desde el Socket, lo desencripta y muestra la imagen */
    private fun processImageRecovery() {
        val userId = requireContext().getSharedPreferences("NerevianPrefs", Context.MODE_PRIVATE).getInt("user_id", -1)
        if (userId == -1) return

        btnRecoverDni.isEnabled = false
        lifecycleScope.launch {
            try {
                val rawData = withContext(Dispatchers.IO) {
                    RetrofitInstance.downloadDataViaSocket("DOWNLOAD|$userId")
                }
                if (rawData.isBlank() || rawData.startsWith("ERROR")) throw Exception("Error")

                val cleanBase64 = rawData.replace("[^a-zA-Z0-9+/=]".toRegex(), "").trim()
                val bitmap = withContext(Dispatchers.Default) {
                    val encryptedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                    val decryptedBytes = AESUtils.getDecryptCipher().doFinal(encryptedBytes)
                    BitmapFactory.decodeByteArray(decryptedBytes, 0, decryptedBytes.size)
                }

                bitmap?.let {
                    ivDniPreview.setImageBitmap(it)
                    ivDniPreview.visibility = View.VISIBLE
                    showMessage("DNI recuperado")
                }
            } catch (e: Exception) {
                showMessage("No hay DNI en el servidor")
            } finally {
                btnRecoverDni.isEnabled = true
            }
        }
    }

    /** Carga los datos del usuario desde la API y los muestra en los campos */
    private fun loadUserProfile() {
        val userId = requireContext().getSharedPreferences("NerevianPrefs", Context.MODE_PRIVATE).getInt("user_id", -1)
        if (userId == -1) return

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getUserProfile(userId)
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        etPersonaContacto.setText("${user.name} ${user.surname}")
                        etEmailEmpresa.setText(user.email)
                        etTelefonoEmpresa.setText(user.phoneNumber ?: "N/A")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error perfil: ${e.message}")
            }
        }
    }

    /** Muestra un Toast breve */
    private fun showMessage(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}