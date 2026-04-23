package com.example.nerevian.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
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

class PerfilFragment : Fragment() {

    private val TAG = "PerfilFragment_DEBUG"
    private var selectedImageUri: Uri? = null

    private lateinit var ivDniPreview: ImageView
    private lateinit var btnSelectDni: MaterialButton
    private lateinit var btnUploadDni: MaterialButton
    private lateinit var btnRecoverDni: MaterialButton
    private lateinit var layoutUploadedDni: LinearLayout

    private lateinit var etPersonaContacto: TextInputEditText
    private lateinit var etEmailEmpresa: TextInputEditText
    private lateinit var etTelefonoEmpresa: TextInputEditText

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            showLocalPreview(uri)
            btnUploadDni.isEnabled = true
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        setupListeners()
        loadUserProfile()
    }

    private fun setupViews(view: View) {
        ivDniPreview = view.findViewById(R.id.ivPreviewDni)
        btnSelectDni = view.findViewById(R.id.btnSeleccionarDni)
        btnUploadDni = view.findViewById(R.id.btnSubirDni)
        btnRecoverDni = view.findViewById(R.id.btnRecuperarDni)
        layoutUploadedDni = view.findViewById(R.id.layoutDniSubido)

        etPersonaContacto = view.findViewById(R.id.etPersonaContacto)
        etEmailEmpresa = view.findViewById(R.id.etEmailEmpresa)
        etTelefonoEmpresa = view.findViewById(R.id.etTelefonoEmpresa)
    }

    private fun setupListeners() {
        btnSelectDni.setOnClickListener { pickImageLauncher.launch("image/*") }
        btnUploadDni.setOnClickListener { uploadEncryptedImage() }
        btnRecoverDni.setOnClickListener { downloadAndDecryptImage() }
    }

    // --- LOGICA DE CARGA DE PERFIL ---
    private fun loadUserProfile() {
        val sharedPrefs = requireContext().getSharedPreferences("NerevianPrefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getInt("user_id", -1)

        if (userId == -1) {
            showMessage("Error: Sesión no válida")
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Llamada a api/user/profile/{id}
                val response = RetrofitInstance.api.getUserProfile(userId)

                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    Log.d(TAG, "Perfil cargado: ${user.name}")

                    withContext(Dispatchers.Main) {
                        // Sincronizado con los nombres de tu data class User
                        etPersonaContacto.setText("${user.name} ${user.surname}")
                        etEmailEmpresa.setText(user.email)
                        etTelefonoEmpresa.setText(user.phoneNumber ?: "Sin teléfono")
                    }
                } else {
                    Log.e(TAG, "Error HTTP: ${response.code()}")
                    showMessage("No se encontró el perfil (404)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error de red", e)
                showMessage("Error de conexión con el servidor")
            }
        }
    }

    // --- LOGICA DE ENCRIPTACION Y SOCKETS ---
    private fun uploadEncryptedImage() {
        val uri = selectedImageUri ?: return
        btnUploadDni.isEnabled = false
        btnUploadDni.text = "Enviando..."

        viewLifecycleOwner.lifecycleScope.launch {
            var isSuccess = false
            try {
                val originalBytes = withContext(Dispatchers.IO) {
                    requireContext().contentResolver.openInputStream(uri)?.readBytes()
                }
                if (originalBytes != null) {
                    val cipher = AESUtils.getEncryptCipher()
                    val encryptedBytes = cipher.doFinal(originalBytes)
                    val base64 = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)

                    isSuccess = withContext(Dispatchers.IO) {
                        RetrofitInstance.uploadDataViaSocket(base64)
                    }
                }
            } catch (e: Exception) { Log.e(TAG, "Error upload", e) }

            withContext(Dispatchers.Main) {
                btnUploadDni.isEnabled = true
                btnUploadDni.text = "Subir DNI Encriptado"
                if (isSuccess) {
                    layoutUploadedDni.visibility = View.VISIBLE
                    showMessage("DNI guardado correctamente")
                } else showMessage("Error al conectar con el PC")
            }
        }
    }

    private fun downloadAndDecryptImage() {
        btnRecoverDni.isEnabled = false
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val encryptedBase64 = withContext(Dispatchers.IO) {
                    RetrofitInstance.downloadDataViaSocket()
                }
                if (encryptedBase64.isNotEmpty()) {
                    val encryptedBytes = Base64.decode(encryptedBase64.trim(), Base64.DEFAULT)
                    val cipher = AESUtils.getDecryptCipher()
                    val decryptedBytes = cipher.doFinal(encryptedBytes)
                    val bitmap = BitmapFactory.decodeByteArray(decryptedBytes, 0, decryptedBytes.size)

                    withContext(Dispatchers.Main) {
                        ivDniPreview.setImageBitmap(bitmap)
                        ivDniPreview.visibility = View.VISIBLE
                        showMessage("Imagen recuperada!")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error recovery", e)
                showMessage("Error al recuperar imagen")
            } finally {
                btnRecoverDni.isEnabled = true
            }
        }
    }

    private fun showLocalPreview(uri: Uri) {
        ivDniPreview.setImageURI(uri)
        ivDniPreview.visibility = View.VISIBLE
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}