package com.example.nerevian.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.nerevian.R
import com.example.nerevian.data.crypto.AESUtils
import com.example.nerevian.data.network.RetrofitInstance
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*

class PerfilFragment : Fragment() {

    private val TAG = "PerfilFragment_DEBUG"
    private var selectedImageUri: Uri? = null

    private lateinit var ivDniPreview: ImageView
    private lateinit var btnSelectDni: MaterialButton
    private lateinit var btnUploadDni: MaterialButton
    private lateinit var btnRecoverDni: MaterialButton
    private lateinit var etPersonaContacto: TextInputEditText
    private lateinit var etEmailEmpresa: TextInputEditText
    private lateinit var etTelefonoEmpresa: TextInputEditText

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
        initListeners()
        loadUserProfile()
    }

    private fun initViews(view: View) {
        ivDniPreview = view.findViewById(R.id.ivPreviewDni)
        btnSelectDni = view.findViewById(R.id.btnSeleccionarDni)
        btnUploadDni = view.findViewById(R.id.btnSubirDni)
        btnRecoverDni = view.findViewById(R.id.btnRecuperarDni)
        etPersonaContacto = view.findViewById(R.id.etPersonaContacto)
        etEmailEmpresa = view.findViewById(R.id.etEmailEmpresa)
        etTelefonoEmpresa = view.findViewById(R.id.etTelefonoEmpresa)
    }

    private fun initListeners() {
        btnSelectDni.setOnClickListener { pickImageLauncher.launch("image/*") }
        btnUploadDni.setOnClickListener { processImageUpload() }
        btnRecoverDni.setOnClickListener { processImageRecovery() }
    }

    private fun processImageUpload() {
        val uri = selectedImageUri ?: return

        val sharedPrefs = requireContext().getSharedPreferences("NerevianPrefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getInt("user_id", -1)

        if (userId == -1) {
            showMessage("Error: Inicie sesión de nuevo")
            return
        }

        toggleButtons(false, "Subiendo...")

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    val bytes = requireContext().contentResolver.openInputStream(uri)?.readBytes() ?: return@runCatching false

                    // 1. Encriptar
                    val encrypted = AESUtils.getEncryptCipher().doFinal(bytes)

                    // 2. Base64 (NO_WRAP es vital para Sockets)
                    val base64 = Base64.encodeToString(encrypted, Base64.NO_WRAP)

                    // 3. Enviar con formato "ID|DATA"
                    RetrofitInstance.uploadDataViaSocket("$userId|$base64")
                }.getOrDefault(false)
            }

            toggleButtons(true, "Subir DNI Encriptado")
            showMessage(if (result) "¡Guardado con éxito!" else "Error en el servidor")
        }
    }

    private fun processImageRecovery() {
        val sharedPrefs = requireContext().getSharedPreferences("NerevianPrefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getInt("user_id", -1)

        if (userId == -1) {
            showMessage("Inicie sesión para recuperar")
            return
        }

        btnRecoverDni.isEnabled = false
        lifecycleScope.launch {
            try {
                // 1. Pedir datos: "DOWNLOAD|20"
                val rawData = withContext(Dispatchers.IO) {
                    RetrofitInstance.downloadDataViaSocket("DOWNLOAD|$userId")
                }

                if (rawData.isBlank()) throw Exception("Servidor vacío")
                if (rawData.startsWith("ERROR")) throw Exception("Servidor dice: $rawData")

                // 2. Limpieza de caracteres no válidos en Base64
                val cleanBase64 = rawData.replace("[^a-zA-Z0-9+/=]".toRegex(), "").trim()

                val bitmap = withContext(Dispatchers.Default) {
                    // 3. Decodificar Base64
                    val encryptedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)

                    // 4. Desencriptar AES
                    val decryptedBytes = AESUtils.getDecryptCipher().doFinal(encryptedBytes)

                    // 5. Generar Bitmap
                    BitmapFactory.decodeByteArray(decryptedBytes, 0, decryptedBytes.size)
                }

                displayRecoveredImage(bitmap)
            } catch (e: Exception) {
                Log.e(TAG, "Error en recovery: ${e.message}")
                showMessage("Fallo: ${e.localizedMessage}")
            } finally {
                btnRecoverDni.isEnabled = true
            }
        }
    }

    private fun displayRecoveredImage(bitmap: Bitmap?) {
        if (bitmap != null) {
            ivDniPreview.setImageBitmap(bitmap)
            ivDniPreview.visibility = View.VISIBLE
            showMessage("Imagen recuperada correctamente")
        } else {
            showMessage("Error: Los datos recuperados no son válidos")
        }
    }

    private fun toggleButtons(enabled: Boolean, text: String) {
        btnUploadDni.isEnabled = enabled
        btnUploadDni.text = text
    }

    private fun showMessage(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    private fun loadUserProfile() {
        val sharedPrefs = requireContext().getSharedPreferences("NerevianPrefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getInt("user_id", -1)
        if (userId == -1) return

        lifecycleScope.launch {
            runCatching { RetrofitInstance.api.getUserProfile(userId) }.onSuccess { response ->
                if (response.isSuccessful) response.body()?.let { user ->
                    etPersonaContacto.setText("${user.name} ${user.surname}")
                    etEmailEmpresa.setText(user.email)
                    etTelefonoEmpresa.setText(user.phoneNumber ?: "N/A")
                }
            }
        }
    }
}