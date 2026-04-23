package com.example.nerevian.ui

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilFragment : Fragment() {

    // ------------------------------------------------------------------------
    // GLOBAL VIEW VARIABLES
    // ------------------------------------------------------------------------
    private var selectedImageUri: Uri? = null

    private lateinit var ivDniPreview: ImageView
    private lateinit var btnSelectDni: MaterialButton
    private lateinit var btnUploadDni: MaterialButton
    private lateinit var btnRecoverDni: MaterialButton
    private lateinit var layoutUploadedDni: LinearLayout

    // ------------------------------------------------------------------------
    // INTENT LAUNCHERS
    // ------------------------------------------------------------------------
    // Selector nativo de Android para obtener el archivo de la galería/archivos
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            showLocalPreview(uri)
            btnUploadDni.isEnabled = true
            showMessage("Archivo preparado para enviar")
        }
    }

    // ------------------------------------------------------------------------
    // FRAGMENT LIFECYCLE
    // ------------------------------------------------------------------------
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modularización: Separamos la inicialización y los eventos en funciones limpias
        setupViews(view)
        setupListeners()
    }

    // ------------------------------------------------------------------------
    // INITIAL CONFIGURATION
    // ------------------------------------------------------------------------
    private fun setupViews(view: View) {
        ivDniPreview = view.findViewById(R.id.ivPreviewDni)
        btnSelectDni = view.findViewById(R.id.btnSeleccionarDni)
        btnUploadDni = view.findViewById(R.id.btnSubirDni)
        btnRecoverDni = view.findViewById(R.id.btnRecuperarDni)
        layoutUploadedDni = view.findViewById(R.id.layoutDniSubido)
    }

    private fun setupListeners() {
        // Evento para abrir la galería
        btnSelectDni.setOnClickListener {
            pickImageLauncher.launch("*/*")
        }

        // Evento para encriptar y subir
        btnUploadDni.setOnClickListener {
            uploadEncryptedImage()
        }

        // Evento para descargar, desencriptar y visualizar
        btnRecoverDni.setOnClickListener {
            downloadAndDecryptImage()
        }
    }

    // ------------------------------------------------------------------------
    // BUSINESS LOGIC: UPLOAD (ENCRYPTION)
    // ------------------------------------------------------------------------
    private fun uploadEncryptedImage() {
        val uri = selectedImageUri ?: return

        btnUploadDni.isEnabled = false
        btnUploadDni.text = "Encriptando y enviando..."

        viewLifecycleOwner.lifecycleScope.launch {
            var isSuccess = false

            try {
                // 1. Leer bytes del archivo local (I/O Coroutine)
                val originalImageBytes = withContext(Dispatchers.IO) {
                    requireContext().contentResolver.openInputStream(uri)?.readBytes()
                }

                if (originalImageBytes != null) {
                    // 2. Encriptar los bytes usando AES
                    val cipher = AESUtils.getEncryptCipher()
                    val encryptedBytes = cipher.doFinal(originalImageBytes)

                    // 3. Convertir a Base64 para envío seguro por Sockets (NO_WRAP evita saltos de línea)
                    val imageBase64 = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)

                    // 4. Enviar mediante el Socket usando el nuevo método en inglés
                    isSuccess = withContext(Dispatchers.IO) {
                        RetrofitInstance.uploadDataViaSocket(imageBase64)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 5. Actualizar la Interfaz de Usuario (Main Coroutine)
            withContext(Dispatchers.Main) {
                btnUploadDni.isEnabled = true
                btnUploadDni.text = "Subir DNI Encriptado"

                if (isSuccess) {
                    layoutUploadedDni.visibility = View.VISIBLE
                    showMessage("DNI encriptado y guardado en tu PC")
                } else {
                    showMessage("Fallo al conectar con el servidor local")
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // BUSINESS LOGIC: DOWNLOAD (DECRYPTION)
    // ------------------------------------------------------------------------
    private fun downloadAndDecryptImage() {
        btnRecoverDni.isEnabled = false
        btnRecoverDni.text = "Recuperando..."

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 1. Solicitar el archivo Base64 al servidor
                val encryptedBase64Image = withContext(Dispatchers.IO) {
                    RetrofitInstance.downloadDataViaSocket()
                }

                Log.d("DNI_DEBUG", "2. Datos recibidos. Longitud: ${encryptedBase64Image.length}")

                if (encryptedBase64Image.isNotEmpty()) {
                    // 2. Limpiamos el String de espacios o saltos de línea basura del Socket
                    val cleanBase64 = encryptedBase64Image.replace("\n", "").replace("\r", "").trim()

                    // 3. Decodificar de Base64 a ByteArray (Usamos DEFAULT para mayor tolerancia)
                    val encryptedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)

                    // 4. Desencriptar usando AES
                    val cipher = AESUtils.getDecryptCipher()
                    val decryptedBytes = cipher.doFinal(encryptedBytes)

                    // 5. Convertir los bytes puros en un Mapa de Bits (Imagen)
                    val bitmap = BitmapFactory.decodeByteArray(decryptedBytes, 0, decryptedBytes.size)

                    // 6. Mostrar la imagen desencriptada en el Hilo Principal
                    withContext(Dispatchers.Main) {
                        if (bitmap != null) {
                            ivDniPreview.setImageBitmap(bitmap)
                            ivDniPreview.visibility = View.VISIBLE
                            showMessage("¡DNI recuperado y desencriptado con éxito!")
                        } else {
                            showMessage("Error: Los datos no forman una imagen válida.")
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showMessage("El servidor no devolvió ninguna imagen")
                    }
                }
            } catch (e: IllegalArgumentException) {
                // Capturamos específicamente el error de Base64 para saber si sigue fallando
                Log.e("DNI_DEBUG", "Error de Base64: La cadena está corrupta o incompleta.", e)
                withContext(Dispatchers.Main) {
                    showMessage("Error de formato: El archivo está corrupto")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showMessage("Error al recuperar o desencriptar el DNI")
                }
            } finally {
                withContext(Dispatchers.Main) {
                    btnRecoverDni.isEnabled = true
                    btnRecoverDni.text = "Recuperar DNI del Servidor"
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // HELPER FUNCTIONS
    // ------------------------------------------------------------------------
    private fun showLocalPreview(uri: Uri) {
        try {
            ivDniPreview.setImageURI(uri)
            ivDniPreview.visibility = View.VISIBLE
        } catch (e: Exception) {
            ivDniPreview.visibility = View.GONE
            e.printStackTrace()
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}