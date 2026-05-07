package com.example.nerevian.ui

import android.os.Bundle
import android.util.Log // Importante para los logs
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nerevian.R
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ChatBotFragment : Fragment() {

    private val TAG = "CHAT_LOG" // Etiqueta para filtrar en Logcat
    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: MaterialButton
    private lateinit var adapter: ChatAdapter

    private val messages = mutableListOf<ChatMessage>()
    private val client = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_bot, container, false)

        rvMessages = view.findViewById(R.id.rvMessages)
        etMessage = view.findViewById(R.id.etMessage)
        btnSend = view.findViewById(R.id.btnSend)

        adapter = ChatAdapter(messages)
        rvMessages.layoutManager = LinearLayoutManager(context)
        rvMessages.adapter = adapter

        if (messages.isEmpty()) {
            addMessage("¡Hola! Soy el asistente de Nerevian. ¿En qué puedo ayudarte hoy?", false)
        }

        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) handleUserMessage(text)
        }

        // Borra el setOnKeyListener anterior y usa solo este:
        etMessage.setOnEditorActionListener { _, actionId, event ->
            // Detecta tanto el botón "Enviar" del teclado como la tecla Enter física
            val isEnterAction = (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            val isSendAction = (actionId == EditorInfo.IME_ACTION_SEND)

            if (isSendAction || isEnterAction) {
                val text = etMessage.text.toString().trim()
                if (text.isNotEmpty()) {
                    handleUserMessage(text)
                }
                true // Indica que ya manejamos el evento
            } else {
                false
            }
        }

        return view
    }

    private fun handleUserMessage(text: String) {
        Log.d(TAG, "Enviando mensaje de usuario: $text")
        addMessage(text, true)
        etMessage.text.clear()

        lifecycleScope.launch {
            val response = callN8nWebhook(text)

            if (response != null) {
                Log.d(TAG, "Respuesta recibida con éxito: $response")
                // Si n8n devuelve un JSON, aquí podrías necesitar parsearlo
                addMessage(response, false)
            } else {
                Log.e(TAG, "La respuesta del servidor fue NULL")
                addMessage("Lo siento, no he podido conectar con el servidor.", false)
            }
        }
    }

    private fun addMessage(text: String, isUser: Boolean) {
        messages.add(ChatMessage(text, isUser))
        adapter.notifyItemInserted(messages.size - 1)
        rvMessages.scrollToPosition(messages.size - 1)
    }

    private suspend fun callN8nWebhook(userText: String): String? = withContext(Dispatchers.IO) {
        try {
            val url = "https://n8n.nerevian.xyz/webhook/3e2f1022-a06a-40d0-a401-077128195595/chat"
            Log.d(TAG, "Llamando a la URL: $url")

            val json = JSONObject()
            json.put("chatInput", userText)

            val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                Log.d(TAG, "Código HTTP: ${response.code}")

                if (response.isSuccessful) {
                    val resBody = response.body?.string()
                    Log.d(TAG, "Cuerpo de respuesta: $resBody")
                    resBody
                } else {
                    Log.e(TAG, "Error en la petición: ${response.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "EXCEPCIÓN EN LA LLAMADA: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}