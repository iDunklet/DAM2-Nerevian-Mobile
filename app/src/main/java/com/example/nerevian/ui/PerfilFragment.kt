package com.example.nerevian.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nerevian.R
import com.example.nerevian.ui.AES.AESHelper
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*
import java.io.File
import java.net.Socket
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.result.contract.ActivityResultContracts

class PerfilFragment : Fragment() {


    private val SERVER_IP = "10.0.2.2"
    private val SERVER_PORT = 8080

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val realFile = getFileFromUri(requireContext(), uri)
            if (realFile != null) {
                Toast.makeText(context, "Archivo seleccionado. Iniciando subida...", Toast.LENGTH_SHORT).show()
                uploadDni(requireContext(), realFile)
            } else {
                Toast.makeText(context, "❌ Error al leer el archivo.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Operación cancelada.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            var originalName = "documento.file"
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        originalName = it.getString(nameIndex)
                    }
                }
            }

            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File(context.cacheDir, originalName)
            val outputStream = tempFile.outputStream()

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val btnSubirDni = view.findViewById<MaterialButton>(R.id.btnSubirDni)
        val btnDescargarDni = view.findViewById<MaterialButton>(R.id.btnDescargarDni)
        val btnCerrarSesion = view.findViewById<MaterialButton>(R.id.btnCerrarSesion)

        btnSubirDni.setOnClickListener {

            filePickerLauncher.launch("*/*")
        }

        btnDescargarDni.setOnClickListener {

            val timeStamp = System.currentTimeMillis()
            val savePath = File(requireContext().filesDir, "dni_$timeStamp.jpg")

            Toast.makeText(context, "Iniciando descarga...", Toast.LENGTH_SHORT).show()
            downloadDni(requireContext(), savePath)
        }

        btnCerrarSesion.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }

    private fun uploadDni(context: Context, file: File) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val key = AESHelper.getOrCreateKey(context)
                val encryptedBytes = AESHelper.encrypt(file.readBytes(), key)

                val socket = Socket(SERVER_IP, SERVER_PORT)
                val outputStream = socket.getOutputStream()

                val dos = java.io.DataOutputStream(outputStream)

                dos.write('U'.code)

                dos.writeUTF(file.name)

                dos.write(encryptedBytes)

                dos.flush()
                socket.close()

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "✅ ¡DNI subido con éxito!", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ Error al subir: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun downloadDni(context: Context, savePath: File) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val socket = Socket(SERVER_IP, SERVER_PORT)
                val dos = java.io.DataOutputStream(socket.getOutputStream())

                dos.write('D'.code)
                dos.writeUTF("dni_temp_upload.file")
                dos.flush()

                val dis = java.io.DataInputStream(socket.getInputStream())
                val status = dis.readUTF()

                if (status == "OK") {


                    val bos = java.io.ByteArrayOutputStream()
                    val buffer = ByteArray(4096)
                    var len: Int

                    while (dis.read(buffer).also { len = it } != -1) {
                        bos.write(buffer, 0, len)
                    }

                    val encryptedBytes = bos.toByteArray()
                    socket.close()

                    val key = AESHelper.getOrCreateKey(context)
                    val decryptedBytes = AESHelper.decrypt(encryptedBytes, key)

                    savePath.writeBytes(decryptedBytes)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "✅ ¡Descarga exitosa!", Toast.LENGTH_LONG).show()
                        println("Archivo guardado en: ${savePath.absolutePath}")
                    }
                } else {
                    socket.close()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "❌ El servidor no encontró el archivo.", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ Error de conexión.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}